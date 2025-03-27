import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.util.List;

@RestController
public class PdfMergeController {

    @PostMapping(value = "/stream-merged-with-first", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<InputStreamResource> streamMergedWithFirst(@RequestBody List<String> pdfResourcePaths) {
        if (pdfResourcePaths == null || pdfResourcePaths.size() < 2) {
            return ResponseEntity.badRequest().body(null);
        }

        try {
            InputStream firstPdfStream = new ClassPathResource(pdfResourcePaths.get(0)).getInputStream();
            PDDocument firstPdfDocument = PDDocument.load(firstPdfStream);

            ByteArrayOutputStream mergedOutputStream = new ByteArrayOutputStream();
            PDFMergerUtility mergerUtility = new PDFMergerUtility();
            mergerUtility.setDestinationStream(mergedOutputStream);
            mergerUtility.setMemoryUsageSetting(MemoryUsageSetting.setupTempFileOnly());

            for (int i = 1; i < pdfResourcePaths.size(); i++) {
                InputStream additionalPdfStream = new ClassPathResource(pdfResourcePaths.get(i)).getInputStream();
                mergerUtility.addSource(additionalPdfStream);
            }

            mergerUtility.mergeDocuments(MemoryUsageSetting.setupTempFileOnly());

            byte[] mergedPdfBytes = mergedOutputStream.toByteArray();
            ByteArrayInputStream mergedInputStream = new ByteArrayInputStream(mergedPdfBytes);

            ByteArrayOutputStream firstPdfByteArrayOutputStream = new ByteArrayOutputStream();
            firstPdfDocument.save(firstPdfByteArrayOutputStream);
            firstPdfDocument.close();
            firstPdfStream.close();

            ByteArrayInputStream firstPdfByteArrayInputStream = new ByteArrayInputStream(firstPdfByteArrayOutputStream.toByteArray());

            SequenceInputStream sequenceInputStream = new SequenceInputStream(firstPdfByteArrayInputStream, mergedInputStream);

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=merged_with_first.pdf");

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(new InputStreamResource(sequenceInputStream));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
