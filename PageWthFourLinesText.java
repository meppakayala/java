import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.util.Dimension;

import java.io.IOException;

public class CenteredTextPage {

    public static void main(String[] args) throws IOException {
        // Create a new PDF document
        try (PDDocument document = new PDDocument()) {
            // Create a new page
            PDPage page = new PDPage();
            document.addPage(page);

            // Get the page dimensions
            float pageWidth = page.getMediaBox().getWidth();
            float pageHeight = page.getMediaBox().getHeight();

            // Define the text lines
            String line1 = "This is the BIG first line";
            String line2 = "This is the second line";
            String line3 = "This is the third line";
            String line4 = "This is the fourth line";

            // Define font sizes
            float fontSize1 = 24;
            float fontSizeOther = 12;

            // Define vertical spacing
            float topMargin = 100; // Adjust as needed
            float firstLineSpacing = 30; // Space after the first line
            float otherLineSpacing = 15; // Even spacing between other lines

            // Calculate text widths
            float lineWidth1 = PDType1Font.HELVETICA_BOLD.getStringWidth(line1) / 1000 * fontSize1;
            float lineWidth2 = PDType1Font.HELVETICA.getStringWidth(line2) / 1000 * fontSizeOther;
            float lineWidth3 = PDType1Font.HELVETICA.getStringWidth(line3) / 1000 * fontSizeOther;
            float lineWidth4 = PDType1Font.HELVETICA.getStringWidth(line4) / 1000 * fontSizeOther;

            // Calculate starting X coordinates for centering
            float startX1 = (pageWidth - lineWidth1) / 2;
            float startXOther = (pageWidth - lineWidthOther) / 2;

            // Calculate starting Y coordinates
            float currentY = pageHeight - topMargin;
            float y1 = currentY - fontSize1;
            currentY -= fontSize1 + firstLineSpacing;
            float y2 = currentY - fontSizeOther;
            currentY -= fontSizeOther + otherLineSpacing;
            float y3 = currentY - fontSizeOther;
            currentY -= fontSizeOther + otherLineSpacing;
            float y4 = currentY - fontSizeOther;

            // Create content stream
            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                // Add the first line
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, fontSize1);
                contentStream.beginText();
                contentStream.newLineAtOffset(startX1, y1);
                contentStream.showText(line1);
                contentStream.endText();

                // Add the second line
                contentStream.setFont(PDType1Font.HELVETICA, fontSizeOther);
                contentStream.beginText();
                contentStream.newLineAtOffset(startXOther, y2);
                contentStream.showText(line2);
                contentStream.endText();

                // Add the third line
                contentStream.setFont(PDType1Font.HELVETICA, fontSizeOther);
                contentStream.beginText();
                contentStream.newLineAtOffset(startXOther, y3);
                contentStream.showText(line3);
                contentStream.endText();

                // Add the fourth line
                contentStream.setFont(PDType1Font.HELVETICA, fontSizeOther);
                contentStream.beginText();
                contentStream.newLineAtOffset(startXOther, y4);
                contentStream.showText(line4);
                contentStream.endText();
            }

            // Save the document
            document.save("centered_text_page.pdf");
            System.out.println("PDF created successfully: centered_text_page.pdf");
        }
    }
}
