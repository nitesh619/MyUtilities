package experian.xml.validation;


import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class XMLValidator {
    private static final String SCHEMA_LANGUAGE = "http://www.w3.org/2001/XMLSchema";
    private static Logger logger = Logger.getLogger(XMLValidator.class);
    private static Logger rootLogger = Logger.getRootLogger();
    private final File validXmlDir = new File("valid_xml");
    private final File invalidXMLDir = new File("invalid_xml");
    private Validator validator;
    private int validCount;

    public XMLValidator() {
        createDirectories();
    }

    public boolean processXMLValidation(File xmlDir, File schema) {
        logger.info("Validate files at: "+xmlDir.getPath());
        File[] listOfFiles = xmlDir.listFiles();
        int totalCount = listOfFiles.length;
        try {
            xsdFileToSchemaValidator(schema);
            for (File xmlFile : listOfFiles) {
                copyXMLFiles(xmlFile, validate(xmlFile));
            }
        } catch (SAXException | IOException e) {
            logger.fatal("Validation Abort!!");
            logger.error("Exception: "+ e.getMessage());
            return false;
        }
        //copy xml to different location based on validation
        logger.info("Validation Complete!!");
        logger.info("Total Xml Files:  " + totalCount);
        logger.info("Valid Xml Files:  " + validCount);
        logger.info("Invalid Xml Files:  " + (totalCount - validCount));
        return true;
    }


    private boolean validate(File xml) {
        try {
            validator.validate(new StreamSource(xml));
            rootLogger.info("Valid: " + xml.getName());
            validCount++;
        } catch (SAXException | IOException e) {
            logger.info("Invalid: " + xml.getName());
            logger.error(e.getMessage());
            return false;
        }
        return true;
    }

    private void xsdFileToSchemaValidator(File schemaXsd) throws SAXException {
        SchemaFactory schemaFactory = SchemaFactory.newInstance(SCHEMA_LANGUAGE);
        Schema schema = schemaFactory.newSchema(schemaXsd);
        validator = schema.newValidator();
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
}
