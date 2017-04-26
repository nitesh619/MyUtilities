package experian.xml.kpi.parser.customer;

import java.util.Date;

/**
 * Created by nitesh.jain on 09-04-2017.
 */
public class Submission {
    private String identifier;
    private String product;
    private String classification;
    private Date date;
    private Date appDate;
    private Integer term;
    private Integer appValue;
    private Customer customer;

    @Override
    public String toString() {
        return "Submission{" +
                "identifier='" + identifier + '\'' +
                ", product='" + product + '\'' +
                ", classification='" + classification + '\'' +
                ", date=" + date +
                ", appDate=" + appDate +
                ", term=" + term +
                ", appValue=" + appValue +
                ", customer=" + customer +
                '}';
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getClassification() {
        return classification;
    }

    public void setClassification(String classification) {
        this.classification = classification;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setAppDate(Date appDate) {
        this.appDate = appDate;
    }

    public Integer getTerm() {
        return term;
    }

    public void setTerm(Integer term) {
        this.term = term;
    }

    public void setAppValue(Integer appValue) {
        this.appValue = appValue;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer cust) {
        customer = cust;
    }
}
