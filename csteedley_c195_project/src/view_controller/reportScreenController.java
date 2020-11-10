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
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import util.DBQuery;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.ResourceBundle;
import java.util.TimeZone;

/**@author chris steedley */

/** report screen controller. controls report screen and implements 4 reports all results just print to one large text field */
public class reportScreenController implements Initializable {

    @FXML
    private ComboBox<String> selectReportCombo;

    @FXML
    private TextArea typesByMonthsText;


/** loads combo box with options and initializes */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        selectReportCombo.setItems(selectedReport);
    }

    /** go back to main screen */
    @FXML
    void back(MouseEvent event) {
        mainScreen(event);
    }
    /**event to go back to main screen */
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

    /**converts to current timezone. mainly used to convert utc to current time for table. */
    public String convertToCurrentTimeZone(String Date) {
        String convertedDate = "";
        try {

            DateFormat utcFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            java.util.Date date = utcFormat.parse(Date);

            DateFormat currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            currentTime.setTimeZone(TimeZone.getTimeZone(getCurrentTimeZone()));

            convertedDate =  currentTime.format(date);
        }catch (Exception e){ e.printStackTrace();}

        return convertedDate;
    }


    /**gets current timezone used in function above */
    public String getCurrentTimeZone(){
        TimeZone tz = Calendar.getInstance().getTimeZone();
        return tz.getID();
    }

    /**Determines your combo box option. and selects which report to generate */
    @FXML
    void genReport(MouseEvent event) {
        if(selectReportCombo.getValue().equals("appointment types by month")) {

            generateReport1();
        }

        if(selectReportCombo.getValue().equals("Schedule of each contact")) {

            generateReport2t();
        }

        if(selectReportCombo.getValue().equals("Total appointments this year")) {

            generateReport3();
        }
        if(selectReportCombo.getValue().equals("Appointments by Customer")) {

            generateReport4();
        }


    }
    /** list of items for combo box*/
    ObservableList<String> selectedReport = FXCollections.observableArrayList("appointment types by month", "Schedule of each contact", "Total appointments this year", "Appointments by Customer");

/**Lists report number by type and month (ex: type test has 4 reports in Nov). All 4 reports create basic header line and print that way.*/
    public void generateReport1() {

        try {
            typesByMonthsText.clear();
            String customersSearch = "select Type, MONTH(Start) as 'Month', COUNT(*) as 'Total' FROM WJ07zq4.appointments GROUP BY Type, MONTH(Start)";
            DBQuery.searchDB(customersSearch);
            ResultSet rs = DBQuery.getResult();

            StringBuilder parseReport = new StringBuilder();

            parseReport.append(String.format("%1$-45s %2$-45s %3$s \n", "Month", "Appointment Type", "Total"));
            parseReport.append("\n");

            while(rs.next()) {

                parseReport.append(String.format("%1$-56s %2$-60s %3$s \n", rs.getString("Month"), rs.getString("Type"), rs.getInt("Total")));
            }

            typesByMonthsText.setText(parseReport.toString());
        }

        catch(SQLException e) {
            System.out.println("Error " + e.getMessage());
        }
    }
    /**report 2 gives contact schedules of appointments. Note it can get a little unruly depending on length strings and such. in the future a table would be better */
    public void generateReport2t() {

        try {
            typesByMonthsText.clear();


            String customersSearch = "select contacts.Contact_Name, appointments.Appointment_ID, appointments.Customer_ID, appointments.Title," +
                    " appointments.Description, appointments.Type, appointments.Start, appointments.End from WJ07zq4.contacts, WJ07zq4.appointments where appointments.Contact_Id = contacts.Contact_ID order by Contact_Name";
            DBQuery.searchDB(customersSearch);
            ResultSet rs = DBQuery.getResult();

            StringBuilder parseReport = new StringBuilder();
            parseReport.append(String.format("%1$-20s %2$-20s %3$-20s %4$-20s %5$-20s %6$-30s %7$-30s %8$-20s  \n", "Contact_Name", "Appointment_ID", "Customer_ID", "Title", "Description", "Type",  "Start", "End"));
            parseReport.append("\n");

            while(rs.next()) {


                parseReport.append(String.format("%1$-30s %2$-30s %3$-25s %4$-25s %5$-20s %6$-30s %7$-30s %8$-30s \n",
                        rs.getString("Contact_Name"), rs.getInt("Appointment_ID"), rs.getInt("Customer_ID"), rs.getString("Title"),
                        rs.getString("Description"), rs.getString("Type"), convertToCurrentTimeZone(rs.getString("Start")), convertToCurrentTimeZone(rs.getString("End"))));


                parseReport.append("\n");
            }

            typesByMonthsText.setText(parseReport.toString());

        }
        catch(SQLException e) {
            System.out.println("Error " + e.getMessage());
        }
    }
    /** gives total appointments by year. great for seeing total business meetings over long periods */
    public void generateReport3() {

        try {
            typesByMonthsText.clear();
            String customersSearch = "select YEAR(Start) as 'Year', COUNT(*) as 'Total' FROM appointments GROUP BY YEAR(Start)";
            DBQuery.searchDB(customersSearch);
            ResultSet rs = DBQuery.getResult();


            StringBuilder parseReport = new StringBuilder();
            parseReport.append(String.format("%1$-25s %2$-25s \n", "Year", "Total"));
            parseReport.append("\n");

            while(rs.next()) {

                parseReport.append(String.format("%1$-25s %2$-25s \n", rs.getString("Year"), rs.getString("Total")));
            }

            typesByMonthsText.setText(parseReport.toString());
        }

        catch(SQLException e) {
            System.out.println("Error " + e.getMessage());
        }
    }

    /**report 4 gives contact schedules of appointments. wrote this as my original 3rd report but since it was just changing contact name and customer name in query  I thought I'd do
     * another to make sure it passed.
     *  Note it can get a little unruly depending on length strings and such. in the future a table would be better */
    public void generateReport4() {

        try {
            typesByMonthsText.clear();


            String customersSearch = "select customers.Customer_Name, appointments.Appointment_ID, appointments.Contact_ID, appointments.Title," +
                    " appointments.Description, appointments.Type, appointments.Start, appointments.End from WJ07zq4.customers, WJ07zq4.appointments where appointments.Customer_ID = customers.Customer_ID order by Customer_Name";
            DBQuery.searchDB(customersSearch);
            ResultSet rs = DBQuery.getResult();

            StringBuilder parseReport = new StringBuilder();
            parseReport.append(String.format("%1$-20s %2$-20s %3$-20s %4$-20s %5$-20s %6$-30s %7$-30s %8$-20s  \n", "Customer_Name", "Appointment_ID", "Contact_ID", "Title", "Description", "Type",  "Start", "End"));
            parseReport.append("\n");

            while(rs.next()) {



                parseReport.append(String.format("%1$-30s %2$-30s %3$-25s %4$-25s %5$-20s %6$-30s %7$-30s %8$-30s \n",
                        rs.getString("Customer_Name"), rs.getInt("Appointment_ID"), rs.getInt("Contact_ID"), rs.getString("Title"),
                        rs.getString("Description"), rs.getString("Type"), convertToCurrentTimeZone(rs.getString("Start")), convertToCurrentTimeZone(rs.getString("End"))));


                parseReport.append("\n");
            }

            typesByMonthsText.setText(parseReport.toString());

        }
        catch(SQLException e) {
            System.out.println("Error " + e.getMessage());
        }
    }





}
