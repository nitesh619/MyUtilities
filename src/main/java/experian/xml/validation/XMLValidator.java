package experian.xml.validation;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XMLValidator {
    private static final String SCHEMA_LANGUAGE = "http://www.w3.org/2001/XMLSchema";
    private static Logger logger = Logger.getLogger(XMLValidator.class);
    private static Logger rootLogger = Logger.getRootLogger();
    private final File validXmlDir = new File("valid_xml");
    private final File invalidXMLDir = new File("invalid_xml");
    private final XMLErrorHandlerLogger errorHandler;
    private final Pattern p = Pattern.compile("<IDENTIFIER>(.*?)</IDENTIFIER>");
    private Validator validator;
    private int validCount;
    private final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

    public XMLValidator() {
        createDirectories();
        dbf.setValidating(false);
        errorHandler = new XMLErrorHandlerLogger();
    }

    /* Delete valid/Invalid dir if exist */
    public static boolean deleteDirectory(File path) {
        if (path.exists()) {
            File[] files = path.listFiles();
            for (File xmlFile : files) {
                xmlFile.delete();
            }
        }
        return (path.delete());
    }

    private String prettyPrint(Document xml) throws Exception {
        Transformer tf = TransformerFactory.newInstance().newTransformer();
        tf.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        tf.setOutputProperty(OutputKeys.INDENT, "yes");
        StreamResult result = new StreamResult(new StringWriter());
        tf.transform(new DOMSource(xml), result);
        return result.getWriter().toString();
    }

    public boolean processXMLValidation(File xmlDir, File schema) throws Exception {
        logger.info("Validate files at: " + xmlDir.getPath());
        File[] listOfFiles = xmlDir.listFiles();
        int totalCount = listOfFiles.length;
        try {
            xsdFileToSchemaValidator(schema);
            for (File xmlFile : listOfFiles) {
                copyXMLFiles(xmlFile, validate(xmlFile));
            }
        } catch (SAXException | IOException e) {
            logger.fatal("Validation Abort!!");
            logger.error("Exception: " + e.getMessage());
            return false;
        }
        logger.info("Validation Complete!!");
        logger.info("Total Xml Files:  " + totalCount);
        logger.info("Valid Xml Files:  " + validCount);
        logger.info("Invalid Xml Files:  " + (totalCount - validCount));
        return true;
    }

    private boolean validate(File xml) throws Exception {
        try {
            String identifierNo = "";
            errorHandler.clearExceptions();

            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new FileInputStream(xml));
            String prettyString = prettyPrint(doc);
            validator.validate(new StreamSource(new StringReader(prettyString)));

            if (!errorHandler.getExceptions().isEmpty()) {
                //Create new appender to create seprate log file for each invalid xml
                PatternLayout layout = new PatternLayout();
                String logFileName = "invalid_" + xml.getName().replace(".xml", ".txt");
                FileAppender appender = new FileAppender(layout, "logs/validatedirectory/" + logFileName, false);
                logger.addAppender(appender);
                logger.info("Invalid File Name:" + xml.getName());

                List<Integer> lineNumberList = new ArrayList<>();
                for (SAXParseException sax : errorHandler.getExceptions()) {
                    //If line no. repeats then skip it
                    if (lineNumberList.contains(sax.getLineNumber())) {
                        continue;
                    }
                    lineNumberList.add(sax.getLineNumber());
                    identifierNo = getIdentifier(sax.getLineNumber(), prettyString);
                    logger.info("Identifiers of invalid file: " + identifierNo);
                    logger.info("Line Number of Error:" + sax.getLineNumber());
                    logger.error("Message: " + sax.getMessage());
                }
                logger.removeAppender(appender);
                return false;
            }
            rootLogger.info("Valid: " + xml.getName());
            validCount++;
        } catch (SAXException | IOException e) {
            logger.info("Invalid XML File(Syntax Error): " + xml.getName());
            logger.error("Message: " + e.getMessage());
            return false;
        }
        return true;
    }

    private void xsdFileToSchemaValidator(File schemaXsd) throws SAXException {
        SchemaFactory schemaFactory = SchemaFactory.newInstance(SCHEMA_LANGUAGE);
        Schema schema = schemaFactory.newSchema(schemaXsd);
        validator = schema.newValidator();
        validator.setErrorHandler(errorHandler);
    }

    private void copyXMLFiles(File xmlFile, boolean pass) throws IOException {
        File finalDir = pass ? validXmlDir : invalidXMLDir;
        Files.copy(xmlFile.toPath(), new File(finalDir, xmlFile.getName()).toPath());
    }

    /* Create directories to hold valid and invalid xml files*/
    private void createDirectories() {
        deleteDirectory(invalidXMLDir);
        deleteDirectory(validXmlDir);
        validXmlDir.mkdir();
        invalidXMLDir.mkdir();
    }

    private String getIdentifier(int linenumber, String xmlText) throws Exception {
        BufferedReader br = new BufferedReader(new StringReader(xmlText));
        String group = "";
        for (int i = 0; i < linenumber; i++) {
            String line = br.readLine();
            Matcher m = p.matcher(line.trim());
            if (m.matches()) {
                group = m.group(1);
            }
        }
        return group;
    }
}