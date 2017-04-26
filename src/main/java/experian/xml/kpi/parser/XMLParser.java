package experian.xml.kpi.parser;

import experian.xml.kpi.parser.customer.*;
import experian.xml.validation.XMLValidator;
import org.apache.log4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


public class XMLParser {
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-mm-dd");
    private static Logger logger = Logger.getLogger(XMLParser.class);
    private static Logger rootLogger = Logger.getRootLogger();
    private final File kpiSkipped = new File("kpi_skip");
    private DocumentBuilder dBuilder;
    private List<Submission> submissions;

    public XMLParser() {
        XMLValidator.deleteDirectory(kpiSkipped);
        if (kpiSkipped.mkdir()) {
            rootLogger.info(kpiSkipped.getName() + " is created");
        }
    }

    public boolean parseXMLCustomerData(File xmlDir) {
        File[] xmlFiles = xmlDir.listFiles();
        submissions = new ArrayList<>();
        try {
            dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            logger.fatal("Parsing Aborted!!");
            logger.error(e.getMessage());
            return false;
        }

        for (File file : xmlFiles) {
            try {
                org.w3c.dom.Document xmlDoc = dBuilder.parse(new FileInputStream(file));
                NodeList submissionNode = xmlDoc.getElementsByTagName("SUBMISSION");
                for (int i = 0; i < submissionNode.getLength(); i++) {
                    Submission submission = parseSubmission(submissionNode.item(i));
                    submissions.add(submission);
                    rootLogger.info("Parsing: " + submission);
                }
            } catch (IOException | SAXException e) {
                logger.error("Skipped: " + file.getName());
                logger.error(e.getMessage());
                copySkippedFile(file);
            }
        }
        logger.info("Parsing Completed!!");
        return true;
    }

    private void copySkippedFile(File file) {
        try {
            Files.copy(file.toPath(), new File(kpiSkipped, file.getName()).toPath());
        } catch (IOException e) {
            logger.error("Can't copy skipped file!!");
            logger.error("Exception: "+ e.getMessage());
        }
    }

    public List<Submission> getSubmissions() {
        return submissions;
    }

    private Submission parseSubmission(Node node) {
        Submission submission = new Submission();
        if (Node.ELEMENT_NODE == node.getNodeType()) {
            Element element = (Element) node;

            Node identifier = element.getElementsByTagName(XMLKeys.SubKey.IDENTIFIER).item(0);
            Node product = element.getElementsByTagName(XMLKeys.SubKey.PRODUCT).item(0);
            Node classification = element.getElementsByTagName(XMLKeys.SubKey.CLASSIFICATION).item(0);
            Node date = element.getElementsByTagName(XMLKeys.SubKey.DATE).item(0);
            Node app_date = element.getElementsByTagName(XMLKeys.SubKey.APP_DTE).item(0);
            Node term = element.getElementsByTagName(XMLKeys.SubKey.TERM).item(0);
            Node app_val = element.getElementsByTagName(XMLKeys.SubKey.APP_VAL).item(0);
            Node ma = element.getElementsByTagName(XMLKeys.SubKey.MA).item(0);

            if (identifier != null) {
                submission.setIdentifier(identifier.getTextContent());
            }
            if (product != null) {
                submission.setProduct(product.getTextContent());
            }
            if (classification != null) {
                submission.setClassification(classification.getTextContent());
            }
            if (date != null) {
                submission.setDate(parseDateFormat(date.getTextContent()));
            }
            if (app_date != null) {
                submission.setAppDate(parseDateFormat(app_date.getTextContent()));
            }
            if (term != null) {
                submission.setTerm(Integer.valueOf(term.getTextContent()));
            }
            if (app_val != null) {
                submission.setAppValue(Integer.valueOf(app_val.getTextContent()));
            }
            if (ma != null) {
                submission.setCustomer(parseCustomerDetails(ma));
            }
        }
        return submission;
    }

    private Customer parseCustomerDetails(Node ma) {
        if (ma.getNodeType() == Node.ELEMENT_NODE) {
            Customer customer = new Customer();
            Element element = (Element) ma;

            Node pan = element.getElementsByTagName(XMLKeys.CustomKey.PAN).item(0);
            Node fstNme = element.getElementsByTagName(XMLKeys.CustomKey.FST_NME).item(0);
            Node lstNme = element.getElementsByTagName(XMLKeys.CustomKey.LST_NME).item(0);
            Node dob = element.getElementsByTagName(XMLKeys.CustomKey.DOB).item(0);
            Node age = element.getElementsByTagName(XMLKeys.CustomKey.AGE).item(0);
            Node gndr = element.getElementsByTagName(XMLKeys.CustomKey.GNDR).item(0);
            Node marSit = element.getElementsByTagName(XMLKeys.CustomKey.MAR_STT).item(0);
            Node inc = element.getElementsByTagName(XMLKeys.CustomKey.INC).item(0);
            Node natCde = element.getElementsByTagName(XMLKeys.CustomKey.NAT_CDE).item(0);
            Node maPMA = element.getElementsByTagName(XMLKeys.CustomKey.MA_PMA).item(0);
            Node ma_ht = element.getElementsByTagName(XMLKeys.CustomKey.MA_HT).item(0);
            Node ma_mt = element.getElementsByTagName(XMLKeys.CustomKey.MA_MT).item(0);
            NodeList ma_id = element.getElementsByTagName(XMLKeys.CustomKey.MA_ID);

            if (pan != null) {
                customer.setPan(pan.getTextContent());
            }
            if (fstNme != null) {
                customer.setFirstName(fstNme.getTextContent());
            }
            if (lstNme != null) {
                customer.setLastName(lstNme.getTextContent());
            }
            if (dob != null) {
                customer.setDob(parseDateFormat(dob.getTextContent()));
            }
            if (age != null) {
                customer.setAge(Integer.valueOf(age.getTextContent()));
            }
            if (gndr != null) {
                customer.setGender(gndr.getTextContent());
            }
            if (marSit != null) {
                customer.setMaritalStatus(marSit.getTextContent());
            }
            if (inc != null) {
                customer.setInc(Integer.valueOf(inc.getTextContent()));
            }
            if (natCde != null) {
                customer.setNationCode(natCde.getTextContent());
            }
            if (ma_ht != null) {
                customer.setTelephoneNo(String.valueOf(parseTelephoneNo(ma_ht)));
            }
            if (ma_mt != null) {
                customer.setMobileNo(parseTelephoneNo(ma_mt));
            }
            if (maPMA != null) {
                customer.setAddress(parseAddress(maPMA));
            }
            if (ma_id != null) {
                customer.setDocuments(parseDocuments(ma_id));
            }
            return customer;
        }
        return null;
    }

    private Set<Document> parseDocuments(NodeList idNode) {
        Set<Document> documentSet = new HashSet<>();
        for (int i = 0; i < idNode.getLength(); i++) {
            Node node = idNode.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Document document = new Document();
                Element element = (Element) node;

                Node docTyp = element.getElementsByTagName(XMLKeys.DocKey.DOC_TYP).item(0);
                Node docId = element.getElementsByTagName(XMLKeys.DocKey.DOC_NO).item(0);

                if (docTyp != null) {
                    document.setDocType(docTyp.getTextContent());
                }
                if (docId != null) {
                    document.setDocId(docId.getTextContent());
                }
                documentSet.add(document);
            }
        }
        return documentSet;
    }

    private Address parseAddress(Node addressNode) {
        if (addressNode.getNodeType() == Node.ELEMENT_NODE) {
            Address address = new Address();
            Element element = (Element) addressNode;

            Node add = element.getElementsByTagName(XMLKeys.AddKey.ADD).item(0);
            Node city = element.getElementsByTagName(XMLKeys.AddKey.CTY).item(0);
            Node state = element.getElementsByTagName(XMLKeys.AddKey.STE).item(0);
            Node country = element.getElementsByTagName(XMLKeys.AddKey.CTRY).item(0);
            Node pin = element.getElementsByTagName(XMLKeys.AddKey.PIN).item(0);

            if (add != null) {
                address.setAddress(add.getTextContent());
            }
            if (city != null) {
                address.setCity(city.getTextContent());
            }
            if (state != null) {
                address.setState(state.getTextContent());
            }
            if (country != null) {
                address.setCountry(country.getTextContent());
            }
            if (pin != null) {
                address.setPinCode(pin.getTextContent());
            }
            return address;
        }
        return null;
    }

    private String parseTelephoneNo(Node telNode) {
        if (telNode.getNodeType() == Node.ELEMENT_NODE) {
            Element element = (Element) telNode;
            Node telNo = element.getElementsByTagName("TEL_NO").item(0);
            if (telNo != null) {
                return telNo.getTextContent();
            }
        }
        return null;
    }

    private Date parseDateFormat(String date) {
        try {
            return SIMPLE_DATE_FORMAT.parse(date);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
}
