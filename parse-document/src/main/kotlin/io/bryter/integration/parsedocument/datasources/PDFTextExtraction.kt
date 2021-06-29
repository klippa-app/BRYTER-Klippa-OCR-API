package io.bryter.integration.parsedocument.datasources

import io.bryter.integration.DataSourceExecutionContext
import io.bryter.integration.DataSourceOutput
import io.bryter.integration.ExecutableDataSource
import io.bryter.integration.Outcome

const val PDF_TEXT_EXTRACTION_SOURCE_ID = "pdf-text-extraction"

class PDFTextExtraction : ExecutableDataSource {
    override val id: String = PDF_TEXT_EXTRACTION_SOURCE_ID
    override val description: String = ""
    override val label: String = ""

    override fun execute(context: DataSourceExecutionContext): Outcome<DataSourceOutput> {
        context.output.item("Fast", "fast")
        context.output.item("Full", "full")
        return context.complete()
    }
}