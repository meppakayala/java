import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class MemoryEfficientPdfMerger {

    /**
     * Merges pages from a list of PDF input streams into a single byte array output stream
     * in a memory-efficient manner. Each input stream is processed and closed
     * individually.
     *
     * @param pdfInputStreams A list of InputStream objects, each representing a PDF document.
     * It is the caller's responsibility to ensure these streams are properly
     * closed after this method returns.
     * @return ByteArrayOutputStream containing the merged PDF. The caller is responsible
     * for closing this stream when finished.
     * @throws IOException If an error occurs during PDF processing.
     */
    public ByteArrayOutputStream mergePdfPages(List<InputStream> pdfInputStreams) throws IOException {
        PDFMergerUtility merger = new PDFMergerUtility();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        merger.setDestinationStream(outputStream);

        for (InputStream inputStream : pdfInputStreams) {
            PDDocument sourceDocument = null;
            try {
                sourceDocument = PDDocument.load(inputStream);
                List<PDPage> pages = sourceDocument.getPages();
                for (int i = 0; i < pages.getCount(); i++) {
                    PDPage page = pages.get(i);
                    PDDocument singlePageDocument = new PDDocument();
                    singlePageDocument.addPage(page);
                    merger.appendDocument(singlePageDocument, singlePageDocument);
                    singlePageDocument.close(); // Close the single-page document immediately
                }
            } finally {
                if (sourceDocument != null) {
                    sourceDocument.close(); // Close the source document after processing its pages
                }
            }
        }

        merger.close(); // This flushes and closes the destination stream internally
        return outputStream;
    }

    public static void main(String[] args) throws IOException {
        // Example Usage (replace with your actual InputStreams)
        try (InputStream inputStream1 = MemoryEfficientPdfMerger.class.getResourceAsStream("/pdf/sample1.pdf");
             InputStream inputStream2 = MemoryEfficientPdfMerger.class.getResourceAsStream("/pdf/sample2.pdf")) {

            if (inputStream1 == null || inputStream2 == null) {
                System.err.println("Error: Sample PDF files not found.");
                return;
            }

            List<InputStream> inputStreams = List.of(inputStream1, inputStream2);
            MemoryEfficientPdfMerger merger = new MemoryEfficientPdfMerger();
            ByteArrayOutputStream mergedPdfStream = merger.mergePdfPages(inputStreams);

            // Now you can work with the mergedPdfStream (e.g., save to a file, send as a response)
            System.out.println("PDFs merged successfully. Merged PDF size: " + mergedPdfStream.size() + " bytes.");

            // Important: Close the ByteArrayOutputStream when you're done with it
            mergedPdfStream.close();
        }
    }
}
