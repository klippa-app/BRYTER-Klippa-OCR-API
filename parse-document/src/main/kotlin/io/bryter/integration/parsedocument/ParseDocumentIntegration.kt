package io.bryter.integration.parsedocument

import io.bryter.integration.AbstractBryterIntegration
import io.bryter.integration.ExecutableAction
import io.bryter.integration.ExecutableDataSource
import io.bryter.integration.IntegrationContext
import io.bryter.integration.classKLogger
import io.bryter.integration.parsedocument.actions.ParseDocument
import io.bryter.integration.parsedocument.datasources.PDFTextExtraction

class ParseDocumentIntegration(
    override val integrationContext: IntegrationContext
) : AbstractBryterIntegration(integrationContext) {

    private val logger by classKLogger()

    override val name: String = "Klippa OCR API"
    override val description: String = "A BRYTER Action to call the Klippa OCR API. The Klippa OCR API provides real-time document extraction using OCR and Machine Learning, just send us the document and we provide you with the document content in a structured way to automate your document workflow."

    override val actions: List<ExecutableAction> = listOf(ParseDocument())
    override val dataSources: List<ExecutableDataSource> = listOf(PDFTextExtraction())

    // extract expected secrets which should be documented in the README.md
    private val exampleSecret by lazy {
        integrationContext.getSecretValue("parse-document-key") // You can also extract in actions or data sources
    }

    // extract expected environment variables which should be documented in the README.md
    private val exampleEnvVar by lazy {
        integrationContext.getEnvVar("parse-document-env-var").also {
            logger.debug { "Got Env var value of 'parse-document-env-var': '$it'" }
        }
    }
}