import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XmlPreprocessor {

    public static String makeXmlParsable(String inputXml) {
        if (inputXml == null) {
            return null;
        }

        // Basic replacements to handle common issues making XML unparsable
        String parsableXml = inputXml;

        // 1. Escape stray '<' and '>' outside of tags
        // This regex looks for '<' or '>' that are NOT followed/preceded by
        // a word character (letter, number, underscore) or a forward slash.
        // This is a heuristic and might need refinement based on your specific data.
        parsableXml = parsableXml.replaceAll("(?<![\\w/])<(?![\\w/])", "&lt;");
        parsableXml = parsableXml.replaceAll("(?<![\\w/])>(?![\\w/])", "&gt;");

        // 2. Escape stray '&' that are not part of an entity
        parsableXml = parsableXml.replaceAll("&(?!([a-zA-z0-9]+;|#\\d+;|#x[0-9a-fA-F]+;))", "&amp;");

        // 3. Attempt to fix simple unclosed tags (very basic heuristic)
        // This looks for a start tag without a corresponding closing tag
        // on the same "level" (very simplified and might have false positives).
        // A proper solution would involve parsing and understanding the XML structure.
        Pattern unclosedTagPattern = Pattern.compile("<([a-zA-Z0-9:]+)([^>]*)>(?!.*</\\1>)");
        Matcher matcher = unclosedTagPattern.matcher(parsableXml);
        parsableXml = matcher.replaceAll("<$1$2></$1>"); // Add a corresponding closing tag

        // Note: More complex scenarios (e.g., deeply nested issues, truly broken structure)
        // would require a more sophisticated approach, potentially involving a tolerant
        // XML parser or manual inspection and correction.

        return parsableXml;
    }

    public static void main(String[] args) {
        String veryInvalidXml = "<root>Some <data with < embedded > and & chars & also unclosed <tag></root>";
        System.out.println("Original Invalid XML:\n" + veryInvalidXml);

        String preprocessedXml = makeXmlParsable(veryInvalidXml);
        System.out.println("\nPreprocessed XML (Attempting to be parsable):\n" + preprocessedXml);

        // You would then attempt to parse the preprocessedXml
        // using a standard XML parser.
        // try {
        //     DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        //     DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        //     InputSource is = new InputSource(new StringReader(preprocessedXml));
        //     Document doc = dBuilder.parse(is);
        //     System.out.println("\nSuccessfully Parsed Preprocessed XML.");
        // } catch (Exception e) {
        //     System.err.println("\nError parsing preprocessed XML: " + e.getMessage());
        // }
    }
}
