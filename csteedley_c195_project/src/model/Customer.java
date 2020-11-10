package model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
/**@author chris steedley */

/**Customer class for creating customer objects */
public class Customer {
    private int customerId;
    private String customerName;
    private String address;
    private String division;
    private String country;
    private String postalCode;
    private String phone;

    public static ObservableList<Customer> allOfCustomers = FXCollections.observableArrayList();


    /**constructor */
    public Customer() {
        this.customerId = customerId;
        this.customerName = customerName;
        this.address = address;
        this.division = division;
        this.country = country;
        this.postalCode = postalCode;
        this.phone = phone;
    }
/**Getters and setters for object customer information
 * @return customerId */
    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerID(int customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }


    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }


    public String getDivision() {
        return division;
    }

    public void setDivision(String division) {
        this.division = division;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String Country) {
        this.country = Country;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }



    }


