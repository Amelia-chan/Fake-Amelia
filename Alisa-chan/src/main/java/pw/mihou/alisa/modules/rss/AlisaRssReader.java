package pw.mihou.alisa.modules.rss;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import pw.mihou.alisa.modules.exceptions.handler.AlisaExceptionHandler;
import pw.mihou.alisa.modules.rss.properties.AlisaRssProperty;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class AlisaRssReader {

    private static final DocumentBuilderFactory FACTORY = DocumentBuilderFactory.newDefaultInstance();

    /**
     * Reads the RSS feed by using the default Java URI reader.
     *
     * @param uri   The URI field to connect and parse.
     * @return      The {@link AlisaRssProperty} containing all the required data.
     */
    public static AlisaRssProperty fromUri(String uri) {
        try {
            return parse(FACTORY.newDocumentBuilder().parse(uri));
        } catch (SAXException | IOException | ParserConfigurationException exception) {
            AlisaExceptionHandler.accept(exception);
            return null;
        }
    }

    /**
     * Reads the RSS feed from the content provided.
     *
     * @param content   The content provided.
     * @return          The {@link AlisaRssProperty} containing all the required data.
     */
    public static AlisaRssProperty fromContent(String content) {
        try {
            return parse(FACTORY.newDocumentBuilder().parse(new InputSource(new StringReader(content))));
        } catch (SAXException | IOException | ParserConfigurationException exception) {
            AlisaExceptionHandler.accept(exception);
            return null;
        }
    }

    /**
     * Parses the XML data received from the Document into Alisa's specification
     * of RSS data types.
     *
     * @param document  The document to parse.
     * @return          The {@link AlisaRssProperty} containing all the data.
     */
    private static AlisaRssProperty parse(Document document) {
        try {
            Node channel = document.getElementsByTagName("channel").item(0);
            AtomicInteger number = new AtomicInteger(-1);
            List<AlisaRssProperty> properties = new ArrayList<>();
            while (channel.getChildNodes().item(number.get() + 1) != null) {
                Node childNode = channel.getChildNodes().item(number.incrementAndGet());

                if (childNode.getNodeName().equalsIgnoreCase("item")) {
                    Node itemChild = childNode.getFirstChild();

                    AlisaRssProperty property = ofNodeList(itemChild);
                    properties.add(property);
                }
            }

            return new AlisaRssProperty("channel", null, properties);
        } catch (Exception exception) {
            AlisaExceptionHandler.accept(exception);
            return null;
        }
    }

    /**
     * Handles a single element node nesting.
     *
     * @param itemNode  The item node to handle.
     * @return          The {@link AlisaRssProperty} version of the node.
     */
    private static AlisaRssProperty ofNode(Node itemNode) {
        if (itemNode.getNodeType() == Node.ELEMENT_NODE) {
            if (itemNode.getChildNodes().getLength() == 1) {
                return new AlisaRssProperty(
                        itemNode.getNodeName(),
                        itemNode.getTextContent().trim(),
                        Collections.emptyList()
                );
            } else {
                return ofNodeList(itemNode);
            }
        } else {
            return null;
        }
    }

    /**
     * Handles multiple level nesting of nodes.
     *
     * @param node  The node to handle.
     * @return      The {@link AlisaRssProperty} for the node.
     */
    private static AlisaRssProperty ofNodeList(Node node) {
        AtomicReference<Node> itemNode = new AtomicReference<>(node);
        List<AlisaRssProperty> itemProperties = new ArrayList<>();

        while (itemNode.get().getNextSibling() != null) {
            itemNode.set(itemNode.get().getNextSibling());

            AlisaRssProperty property = ofNode(itemNode.get());
            if (property != null) {
                itemProperties.add(ofNode(itemNode.get()));
            }
        }

        return new AlisaRssProperty(node.getParentNode().getNodeName(), null, itemProperties);
    }

}
