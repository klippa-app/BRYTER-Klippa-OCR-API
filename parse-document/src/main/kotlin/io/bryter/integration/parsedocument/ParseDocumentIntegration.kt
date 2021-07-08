package io.bryter.integration.parsedocument

import io.bryter.integration.*
import io.bryter.integration.parsedocument.actions.ParseDocument
import io.bryter.integration.parsedocument.datasources.PDFTextExtraction

class ParseDocumentIntegration(
    override val integrationContext: IntegrationContext
) : AbstractBryterIntegration(integrationContext) {

    private val logger by classKLogger()

    override val name: String = "Klippa OCR API"
    override val description: String = "A BRYTER Action to call the Klippa OCR API. The Klippa OCR API provides real-time document extraction using OCR and Machine Learning, just send us the document and we provide you with the document content in a structured way to automate your document workflow."

    override val actions: List<ExecutableAction> = listOf(ParseDocument(createDefaultApiClient("KlippaOCRApi")))
    override val dataSources: List<ExecutableDataSource> = listOf(PDFTextExtraction())
}
