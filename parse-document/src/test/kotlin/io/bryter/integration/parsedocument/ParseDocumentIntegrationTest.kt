package io.bryter.integration.parsedocument

import io.bryter.integration.parsedocument.actions.ParseDocument
import io.bryter.integration.parsedocument.datasources.PDFTextExtraction
import io.bryter.integration.test.createIntegrationContext
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS

@TestInstance(PER_CLASS)
class ParseDocumentIntegrationTest {
    /*
    A faster test that calls the integration directly on the Kotlin level.
    */

    private val integration = ParseDocumentIntegration(createIntegrationContext())

    @Test
    fun `basic properties should be provided`() {
        with(integration) {
            assertThat(this.name).contains("Klippa OCR API")
            assertThat(this.description).contains("A BRYTER Action to call the Klippa OCR API. The Klippa OCR API provides real-time document extraction using OCR and Machine Learning, just send us the document and we provide you with the document content in a structured way to automate your document workflow.")
        }
    }
}
