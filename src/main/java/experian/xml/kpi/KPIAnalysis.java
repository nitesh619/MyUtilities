package experian.xml.kpi;


import experian.xml.kpi.parser.XMLParser;
import experian.xml.kpi.parser.customer.Submission;
import lombok.extern.log4j.Log4j;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j
public class KPIAnalysis {
    private static Logger rootLogger = Logger.getRootLogger();
    private List<Submission> submissions;
    private Map<Criteria, CriteriaImpl> criteriaMap;
    private boolean xmlParseSuccess;

    public KPIAnalysis(File xmlDir) {
        log.info("KPI Analyze files at: " + xmlDir.getPath());
        criteriaMap = new HashMap<>();
        XMLParser xmlParser = new XMLParser();
        xmlParseSuccess = xmlParser.parseXMLCustomerData(xmlDir);
        submissions = xmlParser.getSubmissions();
        //initializeCriteria after parsing only
        initializeCriteria();
    }

    private void initializeCriteria() {
        criteriaMap.put(Criteria.PIN_CODE, new CriteriaImpl("\\d\\d\\d\\d\\d\\d", true, false, false));
        criteriaMap.put(Criteria.PAN_NUMBER, new CriteriaImpl(getAllPanNumber(), "[A-Z]{5}\\d{4}[A-Z]", true, true, false));
        criteriaMap.put(Criteria.ADDRESS, new CriteriaImpl(true, true));
        criteriaMap.put(Criteria.CITY, new CriteriaImpl("\\D*", true));
        criteriaMap.put(Criteria.STATE, new CriteriaImpl("\\D*", true));
        criteriaMap.put(Criteria.COUNTRY, new CriteriaImpl("\\D*", true));
        criteriaMap.put(Criteria.FIRST_NAME, new CriteriaImpl("\\D*", true, false, true));
        criteriaMap.put(Criteria.LAST_NAME, new CriteriaImpl("\\D*", true, false, true));
        criteriaMap.put(Criteria.TELEPHONE, new CriteriaImpl("\\D*", true));
        criteriaMap.keySet().forEach(criteria -> rootLogger.info(criteria.name() + " Criteria Added"));
    }

    public boolean analyseCriteria() {
        if (!xmlParseSuccess) {
            log.error("KPI Analysis failed!!");
            return false;
        }
        int fullAddressNullCount = 0;
        //reset criteria counter
        criteriaMap.values().forEach(CriteriaImpl::clearCriteriaCounter);
        rootLogger.info("Analysis in Progress...");
        for (Submission submission : submissions) {
            criteriaMap.get(Criteria.PAN_NUMBER).checkCriteria(submission.getCustomer().getDocumentPan());
            if (submission.getCustomer().getAddress() != null) {
                criteriaMap.get(Criteria.PIN_CODE).checkCriteria(submission.getCustomer().getAddress().getPinCode());
                criteriaMap.get(Criteria.CITY).checkCriteria(submission.getCustomer().getAddress().getCity());
                criteriaMap.get(Criteria.COUNTRY).checkCriteria(submission.getCustomer().getAddress().getCountry());
                criteriaMap.get(Criteria.STATE).checkCriteria(submission.getCustomer().getAddress().getState());
                criteriaMap.get(Criteria.ADDRESS).checkCriteria(submission.getCustomer().getAddress().getAddress());
            } else {
                fullAddressNullCount++;
            }
            criteriaMap.get(Criteria.TELEPHONE).checkCriteria(submission.getCustomer().getTelephoneNo());
            criteriaMap.get(Criteria.FIRST_NAME).checkCriteria(submission.getCustomer().getFirstName());
            criteriaMap.get(Criteria.LAST_NAME).checkCriteria(submission.getCustomer().getLastName());
        }

        criteriaMap.forEach((k, v) -> log.info(k.name() + ": " + v.toString()));

        log.info("Full Address (MA_PMA): " + "Null " + fullAddressNullCount);
        log.info("Total Submissions: " + submissions.size());
        log.info("KPI Analysis completed!!");
        return true;
    }

    private List<String> getAllPanNumber() {
        List<String> pan = new ArrayList<>();
        if (submissions != null) {
            for (Submission submission : submissions) {
                if (submission.getCustomer() != null) {
                    pan.add(submission.getCustomer().getDocumentPan());
                } else {
                    pan.add(null);
                }
            }
        }
        return pan;
    }
}
