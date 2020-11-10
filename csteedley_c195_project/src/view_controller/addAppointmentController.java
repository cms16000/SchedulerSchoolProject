package view_controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.css.Match;
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

/**@author chris steedley*/

/** add Appointment class. performs the adding of appointments to db and table. */
public class addAppointmentController implements Initializable {
    User user = loginScreenController.getOnlineUser();


    @FXML
    private TextField idField;

    @FXML
    private TextField titleField;

    @FXML
    private TextField endField;

    @FXML
    private TextField startField;

    @FXML
    private ComboBox<?> contactBox;

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

    @FXML
    void cancel(MouseEvent event) {
        mainScreen(event);


    }

    /**Initialize starts the Id generator. as well as loads combo box options */
    public void initialize(URL url, ResourceBundle rb) {
        genApptId();
        comboBoxes();

    }

    /** method converts from local time to utc time for storing of data in db */
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

    /** Converts time to current timezone.  */
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
    /**Converts time to eastern from local. useful for checking business rules*/
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

    /**used in 3 methods above to get timezone id */
    public String getCurrentTimeZone() {
        TimeZone tz = Calendar.getInstance().getTimeZone();
        return tz.getID();
    }
    /**method for generating an id. lists all appointment ids and adds one to last. */
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
    /** creates combo boxes and fills the drop down list with contacts*/
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

    /** on mouse click saves appointment to DB. also does checks to ensure data and time in correct format. */
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
            String customersSearch = "insert into WJ07zq4.appointments (Appointment_ID, Title, Description, Location, Type, Start, End, Create_Date, Created_By, Last_Update, Last_Updated_By, Customer_ID, User_ID, Contact_ID)" +
                    " values ('" + cId + "', '" + cn + "', '" + a + "', '" + pc + "', '" + ph + "', '" + s + "', '" + en + "', '" + now() + "', '" + cU + "', '" + now() + "', '" + cU + "', '" + q + "', (select User_ID from WJ07zq4.users where User_Name = '" + w + "'), (select Contact_ID from WJ07zq4.contacts where Contact_Name = '" + didi + "'))";

            try {
                Statement statement = DBConnection.startConnection().createStatement();
                statement.executeUpdate(customersSearch);
                mainScreen(event);


            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

        }

    }
/**checks time for business rules. converts time to eastern, splits it by space and groups through a regex. then compares hours/minutes using logic for rules  */
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
                    System.out.println("Y");

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


    /**checks time for business rules. converts time to eastern, splits it by space and groups through a regex.
     *  then compares hours/minutes using logic for overlapping appointments
     *  This one took me quite a while..... and I'm sure there is an easier way but this is what I came Up with.
     *  */
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

                                if (hour == hour3 && hour == hour4 && minute >= minute3 && minute <= minute4)
                                    return false;
                                if (hour == hour3 && hour != hour4 && minute >= minute3) return false;
                                if (hour != hour3 && hour == hour4 && minute <= minute4) return false;

                                if (hour > hour2) return false;
                                if (hour == hour2 && minute > minute2) return false;

                            if(hour < hour3 && hour2 > hour4) return false;
                            if(hour == hour3 && hour2 == hour4 && minute2 > minute3 && minute < minute3) return false;
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
        return true;
    }


    /** runs all checks. decides if one fails inspection and then permits saving if all good.*/
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

/** returns to the main screen. used by cancel function */
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


