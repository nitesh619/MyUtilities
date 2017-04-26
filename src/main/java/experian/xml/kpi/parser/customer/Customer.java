package experian.xml.kpi.parser.customer;

import java.util.Date;
import java.util.Set;

/**
 * Created by nitesh.jain on 08-04-2017.
 */
public class Customer {
    private String firstName;
    private String lastName;
    private Date dob;
    private Integer age;
    private String gender;
    private String maritalStatus;
    private Integer inc;
    private String nationCode;
    private Address address;
    private String telephoneNo;
    private String mobileNo;
    private Set<Document> documents;
    private String pan;

    public Set<Document> getDocuments() {
        return documents;
    }

    public void setDocuments(Set<Document> documents) {
        this.documents = documents;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setMaritalStatus(String maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public Integer getInc() {
        return inc;
    }

    public void setInc(Integer inc) {
        this.inc = inc;
    }


    public void setNationCode(String nationCode) {
        this.nationCode = nationCode;
    }

    public Address getAddress() {
        return address;
    }

    public String getDocumentPan() {
        if (documents != null) {
            for (Document document : documents) {
                if (document.getDocType().equalsIgnoreCase("PAN CARD")) {
                    return document.getDocId();
                }
            }
        }
        return null;
    }

    public void setAddress(Address add) {
        address = add;
    }

    public String getTelephoneNo() {
        return telephoneNo;
    }

    public void setTelephoneNo(String telephoneNo) {
        this.telephoneNo = telephoneNo;
    }


    @Override
    public String toString() {
        return "Customer{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", dob=" + dob +
                ", age=" + age +
                ", gender='" + gender + '\'' +
                ", maritalStatus='" + maritalStatus + '\'' +
                ", inc=" + inc +
                ", nationCode='" + nationCode + '\'' +
                ", address=" + address +
                ", telephoneNo=" + telephoneNo +
                ", mobileNo=" + mobileNo +
                ", documents=" + documents +
                '}';
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public void setPan(String pan) {
        this.pan = pan;
    }

    public String getPan() {
        return pan;
    }
}
