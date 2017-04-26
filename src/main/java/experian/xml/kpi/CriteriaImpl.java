package experian.xml.kpi;


import java.util.List;
import java.util.regex.Pattern;

/**
 * Implementation of criteria for KPI.
 */
public class CriteriaImpl {
    private List<String> property;
    private String pattern;
    private boolean isNull;
    private boolean unique;
    private boolean dummy;

    private int regexCount;
    private int nullCount;
    private int duplicate;
    private int dummyCount;

    public CriteriaImpl(String pattern, boolean isNull) {
        this.pattern = pattern;
        this.isNull = isNull;
    }

    public CriteriaImpl(boolean isNull, boolean unique) {
        this(null, isNull, unique, false);
    }

    public CriteriaImpl(String pattern, boolean isNull, boolean unique, boolean dummy) {
        this(null, pattern, isNull, unique, dummy);
    }

    public CriteriaImpl(List<String> property, String pattern, boolean isNull, boolean unique, boolean dummy) {
        this.property = property;
        this.pattern = pattern;
        this.isNull = isNull;
        this.unique = unique;
        this.dummy = dummy;
    }

    @Override
    public String toString() {
        String builder = " { ";
        builder += pattern != null ? "Pattern= " + regexCount : "";
        builder += isNull ? "  Null= " + nullCount : "";
        builder += unique ? "  Duplicates= " + duplicate : "";
        builder += dummy ? "  Dummy= " + dummyCount : "";
        return builder + " }";
    }

    private boolean isPatternPass(String item) {
        if (pattern != null) {
            if (Pattern.matches(pattern, item)) {
                return true;
            }
            regexCount++;
            return false;
        }
        return true;
    }

    private boolean isDummyPass(String item) {
        if (dummy) {
            if (!item.equalsIgnoreCase("Dummy")) {
                return true;
            }
            dummyCount++;
            return false;
        }
        return true;
    }

    private boolean isNotNullPass(String item) {
        if (isNull) {
            if (item != null && !item.equals("")) {
                return true;
            }
            nullCount++;
            return false;
        }
        return true;
    }

    private boolean isUniquePass(String item) {
        if (unique && property != null && !property.isEmpty()) {
            int count = 0;
            for (String s : property) {
                if (s != null && s.equalsIgnoreCase(item)) {
                    count++;
                }
                if (count > 1) {
                    duplicate++;
                    return false;
                }
            }
        }
        return true;
    }

    public void clearCriteriaCounter() {
        regexCount = 0;
        nullCount = 0;
        duplicate = 0;
        dummyCount = 0;
    }

    public boolean checkCriteria(String item) {
        return isNotNullPass(item) && (isPatternPass(item) && isUniquePass(item) && isDummyPass(item));
    }
}
