package experian.xml.merge;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;

public class XMLMerger {
    private static final String COUNT = "COUNT";
    private static final String MERGE_FILE_NAME = "merge.xml";
    private static final String SUBMISSION = "SUBMISSION";
    private static final String SUBMISSIONS = "SUBMISSIONS";
    private static Logger logger = Logger.getLogger(XMLMerger.class);
    private static Logger rootLogger = Logger.getRootLogger();
    private Document mergeXMLFile;
    private DocumentBuilder dBuilder;
    private int mergeXMLCount;
    private int totalXMLCount;

    public boolean mergeXMLFiles(File xmlDir) {
        logger.info("Merge files at: " + xmlDir.getPath());
        File[] xmlFiles = xmlDir.listFiles();
        try {
            // Take 1st XML file and start appending nodes to this from others
            dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            if (xmlFiles != null && xmlFiles.length > 0) {
                mergeXMLFile = dBuilder.parse(new FileInputStream(xmlFiles[0]));
            } else {
                logger.fatal("No Xml found");
                logger.fatal("Merge Abort!");
                return false;
            }
            totalXMLCount = xmlFiles.length;
            mergeXMLCount++;
            startMergeProcess(xmlFiles);
            writeToMergeXML(mergeXMLFile);
            rootLogger.info("Merged Successfully!!");
            return true;
        } catch (Exception e) {
            logger.fatal("Check Xml: " + xmlFiles[0].getName());
            logger.fatal("Exception: " + e.getMessage());
            logger.fatal("Merge Failure!");
            return false;
        }
    }

    private void startMergeProcess(File[] xmlFiles) {
        Node submissionsNode = mergeXMLFile.getElementsByTagName(SUBMISSIONS).item(0);
        Node countNode = mergeXMLFile.getElementsByTagName(COUNT).item(0);
        int submissionCount = mergeXMLFile.getElementsByTagName(SUBMISSION).getLength();

        for (int f = 1; f < xmlFiles.length; f++) {
            File xmlFile = xmlFiles[f];
            try {
                Document xmlDoc = dBuilder.parse(new FileInputStream(xmlFile));
                xmlDoc.getDocumentElement().normalize();
                NodeList submissions = xmlDoc.getElementsByTagName(SUBMISSION);

                for (int i = 0; i < submissions.getLength(); i++) {
                    Element submission = (Element) submissions.item(i);
                    // Imports a node from another document to this document, without altering
                    // or removing the source node from the original document
                    Node copiedNode = mergeXMLFile.importNode(submission, true);
                    // Adds the node to the end of the submissions of children of this node
                    submissionsNode.appendChild(copiedNode);
                    submissionCount++;
                }
                mergeXMLCount++;
                rootLogger.info("Merged: " + xmlFile.getName());
            } catch (Exception e) {
                logger.error("Skipped: " + xmlFile.getName());
                logger.error("Exception: " + e.getMessage());
            }
        }
        countNode.setTextContent("" + submissionCount);
    }

    private void writeToMergeXML(Document xml) throws Exception {
        Transformer tf = TransformerFactory.newInstance().newTransformer();

        tf.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        tf.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        tf.setOutputProperty(OutputKeys.INDENT, "yes");
        Writer out = new StringWriter();
        tf.transform(new DOMSource(xml), new StreamResult(out));
        FileWriter fw = new FileWriter(MERGE_FILE_NAME);
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write(out.toString());
        bw.close();
        fw.close();
        logger.info("Merge Process Complete!");
        logger.info("Total Xml Files: " + totalXMLCount);
        logger.info("Merged Files: " + mergeXMLCount);
        logger.info("Skipped Files: " + (totalXMLCount - mergeXMLCount));
    }
}