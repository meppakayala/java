import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import org.apache.commons.text.StringEscapeUtils;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XmlCharacterEscaper {

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java XmlCharacterEscaper <inputFile> <outputFile>");
            return;
        }

        String inputFile = args[0];
        String outputFile = args[1];

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {

            String line;
            Pattern elementPattern = Pattern.compile("<([^>]+)>([^<]+)</\\1>");

            while ((line = reader.readLine()) != null) {
                Matcher matcher = elementPattern.matcher(line);
                StringBuffer sb = new StringBuffer();

                while (matcher.find()) {
                    String elementName = matcher.group(1);
                    String elementData = matcher.group(2);
                    String escapedData = StringEscapeUtils.escapeXml11(elementData);
                    matcher.appendReplacement(sb, "<" + elementName + ">" + escapedData + "</" + elementName + ">");
                }
                matcher.appendTail(sb);
                writer.write(sb.toString());
                writer.newLine();
            }

            System.out.println("Successfully processed '" + inputFile + "' and wrote to '" + outputFile + "'.");

        } catch (IOException e) {
            System.err.println("Error processing files: " + e.getMessage());
        }
    }
}
