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
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import model.Appointment;
import model.User;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import util.DBConnection;
import util.DBQuery;
import view_controller.loginScreenController;

import static java.time.LocalDateTime.now;

/**@author chris steedley */

/**class modify appointment. very similar to add appointment except selected appointment causes fields to be pre populated */
public class modifyAppointmentController implements Initializable {
    User user = loginScreenController.getOnlineUser();
    Appointment appointmentSelected;


    @FXML
    private TextField idField;

    @FXML
    private TextField titleField;

    @FXML
    private TextField endField;

    @FXML
    private TextField startField;

    @FXML
    private ComboBox<String> contactBox;

    @FXML
    private TextField cIdField;

    @FXML
    private TextField descriptionField;

    @FXML
    private TextField locationField;

    @FXML
    private TextField typeField;

    @FXML
    private TextField userField;

    /**constructor for appointments selected */
    public modifyAppointmentController(Appointment appointmentSelected) {
        this.appointmentSelected = appointmentSelected;
    }

    /**cancel button. return to main screen*/
    @FXML
    void cancel(MouseEvent event) {
        mainScreen(event);


    }

    /** initilaizes combo box with its list. gens an Id and places selected items to corresponding fields.*/
    public void initialize(URL url, ResourceBundle rb) {
        genApptId();
        comboBoxes();
        placeAppointmentSelected();

    }

    /** places appointments from selected field. queries database to get username from userId */
    private void placeAppointmentSelected() {
        try {
            String customersSearch = "select User_Name from WJ07zq4.users where User_ID = '" + appointmentSelected.getUserId() + "'";
            DBQuery.searchDB(customersSearch);
            ResultSet rs = DBQuery.getResult();
            rs.next();
            String uId = rs.getString("User_Name");




        this.idField.setText(Integer.toString(appointmentSelected.getAppointmentId()));
        this.titleField.setText(appointmentSelected.getTitle());
        this.locationField.setText(appointmentSelected.getLocation());
        this.descriptionField.setText(appointmentSelected.getDescription());
        this.typeField.setText(appointmentSelected.getType());
        this.userField.setText(uId);
        this.cIdField.setText(Integer.toString(appointmentSelected.getCustomerId()));
        this.startField.setText(appointmentSelected.getStart());
        this.endField.setText(appointmentSelected.getEnd());
        this.contactBox.setValue(appointmentSelected.getContact());
    }

     catch (Exception e){
            System.out.println(e.getMessage());
        }

    }
    /** Converts to utc time*/
    public String convertToUtcTimeZone(String Date) {
        String convertedDate = "";
        try {
            DateFormat currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            currentTime.setTimeZone(TimeZone.getTimeZone(getCurrentTimeZone()));

            currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            currentTime.setTimeZone(TimeZone.getTimeZone(getCurrentTimeZone()));

            java.util.Date date = currentTime.parse(Date);

            DateFormat utcFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));


            convertedDate = utcFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return convertedDate;
    }

    /**converts to current timezone */
    public String convertToCurrentTimeZone(String Date) {
        String convertedDate = "";
        try {

            DateFormat utcFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

            java.util.Date date = utcFormat.parse(Date);

            DateFormat currentTFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            currentTFormat.setTimeZone(TimeZone.getTimeZone(getCurrentTimeZone()));

            convertedDate = currentTFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return convertedDate;
    }
    /**converts to eastern timezone */
    public String convertToEasternTimeZone(String Date) {
        String convertedDate = "";
        try {
            DateFormat currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            currentTime.setTimeZone(TimeZone.getTimeZone(getCurrentTimeZone()));

            currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            currentTime.setTimeZone(TimeZone.getTimeZone(getCurrentTimeZone()));

            java.util.Date date = currentTime.parse(Date);

            DateFormat estFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            estFormat.setTimeZone(TimeZone.getTimeZone("America/New_York"));


            convertedDate = estFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return convertedDate;
    }

    /**gets current timezone */
    public String getCurrentTimeZone() {
        TimeZone tz = Calendar.getInstance().getTimeZone();
        return tz.getID();
    }
    /** gens an Id by adding 1 to last id number */
    public void genApptId() {
        try {
            int i = 1;
            String customersSearch = "select Appointment_ID from WJ07zq4.appointments order by Appointment_ID";
            DBQuery.searchDB(customersSearch);
            ResultSet rs = DBQuery.getResult();

            while (rs.next()) {
                int currentId = rs.getInt("Appointment_ID");
                i = currentId;
            }
            i++;
            idField.setText(Integer.toString(i));


        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }


    public ObservableList comboContact = FXCollections.observableArrayList();
    /** creates combo box. adds contacts to drop down list*/
    public void comboBoxes() {
        try {
            comboContact.clear();


            String customersSearch = "select Contact_Name from WJ07zq4.contacts";
            DBQuery.searchDB(customersSearch);
            ResultSet rs = DBQuery.getResult();

            while (rs.next()) {
                String currentContact = null;

                currentContact = rs.getString("Contact_Name");


                comboContact.add(currentContact);
            }


        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        contactBox.setItems(comboContact);
    }

    /** saves modded appointment. also performs same checks as in add appointment to ensure data correctly formatted */
    @FXML
    void saveAppt(MouseEvent event) throws SQLException {

        if (saveAppointmentCheck()) {


            String cId = idField.getText();
            String cn = titleField.getText();
            String a = descriptionField.getText();
            String pc = locationField.getText();
            String ph = typeField.getText();
            String s = convertToUtcTimeZone(startField.getText());
            String en = convertToUtcTimeZone(endField.getText());
            String cU = user.getUserName();
            String w = userField.getText();
            String q = cIdField.getText();
            String didi = contactBox.getSelectionModel().getSelectedItem().toString();
            String customersSearch = "update WJ07zq4.appointments set Appointment_ID ='" + cId + "', Title = '" + cn + "',  Description = '" + a + "'," +
                    " Location = '" + pc + "', Type = '" + ph + "', Start = '" + s + "', End = '" + en + "', Last_Update = '" + now() + "'," +
                    " Last_Updated_By = '" + cU + "', Customer_ID = '" + q + "', User_ID = (select User_ID from WJ07zq4.users where User_Name = '" + w + "')," +
                    " Contact_ID = (select Contact_ID from WJ07zq4.contacts where Contact_Name = '" + didi + "') where Appointment_ID = '"+appointmentSelected.getAppointmentId()+"'";


            try {
                Statement statement = DBConnection.startConnection().createStatement();
                statement.executeUpdate(customersSearch);
                mainScreen(event);


            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

        }

    }
    /**splits date time. compares and groups through regex. parses and compares with logical if statements for business rules*/
    public boolean checkTime() {
        String easternStartTime = convertToEasternTimeZone(startField.getText());
        String[] starParts = easternStartTime.split(" ");
        String easternEndTime = convertToEasternTimeZone(endField.getText());
        String[] endParts = easternEndTime.split(" ");
        Pattern p = Pattern.compile("([0-9]{2}):([0-9]{2}):([0-9]{2})");
        Matcher m = p.matcher(starParts[1]);
        if (m.matches()) {
            String hourString = m.group(1);
            String minuteString = m.group(2);
            int hour = Integer.parseInt(hourString);
            int minute = Integer.parseInt(minuteString);
            p = Pattern.compile("([0-9]{2}):([0-9]{2}):([0-9]{2})");
            Matcher m2 = p.matcher(endParts[1]);
            if (m2.matches()) {
                String  hourString2 = m2.group(1);
                String  minuteString2 = m2.group(2);
                int  hour2 = Integer.parseInt(hourString2);
                int minute2 = Integer.parseInt(minuteString2);


                if (hour >= 8 && hour < 22 && hour2 >= 8 && hour2 <= 22 ) {


                    if (hour2 == 22 && minute2 != 0) {
                        System.out.println("x");
                        return false;
                    }
                    return true;
                }
            }
        }

        return false;
    }



    /**splits date time. compares and groups through regex. parses and compares with logical if statements for business rules*/
    public boolean checkOverlap() throws SQLException {
        String utcStartTime = convertToUtcTimeZone(startField.getText());
        String[] starP = utcStartTime.split(" ");
        String utcEndTime = convertToUtcTimeZone(endField.getText());
        String[] endP = utcEndTime.split(" ");
        Pattern p = Pattern.compile("([0-9]{2}):([0-9]{2}):([0-9]{2})");
        Matcher m = p.matcher(starP[1]);
        Matcher m2 = p.matcher(endP[1]);
        if (m.matches()) {
            String hourString = m.group(1);
            String minuteString = m.group(2);
            int hour = Integer.parseInt(hourString);
            int minute = Integer.parseInt(minuteString);
            if (m2.matches()) {
                String hourString2 = m2.group(1);
                String minuteString2 = m2.group(2);
                int hour2 = Integer.parseInt(hourString2);
                int minute2 = Integer.parseInt(minuteString2);


                String customersSearch = "select Appointment_ID, Start, End from WJ07zq4.appointments";
                DBQuery.searchDB(customersSearch);
                ResultSet rs = DBQuery.getResult();

                while (rs.next()) {
                    int appID = rs.getInt("Appointment_ID");
                    String dbStart = rs.getString("Start");
                    String[] dbStartPart = dbStart.split(" ");
                    String dbEnd = rs.getString("End");
                    String[] dbEndPart = dbEnd.split(" ");

                    if (dbStartPart[0].equals(starP[0])) {


                        p = Pattern.compile("([0-9]{2}):([0-9]{2}):([0-9]{2})");
                        Matcher m3 = p.matcher(dbStartPart[1]);
                        Matcher m4 = p.matcher(dbEndPart[1]);
                        if (m3.matches() && m4.matches()) {
                            String hourString3 = m3.group(1);
                            String minuteString3 = m3.group(2);
                            int hour3 = Integer.parseInt(hourString3);
                            int minute3 = Integer.parseInt(minuteString3);
                            String hourString4 = m4.group(1);
                            String minuteString4 = m4.group(2);
                            int hour4 = Integer.parseInt(hourString4);
                            int minute4 = Integer.parseInt(minuteString4);
                            if (appID != appointmentSelected.getAppointmentId()) {

                                if (hour == hour3 && hour == hour4 && minute >= minute3 && minute <= minute4)
                                    return false;
                                if (hour == hour3 && hour != hour4 && minute >= minute3) return false;
                                if (hour != hour3 && hour == hour4 && minute <= minute4) return false;

                                if (hour > hour2) return false;
                                if (hour == hour2 && minute > minute2) return false;

                                if(hour < hour3 && hour2 > hour4) return false;
                                if(hour == hour3 && hour2 == hour4 && minute2 >minute3 && minute < minute3) return false;
                                if(hour == hour3 && minute <= minute3 &&  hour2 == hour4 && minute2 > minute4) return false;
                                if(hour < hour3  &&  hour2 == hour4 && minute2 > minute4) return false;


                                if (hour2 == hour3 && hour2 == hour4 && minute >= minute3 && minute <= minute4)
                                    return false;
                                if (hour2 == hour3 && hour2 != hour4 && minute >= minute3) return false;
                                if (hour2 != hour3 && hour2 == hour4 && minute <= minute4) return false;


                            }
                        }


                    }

                }
            }
        }
        return true;
    }
    /**performs checks above and returns verdict. if all pass inspection. appointment is saved*/
    public  boolean saveAppointmentCheck() throws SQLException {

        if (titleField.getText().isEmpty() || descriptionField.getText().isEmpty() || locationField.getText().isEmpty() || typeField.getText().isEmpty() ||
                contactBox.getSelectionModel().isEmpty() || startField.getText().isEmpty() || endField.getText().isEmpty() || userField.getText().isEmpty() ||
                cIdField.getText().isEmpty()){
            Alert alert = new Alert(Alert.AlertType.ERROR, "You have left a field blank, all fields must have data");
            alert.showAndWait();
            return false;
        }

        if(!startField.getText().matches("^([0-9]{4})-([0-1][0-9])-([0-3][0-9])\\s([0-1][0-9]|[2][0-3]):([0-5][0-9]):([0-5][0-9])$")){
            Alert alert = new Alert(Alert.AlertType.ERROR, "Must use provided format for date and time");
            alert.showAndWait();
            return false;
        }

        if(!endField.getText().matches("^([0-9]{4})-([0-1][0-9])-([0-3][0-9])\\s([0-1][0-9]|[2][0-3]):([0-5][0-9]):([0-5][0-9])$")){
            Alert alert = new Alert(Alert.AlertType.ERROR, "Must use provided format for date and time");
            alert.showAndWait();
            return false;
        }

        if(!checkTime()){
            Alert alert = new Alert(Alert.AlertType.ERROR, "You must schedule an appointment between 08:00 and 22:00 Eastern Time. Please adjust appointment Time");
            alert.showAndWait();
            return false;
        }

        if(!checkOverlap()){
            Alert alert = new Alert(Alert.AlertType.ERROR, "Appointment already scheduled in this time frame, please pick another");
            alert.showAndWait();
            return false;
        }

        return true;
    }

    /**returns to main screen*/
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