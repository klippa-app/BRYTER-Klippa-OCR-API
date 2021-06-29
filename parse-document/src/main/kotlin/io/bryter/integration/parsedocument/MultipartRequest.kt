package io.bryter.integration.parsedocument

import java.io.File
import java.net.http.HttpRequest
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.util.ArrayList

class MultipartRequest {

    companion object {
        fun HttpRequest.Builder.postMultipartFormData(boundary: String, data: Map<String, Any>): HttpRequest.Builder {
            val byteArrays = ArrayList<ByteArray>()
            val separator = "--$boundary\r\nContent-Disposition: form-data; name=".toByteArray(StandardCharsets.UTF_8)

            for (entry in data.entries) {
                byteArrays.add(separator)
                when(entry.value) {
                    is File -> {
                        val file = entry.value as File
                        val path = Path.of(file.toURI())
                        val mimeType = Files.probeContentType(path)
                        byteArrays.add("\"${entry.key}\"; filename=\"${path.fileName}\"\r\nContent-Type: $mimeType\r\n\r\n".toByteArray(
                            StandardCharsets.UTF_8))
                        byteArrays.add(Files.readAllBytes(path))
                        byteArrays.add("\r\n".toByteArray(StandardCharsets.UTF_8))
                    }
                    else -> byteArrays.add("\"${entry.key}\"\r\n\r\n${entry.value}\r\n".toByteArray(StandardCharsets.UTF_8))
                }
            }
            byteArrays.add("--$boundary--".toByteArray(StandardCharsets.UTF_8))

            this.header("Content-Type", "multipart/form-data;boundary=$boundary")
                .POST(HttpRequest.BodyPublishers.ofByteArrays(byteArrays))
            return this
        }
    }
}