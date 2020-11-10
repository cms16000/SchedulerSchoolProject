package view_controller;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import model.User;
import view_controller.loginScreenController;
import util.DBConnection;
import util.DBQuery;

import java.io.IOException;
import java.net.URL;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ResourceBundle;

import static java.time.LocalDateTime.now;

/**author Chris steedley */

/** class add customer controller, employs methods to add customer and generate combo boxes and save to database*/
public class addCustomerController implements Initializable {

/** Initializes the two combo boxes (division, country) as well as a gen id method.
 * sets combo boxes to first valid choices  */
    public void initialize(URL url, ResourceBundle rb) {
        comboBoxes();
        comboBoxes2();
        genCustomerId();
        this.customerCountry.setValue("");
        this.customerDivision.setValue("");
    }
    User user = loginScreenController.getOnlineUser();
    @FXML
    private TextField customerId;

    @FXML
    private TextField customerName;

    @FXML
    private TextField customerPostalCode;

    @FXML
    private TextField customerAddress;

    @FXML
    private ComboBox<String> customerDivision;

    @FXML
    private ComboBox<String> customerCountry;

    @FXML
    private TextField customerPhone;

    /** returns user to main screen */
    @FXML
    void cancel(MouseEvent event) {
        mainScreen(event);
    }



/**saves the customer to the database. Uses dummy variables and check method (below) to check certain requirements before adding to db */
    @FXML
    void saveCustomer(MouseEvent event) {

        if (saveCustomerCheck()) {


            String cId = customerId.getText();
            String cn = customerName.getText();
            String a = customerAddress.getText();
            String pc = customerPostalCode.getText();
            String ph = customerPhone.getText();
            String cU = user.getUserName();
            String didi = customerDivision.getSelectionModel().getSelectedItem().toString();
            String customersSearch = "insert into WJ07zq4.customers (Customer_ID, Customer_Name, Address, Postal_Code, Phone, Create_Date, Created_By, Last_Updated_By, Division_ID)" +
                    " values ('"+cId+"','" + cn + "','" + a + "','" + pc + "','" + ph + "','" + now() + "', '"+cU+"', '"+cU+"', (select Division_ID from  WJ07zq4.first_level_divisions where Division = '" + didi + "'))";

            try {
                Statement statement = DBConnection.startConnection().createStatement();
                statement.executeUpdate(customersSearch);
                mainScreen(event);


            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

        }
    }

    /** just an error check method. returns false if something is amiss */
       public  boolean saveCustomerCheck(){
        if (customerName.getText().isEmpty() || customerAddress.getText().isEmpty() || customerPostalCode.getText().isEmpty() || customerPhone.getText().isEmpty() ||
        customerDivision.getSelectionModel().isEmpty() || customerCountry.getSelectionModel().isEmpty()){
            Alert alert = new Alert(Alert.AlertType.ERROR, "You have left a field blank, all fields must have data");
            alert.showAndWait();
            return false;
        }
        return true;
    }


    /** method to gen an Id. unsure if you wanted me to create this or let the autogen id on the Db do it, but I added it anyway.
 *  just gets last customer id in db and adds one  */
  public void genCustomerId() {
        try {
            int i = 1;
            String customersSearch = "select Customer_ID from WJ07zq4.customers order by Customer_ID";
            DBQuery.searchDB(customersSearch);
            ResultSet rs = DBQuery.getResult();

            while (rs.next()) {
                int currentId = rs.getInt("Customer_ID");
                i = currentId;
            }
            i++;
            customerId.setText(Integer.toString(i));


        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }


    public ObservableList comboDivision = FXCollections.observableArrayList();

    @FXML
    void refreshDiv(MouseEvent event) {
        if(!customerCountry.getSelectionModel().getSelectedItem().isEmpty()){
            comboBoxesRefresh();
        }
        if(customerCountry.getSelectionModel().getSelectedItem().isEmpty()){
            comboBoxes();
        }

    }



/** new method to filter combo box based on country*/
    public void comboBoxesRefresh() {
        try {
            comboDivision.clear();

            String country = customerCountry.getSelectionModel().getSelectedItem().toString();

            String customersSearch = "select first_level_divisions.Division from WJ07zq4.first_level_divisions where first_level_divisions.Country_ID = (select Country_ID from  WJ07zq4.countries where Country = '"+country+"')";
            DBQuery.searchDB(customersSearch);
            ResultSet rs = DBQuery.getResult();

            while (rs.next()) {
                String currentDivision = null;

                currentDivision = rs.getString("Division");




                comboDivision.add(currentDivision);
            }


        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        customerDivision.setItems(comboDivision);
    }



/** gens list of divisions for division combo box. queries db and adds all to list, then adds list to combo box(same below for countries) */
public void comboBoxes() {
        try {
            comboDivision.clear();


            String customersSearch = "select Division from WJ07zq4.first_level_divisions";
            DBQuery.searchDB(customersSearch);
            ResultSet rs = DBQuery.getResult();

            while (rs.next()) {
                String currentDivision = null;

                currentDivision = rs.getString("Division");




                comboDivision.add(currentDivision);
            }


        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        customerDivision.setItems(comboDivision);
    }

    public ObservableList comboCountry = FXCollections.observableArrayList();

    /**generates the list in the countries combo box, unsure if you wanted to filter countries to just uk, can, and us since divisions are only for those places,
     * but if you did you would just add "where Country_ID = 38 or 230 or 231" to end of  customer search string below. no direct instruction to do so, so I left
     * them all in.
     * */
    public void comboBoxes2() {
        try {

            comboCountry.clear();

            String customersSearch = "select countries.Country, countries.COUNTRY_ID from WJ07zq4.countries ";
            DBQuery.searchDB(customersSearch);
            ResultSet rs = DBQuery.getResult();

            while (rs.next()) {
                String currentCountry = null;

                currentCountry = rs.getString("Country");

                comboCountry.add(currentCountry);
            }


        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        customerCountry.setItems(comboCountry);



    }


/** event to return to the main screen.*/
    public void mainScreen(Event event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view_controller/mainScreen.fxml"));
            mainScreenController controller = new mainScreenController();

            loader.setController(controller);
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
        }
    }


    }