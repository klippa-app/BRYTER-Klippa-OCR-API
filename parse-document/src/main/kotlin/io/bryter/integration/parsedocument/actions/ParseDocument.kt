package io.bryter.integration.parsedocument.actions

import io.bryter.integration.*
import io.bryter.integration.parsedocument.datasources.PDF_TEXT_EXTRACTION_SOURCE_ID
import org.http4k.core.*
import org.http4k.core.Method.POST
import org.http4k.lens.MultipartFormField
import org.http4k.lens.MultipartFormFile


class ParseDocument(
    private val ocrApiClient: HttpHandler
): ExecutableAction {
    override val id: String = "parse-document"
    override val description: String = "Parse a document using the Klippa OCR API. Parses a GIF, PNG, JPG, HEIC/HEIF or PDF file."
    override val label: String = "Parse document"

    companion object {
        val DOCUMENT_URL = stringInputField(
            name = "document-url",
            label = "Document URL",
            description = "The document to scan as a file available at this URL.",
        )

        val DOCUMENT_PATH = fileInputField(
            name = "document-path",
            label = "Document Path",
            description = "The document to scan as a file path.",
        )

        val TEMPLATE = requiredStringInputField(
            name = "template",
            label = "Template",
            description =  "The template to use for parsing. Empty for default parsing.",
        )

        val PDF_TEXT_EXTRACTION = requiredStringInputField (
            name = "pdf-text-extraction",
            label = "PDF Text extraction",
            description = "Use full when you want the best quality scan, use fast when you want fast scan results. Fast will try to extract the text from the PDF. Full will actually scan the full PDF, which is slower.",
            dataSource = DataSourceIdentifier(PDF_TEXT_EXTRACTION_SOURCE_ID)
        )

        val USER_DATA = stringInputField (
            name = "user-data",
            label = "User Data",
            description = "Extra metadata in JSON format to give to the parser. Only works with templates that are configured to accept user data.",
        )

        val USER_DATA_EXTERNAL_ID = stringInputField (
            name = "user-data-external-id",
            label = "User Data Set External ID",
            description = "The external ID of the user data set.",
        )

        val HASH_DUPLICATE_GROUP_ID = stringInputField (
            name = "hash-duplicate-group-id",
            label = "Hash Duplicate Group ID",
            description = "An identifier to use when saving/detecting hash duplicates. This way you can allow to have the same document scanned more than once for multiple groups. When doing a scan, the combination of the Hash Group ID and the document Hash will be used to detect duplicates. This value is saved hashed on our side. Common use cases: Company ID, Campaign ID, User ID."
        )

        val OCR_RESPONSE = requiredStringOutputField (
            name = "ocr-output",
            label = "The OCR output in JSON",
            description = "The JSON response as a String."
        )
    }

    override fun getActionMetadata(context: ActionMetadataContext): Outcome<ActionMetadata> =
        with(context) {
            metadata
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
            var body = MultipartFormBody()

            val apiKey by lazy {
                integrationContext.getSecretValue("ocr-api-key")
            }

            val basePath by lazy {
                integrationContext.getEnvVar("base-url") + "/parseDocument"
            }

            val filePath = inputs.file().optional(DOCUMENT_PATH.name)
            if (filePath != null && filePath.fileUrl.isNotEmpty()) {
                val file: FileInput = filePath
                body = body.plus("document" to MultipartFormFile(file.fileName, ContentType.OCTET_STREAM, file.inputStream()))
            }

            val fileUrl = inputs.string().optional(DOCUMENT_URL.name)
            if (fileUrl != null && fileUrl.isNotEmpty()) {
                body = body.plus("url" to MultipartFormField(fileUrl))
            }

            val template = inputs.string().required(TEMPLATE.name)
            if (template.isNotEmpty()) {
                body = body.plus("template" to MultipartFormField(template))
            }

            val pdfTextExtraction = inputs.string().required(PDF_TEXT_EXTRACTION.name)
            if (pdfTextExtraction.isNotEmpty()) {
                body = body.plus("pdf_text_extraction" to MultipartFormField(pdfTextExtraction))
            }

            val userData = inputs.string().optional(USER_DATA.name)
            if (userData != null && userData.isNotEmpty()) {
                body = body.plus("user_data" to MultipartFormField(userData))
            }

            val userDataSetExternalId = inputs.string().optional(USER_DATA_EXTERNAL_ID.name)
            if (userDataSetExternalId != null && userDataSetExternalId.isNotEmpty()) {
                body = body.plus("user_data_set_external_id" to MultipartFormField(userDataSetExternalId))
            }

            val hashDuplicateGroup = inputs.string().optional(HASH_DUPLICATE_GROUP_ID.name)
            if (hashDuplicateGroup != null && hashDuplicateGroup.isNotEmpty()) {
                body = body.plus("hash_duplicate_group_id" to MultipartFormField(hashDuplicateGroup))
            }

            val headers = listOf("X-Auth-Key" to apiKey, "content-type" to "multipart/form-data; boundary=${body.boundary}")

            val request = Request(POST, basePath)
                .headers(headers)
                .body(body)

            val response = performRequest(request)

            outputs.string("ocr-output", response.body.toString())

            complete()
        }

    private val performRequest = object : HttpHandler {
        override fun invoke(request: Request): Response {
            return ocrApiClient(request)
        }
    }

}
