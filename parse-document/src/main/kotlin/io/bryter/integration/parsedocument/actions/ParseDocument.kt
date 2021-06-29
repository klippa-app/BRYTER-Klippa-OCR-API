package io.bryter.integration.parsedocument.actions

import io.bryter.integration.*
import io.bryter.integration.ActionFieldType.STRING
import io.bryter.integration.parsedocument.MultipartRequest.Companion.postMultipartFormData
import io.bryter.integration.parsedocument.datasources.PDF_TEXT_EXTRACTION_SOURCE_ID
import java.io.*
import java.math.BigInteger
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.*


class ParseDocument : ExecutableAction {
    override val id: String = "parse-document"
    override val description: String = "Parse a document using the Klippa OCR API. Parses a GIF, PNG, JPG, HEIC/HEIF or PDF file."
    override val label: String = "Parse document"

    private val httpClient: HttpClient = HttpClient.newHttpClient()

    companion object {

        val BASE_PATH = ActionInputField (
            type = STRING,
            name = "base-path",
            label = "Klippa OCR API base path",
            required = true,
            description = "The base path to the Klippa API.",
            placeholder = "https://custom-ocr.klippa.com/api/v1",
        )

        val API_KEY = ActionInputField (
            type = STRING,
            name = "api-key",
            label = "API Key",
            required = true,
            description = "The auth key provided by Klippa",
        )

        val DOCUMENT_URL = ActionInputField (
            type = STRING,
            name = "document-url",
            label = "Document URL",
            required = false,
            description = "The document to scan as a file available at this URL.",
        )

        val DOCUMENT_PATH = ActionInputField (
            type = ActionFieldType.FILE,
            name = "document-path",
            label = "Document Path",
            required = false,
            description = "The document to scan as a file path.",
        )

        val TEMPLATE = ActionInputField (
            type = STRING,
            name = "template",
            label = "Template",
            required = true,
            description =  "The template to use for parsing. Empty for default parsing.",
        )

        val PDF_TEXT_EXTRACTION = ActionInputField (
            type = STRING,
            name = "pdf-text-extraction",
            label = "PDF Text extraction",
            required = true,
            description = "Use full when you want the best quality scan, use fast when you want fast scan results. Fast will try to extract the text from the PDF. Full will actually scan the full PDF, which is slower.",
            dataSource = DataSourceIdentifier(PDF_TEXT_EXTRACTION_SOURCE_ID)
        )

        val USER_DATA = ActionInputField (
            type = STRING,
            name = "user-data",
            label = "User Data",
            required = false,
            description = "Extra metadata in JSON format to give to the parser. Only works with templates that are configured to accept user data.",
        )

        val USER_DATA_EXTERNAL_ID = ActionInputField (
            type = STRING,
            name = "user-data-external-id",
            label = "User Data Set External ID",
            required = false,
            description = "The external ID of the user data set.",
        )

        val HASH_DUPLICATE_GROUP_ID = ActionInputField (
            type = STRING,
            name = "hash-duplicate-group-id",
            label = "Hash Duplicate Group ID",
            required = false,
            description = "An identifier to use when saving/detecting hash duplicates. This way you can allow to have the same document scanned more than once for multiple groups. When doing a scan, the combination of the Hash Group ID and the document Hash will be used to detect duplicates. This value is saved hashed on our side. Common use cases: Company ID, Campaign ID, User ID."
        )

        val OCR_RESPONSE = ActionOutputField (
            required = true,
            type = STRING,
            name = "ocr-output",
            label = "The OCR output in JSON",
            description = "The JSON response as a String."
        )

    }

    override fun getActionMetadata(context: ActionMetadataContext): Outcome<ActionMetadata> =
        with(context) {
            metadata
                .inputField(BASE_PATH)
                .inputField(API_KEY)
                .inputField(DOCUMENT_URL)
                .inputField(DOCUMENT_PATH)
                .inputField(TEMPLATE)
                .inputField(PDF_TEXT_EXTRACTION)
                .inputField(USER_DATA)
                .inputField(USER_DATA_EXTERNAL_ID)
                .inputField(HASH_DUPLICATE_GROUP_ID)
                .outputField(OCR_RESPONSE)
            complete()
        }

    override fun execute(context: ActionExecutionContext): Outcome<ActionOutputs> =
        with(context) {

            val data: MutableMap<String, Any> = LinkedHashMap()

            val apiKey = inputs.string().required(API_KEY.name)
            val basePath = inputs.string().required(BASE_PATH.name) + "/parseDocument"

            val filePath = inputs.file().optional(DOCUMENT_PATH.name)
            if (filePath != null && filePath.fileUrl.isNotEmpty()) {
                val file = File(filePath.fileUrl)
                data["document"] = file
            }

            val fileUrl = inputs.string().optional(DOCUMENT_URL.name)
            if (fileUrl != null && fileUrl.isNotEmpty()) {
                data["url"] = fileUrl
            }

            val template = inputs.string().required(TEMPLATE.name)
            if (template.isNotEmpty()) {
                data["template"] = template
            }

            val pdfTextExtraction = inputs.string().required(PDF_TEXT_EXTRACTION.name)
            if (pdfTextExtraction.isNotEmpty()) {
                data["pdf_text_extraction"] = pdfTextExtraction
            }

            val userData = inputs.string().optional(USER_DATA.name)
            if (userData != null && userData.isNotEmpty()) {
                data["user_data"] = userData
            }

            val userDataSetExternalId = inputs.string().optional(USER_DATA_EXTERNAL_ID.name)
            if (userDataSetExternalId != null && userDataSetExternalId.isNotEmpty()) {
                data["user_data_set_external_id"] = userDataSetExternalId
            }

            val hashDuplicateGroup = inputs.string().optional(HASH_DUPLICATE_GROUP_ID.name)
            if (hashDuplicateGroup != null && hashDuplicateGroup.isNotEmpty()) {
                data["hash_duplicate_group_id"] = hashDuplicateGroup
            }

            val boundary: String = BigInteger(35, Random()).toString()

            val request = HttpRequest.newBuilder()
                .uri(URI.create(basePath))
                .postMultipartFormData(boundary, data)
                .header("X-Auth-Key", apiKey)
                .build()

            val response = httpClient.send(request, HttpResponse.BodyHandlers.ofString())

            outputs.string("ocr-output", response.body())

            complete()
        }
}
