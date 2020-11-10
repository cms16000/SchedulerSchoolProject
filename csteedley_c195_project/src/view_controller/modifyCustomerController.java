package view_controller;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import model.Customer;
import model.User;
import util.DBConnection;
import util.DBQuery;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

import static java.time.LocalDateTime.now;

/**@author chris steedley */

/**class modify customer controller. Note similar to add customer class so this one will be brief, main difference
 * is below where the selected customer info is brought into the form */
public class modifyCustomerController implements Initializable {
    Customer customerSelected;
    User user = loginScreenController.getOnlineUser();


/** allows use of selected customer */
    public modifyCustomerController(Customer customerSelected) {
        this.customerSelected = customerSelected;


    }
/**initializes combo boxes as well as selected customers information */
    public void initialize(URL url, ResourceBundle rb) {
        comboBoxes();
        comboBoxes2();
        placeCustomerSelected();
    }

    /**places customers information in selected boxes on form */
    private void  placeCustomerSelected(){



        this.customerId.setText(Integer.toString(customerSelected.getCustomerId()));
        this.customerName.setText(customerSelected.getCustomerName());
        this.customerAddress.setText(customerSelected.getAddress());
        this.customerDivision.setValue(customerSelected.getDivision());
        this.customerCountry.setValue(customerSelected.getCountry());
        this.customerPostalCode.setText(customerSelected.getPostalCode());
        this.customerPhone.setText(customerSelected.getPhone());


    }


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
/** returns to main screen */
    @FXML
    void cancel(MouseEvent event) {
        mainScreen(event);
    }

/**updates customer in database, same as saving new customer except now a update set command instead of insert into */
    @FXML
    void saveCustomer(MouseEvent event) {

        if (saveCustomerCheck()) {
            String cId = customerId.getText();
            String cn = customerName.getText();
            String a = customerAddress.getText();
            String pc = customerPostalCode.getText();
            String uN = user.getUserName();
            String ph = customerPhone.getText();
            String didi = customerDivision.getSelectionModel().getSelectedItem().toString();
            String customersSearch = "update WJ07zq4.customers set Customer_Name = '"+cn+"', Address = '"+a+"', Postal_Code = '"+pc+"', Phone = '"+ph+"', Last_Update = '"+now()+"', Last_Updated_By = '"+uN+"', Division_ID = (select Division_ID from  WJ07zq4.first_level_divisions where Division = '" + didi + "') where Customer_ID = '"+customerSelected.getCustomerId()+"'";

            try {
                Statement statement = DBConnection.startConnection().createStatement();
                statement.executeUpdate(customersSearch);
                mainScreen(event);


            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

        }
    }
/**checks to makes sure forms filled out correctly */
    public  boolean saveCustomerCheck(){
        if (customerName.getText().isEmpty() || customerAddress.getText().isEmpty() || customerPostalCode.getText().isEmpty() || customerPhone.getText().isEmpty() ||
                customerDivision.getSelectionModel().isEmpty() || customerCountry.getSelectionModel().isEmpty()){
            Alert alert = new Alert(Alert.AlertType.ERROR, "You have left a field blank, all fields must have data");
            alert.showAndWait();
            return false;
        }
        return true;
    }


    public ObservableList comboDivision = FXCollections.observableArrayList();
    @FXML
    void refreshDiv(MouseEvent event) {
        if(!customerCountry.getSelectionModel().getSelectedItem().toString().isBlank()){
            comboBoxesRefresh();
        }
        else comboBoxes();

    }



    /** new method*/
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


    /**creates combo box division. filters list by country brought in */
    public void comboBoxes() {
        try {
            comboDivision.clear();

            String customersSearch = "select Division from WJ07zq4.first_level_divisions where first_level_divisions.Country_ID = (select Country_Id from WJ07zq4.countries where Country = '" + customerSelected.getCountry() + "')";
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

    /**creates combo box country list. add countries to list */
    public void comboBoxes2() {
        try {

            comboCountry.clear();

            String customersSearch = "select countries.Country, countries.COUNTRY_ID from WJ07zq4.countries";
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
/** returns to main screen */
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
        
