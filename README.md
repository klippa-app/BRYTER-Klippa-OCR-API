# BRYTER-Klippa-OCR-API

:rocket: *Create smart document processes in BRYTER using the Klippa OCR API* :rocket:

**The source code of the BRYTER Klippa OCR command to do document parsing directly from BRYTER.**

## Action usage
Please be aware you need to have a Klippa OCR API key to use this action.
If you would like to use our API, please contact us [here](https://www.klippa.com/en/ocr/ocr-api)

# Actions

### parse-document

Parse a document using the Klippa OCR API. Parses a GIF, PNG, JPG, HEIC/HEIF or PDF file.

**Input fields:**

required: ``base-url``: ``https://custom-ocr.klippa.com/api/v1``

required: ``api-key``: ``{your-api-key}``

optional: ``document-url``: The document to scan as a file available at this URL.

optional: ``document-path``: The document to scan as a file path.

optional: ``template``: The template to use for parsing. Empty for default parsing. e.g ``"financial_full"``

optional: ``pdf-text-extraction``: Use full when you want the best quality scan, use fast when you want fast scan results. Fast will try to extract the text from the PDF. Full will actually scan the full PDF, which is slower.

optional: ``user-data``: Extra metadata in JSON format to give to the parser. Only works with templates that are configured to accept user data.

optional: ``user-data-external-id``: The external ID of the user data set.

optional: ``hash-duplicate-group-id``: An identifier to use when saving/detecting hash duplicates. This way you can allow to have the same document scanned more than once for multiple groups. When doing a scan, the combination of the Hash Group ID and the document Hash will be used to detect duplicates. This value is saved hashed on our side. Common use cases: Company ID, Campaign ID, User ID.

**Output field:**

required: ``ocr-output``: The JSON response as a String.

More information about the Klippa Custom OCR API [here](https://custom-ocr.klippa.com/docs).

## About Klippa

[Klippa](https://www.klippa.com/en) is a scale-up from [Groningen, The Netherlands](https://goo.gl/maps/CcCGaPTBz3u8noSd6) and was founded in 2015 by six Dutch IT specialists with the goal to digitize paper processes with modern technologies.

We help clients enhance the effectiveness of their organization by using machine learning and OCR. Since 2015 more than a 1000 happy clients have been served with a variety of the software solutions that Klippa offers. Our passion is to help our clients to digitize paper processes by using smart apps, accounts payable software and data extraction by using OCR.

## License

The MIT License (MIT)
