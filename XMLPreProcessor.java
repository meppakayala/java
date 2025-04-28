import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XmlPreprocessor {

    import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XmlPreprocessor {

    public static String makeXmlParsable(String inputXml) {
        if (inputXml == null) {
            return null;
        }

        String parsableXml = inputXml;

        // 1. Escape stray '<' and '>' within element content
        Pattern contentEscapePattern = Pattern.compile(">([^<>]*)<");
        Matcher contentMatcher = contentEscapePattern.matcher(parsableXml);
        StringBuffer sb = new StringBuffer();
        while (contentMatcher.find()) {
            String content = contentMatcher.group(1);
            String escapedContent = content.replace("<", "&lt;").replace(">", "&gt;");
            contentMatcher.appendReplacement(sb, ">" + escapedContent + "<");
        }
        contentMatcher.appendTail(sb);
        parsableXml = sb.toString();

        // 2. Handle cases where < or > might be at the very beginning
        // or end of the element content.
        Pattern boundaryEscapePattern = Pattern.compile("<([^<>]*?)(<|>)([^<>]*?)</");
        Matcher boundaryMatcher = boundaryEscapePattern.matcher(parsableXml);
        StringBuffer sb2 = new StringBuffer();
        while (boundaryMatcher.find()) {
            String before = boundaryMatcher.group(1);
            String invalidChar = boundaryMatcher.group(2);
            String after = boundaryMatcher.group(3);
            String escapedChar = invalidChar.equals("<") ? "&lt;" : "&gt;";
            boundaryMatcher.appendReplacement(sb2, "<" + before + escapedChar + after + "</");
        }
        boundaryMatcher.appendTail(sb2);
        parsableXml = sb2.toString();

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
