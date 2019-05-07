package de.demmer.dennis.service;

import de.demmer.dennis.model.ner.NamedEntity;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;


public class NamedEntityXmlBuilderService {

    public String buildXML(List<NamedEntity> entityList, String text) {

        int id = 0;
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            // root elements
            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("text");
            doc.appendChild(rootElement);


            int start = 0;
            int end = 0;

            for (NamedEntity namedEntity : entityList) {

                // word element
                Element word = doc.createElement("name");
                word.setTextContent(namedEntity.getText());
                word.setAttribute("id", id + "");

                switch (namedEntity.getLabel()) {
                    case ("PER"):
                        word.setAttribute("type", "person");
                        break;
                    case ("LOC"):
                        word.setAttribute("type", "location");
                        break;
                    case ("ORG"):
                        word.setAttribute("type", "organisation");
                        break;
                    default:
                        word.setAttribute("type", namedEntity.getLabel());
                        break;
                }


                StringBuffer buffer = new StringBuffer();

                start = namedEntity.getStart();

                //Add content between words
                String betweenWords = text.substring(end, start);

                String changedEncoding = new String(betweenWords.getBytes(), StandardCharsets.UTF_8);
                Text txt = doc.createTextNode(changedEncoding);

                //append text between words
                rootElement.appendChild(txt);
                //append word
                rootElement.appendChild(word);
                end = namedEntity.getEnd();

                id++;
            }

            //append rest of text
            if (entityList.size()< 0 && entityList.get(entityList.size() - 1).getEnd() != text.length()) {

                start = entityList.get(entityList.size() - 1).getEnd();
                end = text.length();

                String betweenWords = text.substring(start, end);

                String changedEncoding = new String(betweenWords.getBytes(), StandardCharsets.UTF_8);
                Text txt = doc.createTextNode(changedEncoding);

                //append text between words
                rootElement.appendChild(txt);
            }

            // write the content into xml file
//            TransformerFactory transformerFactory = TransformerFactory.newInstance();
//
//            Transformer transformer = transformerFactory.newTransformer();
//            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
//            DOMSource source = new DOMSource(doc);
//            File xmlFile = new File("ner_out.xml");
//            StreamResult result = new StreamResult(xmlFile);
//            transformer.transform(source, result);
//

            DOMSource domSource = new DOMSource(doc);
            StringWriter writer = new StringWriter();
            StreamResult resultString = new StreamResult(writer);
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformerString = tf.newTransformer();
            transformerString.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformerString.transform(domSource, resultString);
            return writer.toString();


        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (TransformerException tfe) {
            tfe.printStackTrace();
        }

        return null;

    }


}
