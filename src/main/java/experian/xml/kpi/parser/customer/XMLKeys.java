package experian.xml.kpi.parser.customer;

public class XMLKeys {

    public interface CustomKey {
        String PAN = "PAN";
        String FST_NME = "FST_NME";
        String LST_NME = "LST_NME";
        String DOB = "DOB";
        String AGE = "AGE";
        String GNDR = "GNDR";
        String MAR_STT = "MAR_STT";
        String INC = "INC";
        String NAT_CDE = "NAT_CDE";
        String MA_PMA = "MA_PMA";
        String MA_HT = "MA_HT";
        String MA_MT = "MA_MT";
        String MA_ID = "MA_ID";
    }

    public interface DocKey {
        String DOC_TYP = "DOC_TYP";
        String DOC_NO = "DOC_NO";
    }

    public interface SubKey {
        String IDENTIFIER = "IDENTIFIER";
        String PRODUCT = "PRODUCT";
        String CLASSIFICATION = "CLASSIFICATION";
        String DATE = "DATE";
        String APP_DTE = "APP_DTE";
        String TERM = "TERM";
        String APP_VAL = "APP_VAL";
        String MA = "MA";
    }

    public interface AddKey {
        String ADD = "ADD";
        String CTY = "CTY";
        String STE = "STE";
        String CTRY = "CTRY";
        String PIN = "PIN";
        String STREET="STREET";
    }
}
