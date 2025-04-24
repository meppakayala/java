import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import org.apache.commons.text.StringEscapeUtils;

public class NestedXmlCharacterEscaper {

    public static String escapeSpecialCharactersInXmlData(String xmlLine) {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            InputSource is = new InputSource(new StringReader(xmlLine));
            Document doc = dBuilder.parse(is);
            doc.getDocumentElement().normalize();

            processElement(doc.getDocumentElement());

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult(writer);
            transformer.transform(source, result);
            return writer.toString();

        } catch (ParserConfigurationException | IOException | org.xml.sax.SAXException | TransformerException e) {
            System.err.println("Error processing XML line: " + e.getMessage());
            return xmlLine; // Return the original line in case of an error
        }
    }

    private static void processElement(Element element) {
        NodeList children = element.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            if (children.item(i).getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                Element childElement = (Element) children.item(i);
                if (childElement.getChildNodes().getLength() == 1 &&
                    childElement.getFirstChild().getNodeType() == org.w3c.dom.Node.TEXT_NODE) {
                    // This element contains only text data, so escape it
                    String unescapedData = childElement.getTextContent();
                    String escapedData = StringEscapeUtils.escapeXml11(unescapedData);
                    childElement.setTextContent(escapedData);
                } else {
                    // This element has child elements, so recurse
                    processElement(childElement);
                }
            }
        }
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java NestedXmlCharacterEscaper <inputFile> <outputFile>");
            return;
        }

        String inputFile = args[0];
        String outputFile = args[1];

        try (java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader(inputFile));
             java.io.BufferedWriter writer = new java.io.BufferedWriter(new java.io.FileWriter(outputFile))) {

            String line;
            while ((line = reader.readLine()) != null) {
                String processedLine = escapeSpecialCharactersInXmlData(line);
                writer.write(processedLine);
                writer.newLine();
            }

            System.out.println("Successfully processed '" + inputFile + "' and wrote to '" + outputFile + "'.");

        } catch (IOException e) {
            System.err.println("Error processing files: " + e.getMessage());
        }
    }
}
