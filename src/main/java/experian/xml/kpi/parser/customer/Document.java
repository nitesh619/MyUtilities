package experian.xml.kpi.parser.customer;

/**
 * Created by nitesh.jain on 08-04-2017.
 */
public class Document {
    private String docType;
    private String docId;

    public String getDocType() {
        return docType;
    }

    public void setDocType(String docType) {
        this.docType = docType;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Document)) return false;

        Document document = (Document) o;

        return getDocType().equals(document.getDocType()) && getDocId().equals(document.getDocId());
    }

    @Override
    public int hashCode() {
        int result = getDocType().hashCode();
        result = 31 * result + getDocId().hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Document{" +
                "docType='" + docType + '\'' +
                ", docId='" + docId + '\'' +
                '}';
    }
}
