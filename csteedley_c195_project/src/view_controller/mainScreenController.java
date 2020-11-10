package view_controller;

import javafx.beans.property.SimpleStringProperty;
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
import model.Appointment;
import model.Customer;
import model.User;
import util.DBConnection;
import util.DBQuery;
import java.time.LocalDate;

import static java.time.DayOfWeek.MONDAY;
import static java.time.DayOfWeek.SUNDAY;
import static java.time.temporal.TemporalAdjusters.nextOrSame;
import static java.time.temporal.TemporalAdjusters.previousOrSame;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**@author chris steedley*/

/**main screen controller. I went for implementing most features off the main screen, allowing a user to view both customers and appointments for ease of reference as well
 * as allowing editing features (update, add, delete) and different views of appointments (month, week, all) as well as added search by month/year.*/
public class mainScreenController implements Initializable {
   public ObservableList<Customer> allCustomers = FXCollections.observableArrayList();
    public ObservableList<Appointment> allAppointments = FXCollections.observableArrayList();
    private Object Customer;
    public static boolean check = false;
    User user = loginScreenController.getOnlineUser();



/** Populates customer table and appointment table. also does check to see if appt within 15 mins of start up. uses boolean to check if has already alerted you on login.
 * that way does not alert on every return to main screen*/
    public void initialize(URL url, ResourceBundle rb) {
        populateCustomerTable();
        populateAppointmentTable();
       if(!check){
        checkApptSoon();}

    }


    /** discussion of lambda this is the abstract method for my lambda */
public interface searchDb{

        public void pop (ResultSet rs) throws SQLException;
}

/** discussion of lambda.  my lambda does all the setting and getting from the result set. saves a lot of space as this is used 3 times to populate the different appoint views
 *  */
searchDb myLambda = (ResultSet rs)-> {  Appointment appointment = new Appointment();

    appointment.setAppointmentId(rs.getInt("Appointment_ID"));
    appointment.setTitle(rs.getString("Title"));
    appointment.setDescription(rs.getString("Description"));
    appointment.setLocation(rs.getString("Location"));
    appointment.setContact(rs.getString("Contact_Name"));
    appointment.setType(rs.getString("Type"));
    appointment.setStart(convertToCurrentTimeZone(rs.getString("Start")));
    appointment.setEnd(convertToCurrentTimeZone(rs.getString("End")));
    appointment.setCustomerId(rs.getInt("Customer_ID"));
    appointment.setUserId(rs.getInt("User_ID"));


    allAppointments.add(appointment);};


/** discussion of lambda
 * this lambda function replaces the alert error message. makes for typing errors messages must quicker and saves lines of code.
 * it is used throughout the whole mainscreenController page.
 * */
    public interface functions {

        public void lamBDA (String string);


    }
/** discussion of lambda
 * this is the actual lambda function below */
functions myFunction = (s)->{Alert alert = new Alert(Alert.AlertType.ERROR, s);
                alert.showAndWait();};

    @FXML
    private TableView<Customer> customersTable;

    @FXML
    private TableColumn<?, ?> Customer_ID;

    @FXML
    private TableColumn<?, ?> Customer_Name;

    @FXML
    private TableColumn<?, ?> Address;

    @FXML
    private TableColumn<?, ?> Division;

    @FXML
    private TableColumn<?, ?> Country;

    @FXML
    private TableColumn<?, ?> Postal_Code;

    @FXML
    private TableColumn<?, ?> Phone;

    @FXML
    private TableView<Appointment> appointmentTable;

    @FXML
    private TableColumn<?, ?> apptId;

    @FXML
    private TableColumn<?, ?> apptTitle;

    @FXML
    private TableColumn<?, ?> apptDescription;

    @FXML
    private TableColumn<?, ?> apptLocation;

    @FXML
    private TableColumn<?, ?> apptContact;

    @FXML
    private TableColumn<?, ?> apptType;

    @FXML
    private TableColumn<?, ?> apptStart;

    @FXML
    private TableColumn<?, ?> apptEnd;

    @FXML
    private TableColumn<?, ?> apptCustomerId;

    @FXML
    private TableColumn<?, ?> apptUserId;


    @FXML
    private ToggleGroup apptGroup;

    @FXML
    private TextField monthSearchField;

    @FXML
    private TextField YearSearchField;

    /**returns to login screen */
    @FXML
    void logOut(MouseEvent event) {
        goLoginScreen(event);
    }
    /**on clicking all radio button restores to original full view of appointments. */
    @FXML
    void allView(MouseEvent event) {
        populateAppointmentTable();

    }
    /**populates monthly view through radio button. if dates entered in text fields will search those specific month/year. default is current date */
    @FXML
    void searchMonthButton(MouseEvent event) {
        populateMonthlyAppointmentTable();
    }

    /**populates monthly view through search button. if dates entered searches those. default is current date */
    @FXML
    void monthlyView(MouseEvent event) {
    populateMonthlyAppointmentTable();
    }

    /**Populates weekly view of Appointments. view is from monday through sunday. of current week */
    @FXML
    void weeklyView(MouseEvent event) {
populateWeeklyAppointmentTable();
    }

    /**goes to the add appointment form screen */
    @FXML
    void addAppt(MouseEvent event) {
        addAppointmentScreen(event);
    }

    /**goes to the add customer form screen */
    @FXML
    void addCustomer(MouseEvent event) {
        addCustomerScreen(event);
    }


    /**performs checks. then deletes from table and db. uses delete appointment method below
     * uses lambda to reduce alert code */
    @FXML
    void deleteAppt(MouseEvent event) throws SQLException {
        Appointment apptToDelete = appointmentTable.getSelectionModel().getSelectedItem();


        if (apptToDelete == null) {
            myFunction.lamBDA("Nothing to select, or nothing selected");
        }

        else {
            boolean confirm = false;
            confirm = confirmationWindowAppt(apptToDelete.getAppointmentId());
            if (!confirm) {
                return;
            }
            deleteAppointment();
            populateAppointmentTable();
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Appointment '"+ apptToDelete.getAppointmentId() +"' deleted");
            alert.showAndWait();
        }
    }
    /**deletes appointment from database. used in deleteappt method. */
    public void deleteAppointment() throws SQLException {
        Appointment app = appointmentTable.getSelectionModel().getSelectedItem();
        int delId = app.getAppointmentId();
        Statement statement = DBConnection.startConnection().createStatement();
        statement.executeUpdate("delete from WJ07zq4.appointments where Appointment_ID = "+delId+"");
    }

    /**deletes customer. performs several checks. making sure customer has no appointments.
     * uses lambda to reduce alert code */
    @FXML
    void deleteCustomer(MouseEvent event) throws SQLException {
        Customer cusToDelete = customersTable.getSelectionModel().getSelectedItem();


            if (cusToDelete == null) {
                 myFunction.lamBDA("No Customer selected or no customers to select");

            }
            else if  (cusApptCheck()) {
                myFunction.lamBDA("customer has appointments, please delete those first");
            }

            else {
                boolean confirm = false;
                confirm = confirmationWindow(cusToDelete.getCustomerName());
                if (!confirm) {
                    return;
                }
                deleteCus();
                populateCustomerTable();
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Customer deleted");
                alert.showAndWait();
            }
        }

    /**does the deleting in method above. deletes from DB */
    public void deleteCus() throws SQLException {
        Customer cusToDelete2 = customersTable.getSelectionModel().getSelectedItem();
        int delId = cusToDelete2.getCustomerId();
        Statement statement = DBConnection.startConnection().createStatement();
        statement.executeUpdate("delete from WJ07zq4.customers where Customer_ID = "+delId+"");


    }

    /**checks to ensure customer Has no appointments already planned. */
    public boolean cusApptCheck() throws SQLException {
        Customer cusToDelete3 = customersTable.getSelectionModel().getSelectedItem();
        int delId1 = cusToDelete3.getCustomerId();
        String cusSearch = "select Customer_ID from WJ07zq4.appointments";
        DBQuery.searchDB(cusSearch);
        ResultSet rs = DBQuery.getResult();
        while (rs.next()) {
            int x = rs.getInt("Customer_ID");
            if (x == delId1) {
                return true;
            }

        }
        return false;
    }
    /**confirmation window. making sure you wish to delete appointment */
    public static boolean confirmationWindowAppt(int apptToId) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete appointment");
        alert.setHeaderText("Delete Appointment #: " + apptToId + "?");
        alert.setContentText("Ok to confirm");
        Optional<ButtonType> result = alert.showAndWait();
        return result.get() == ButtonType.OK;
    }
    /**confirmation window makes sure you want to delete customer */
    public static boolean confirmationWindow(String cusName) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Customer");
        alert.setHeaderText("Delete:  " + cusName + "?");
        alert.setContentText("Ok to confirm");
        Optional<ButtonType> result = alert.showAndWait();
        return result.get() == ButtonType.OK;
    }
    /**takes you to reports screen */
    @FXML
    void generateReport(MouseEvent event) {
        generateReportScreen(event);
    }

    /**takes you to modify parts screen */
    @FXML
    void modifyAppt(MouseEvent event) {
        modifyAppointmentScreen(event);
    }

    /** takes you to modify customer form screen*/
    @FXML
    void modifyCustomer(MouseEvent event) {
        modifyCustomerScreen(event);
    }

    /**next few are all methods just to take you to certain screens. all the same almost except different screen */
    public void goLoginScreen(Event event) {
        try {
            check = false;
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view_controller/loginScreen.fxml"));
            loginScreenController controller = new loginScreenController();

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
    /**takes to add customer screen */
    public void addCustomerScreen(Event event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view_controller/addCustomer.fxml"));
            addCustomerController controller = new addCustomerController();

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
    /**takes to modify customer screen
     * uses lambda to reduce alert code */
    public void modifyCustomerScreen(Event event) {
        try {
            Customer customerSelected = customersTable.getSelectionModel().getSelectedItem();

            if (customerSelected == null) {
                myFunction.lamBDA("Must select a customer to modify");
            } else {

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view_controller/modifyCustomer.fxml"));
                modifyCustomerController controller = new modifyCustomerController(customerSelected);

                loader.setController(controller);
                Parent root = loader.load();
                Scene scene = new Scene(root);
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(scene);
                stage.setResizable(false);
                stage.show();
            }
        }
        catch(IOException e){
            }
        }

    /**takes to add appointment screen */
    public void addAppointmentScreen(Event event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view_controller/addAppointment.fxml"));
            addAppointmentController controller = new addAppointmentController();

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

    /**takes to modify appointment screen
     * uses lambda to reduce alert code*/
    public void modifyAppointmentScreen(Event event) {
        try {
            Appointment appointmentSelected = appointmentTable.getSelectionModel().getSelectedItem();

            if (appointmentSelected == null) {
               myFunction.lamBDA("Must select an appointment to modify");
            } else {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view_controller/modifyAppointment.fxml"));
            modifyAppointmentController controller = new modifyAppointmentController(appointmentSelected);

            loader.setController(controller);
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();
        }
        }catch (IOException e) {
        }
    }

    /**takes to generate report screen */
    public void generateReportScreen(Event event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view_controller/reportScreen.fxml"));
            reportScreenController controller = new reportScreenController();

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

    /**queries db and gets all customers to place in table. */
    public void populateCustomerTable() {
        try {
            allCustomers.clear();
            String customersSearch = "select customers.Customer_ID, customers.Customer_Name, customers.Address, first_level_divisions.Division, countries.Country, customers.Postal_Code, customers.Phone from WJ07zq4.customers, WJ07zq4.first_level_divisions, WJ07zq4.countries where customers.Division_ID = first_level_divisions.Division_ID && first_level_divisions.Country_ID = countries.Country_ID;";
            DBQuery.searchDB(customersSearch);
            ResultSet rs = DBQuery.getResult();

            while (rs.next()) {
                 Customer customer = new Customer();

                 customer.setCustomerID(rs.getInt("Customer_ID"));
                 customer.setCustomerName(rs.getString("Customer_Name"));
                 customer.setAddress(rs.getString("Address"));
                 customer.setDivision(rs.getString("Division"));
                 customer.setCountry(rs.getString("Country"));
                 customer.setPostalCode(rs.getString("Postal_Code"));
                 customer.setPhone(rs.getString("Phone"));

                 allCustomers.add(customer);
            }


        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        Customer_ID.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        Customer_Name.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        Address.setCellValueFactory(new PropertyValueFactory<>("address"));
        Division.setCellValueFactory(new PropertyValueFactory<>("division"));
        Country.setCellValueFactory(new PropertyValueFactory<>("country"));
        Postal_Code.setCellValueFactory(new PropertyValueFactory<>("postalCode"));
        Phone.setCellValueFactory(new PropertyValueFactory<>("phone"));


        customersTable.setItems(allCustomers);
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

    /**converts current time to utctime. mainly used for storing in db or checking */
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


    /**method to check if appointment is within 15 minutes. this one was quite hard and very complex. I can think of some easier ways to do it now.
     * converts time to utc. splits it compares it to every time in db and uses logical if statements to check if it is within 15 minutes
     * */
    public void checkApptSoon() {
        String utcStartTime = LocalTime.now().toString();
        String[] starP = utcStartTime.split("\\.");
        String startD = LocalDate.now().toString();

        Pattern p = Pattern.compile("([0-9]{2}):([0-9]{2}):([0-9]{2})");
        Matcher m = p.matcher(starP[0]);
        if (m.matches()) {
            String hourString = m.group(1);
            String minuteString = m.group(2);
            int hour = Integer.parseInt(hourString);
            int minute = Integer.parseInt(minuteString);

            try {
                String customersSearch = "select Appointment_ID, Start from WJ07zq4.appointments";
                DBQuery.searchDB(customersSearch);
                ResultSet rs = DBQuery.getResult();

                while (rs.next()) {
                    int tempID = rs.getInt("Appointment_ID");
                    String dbStart = convertToCurrentTimeZone(rs.getString("Start"));
                    String[] dbStartPart = dbStart.split(" ");


                    p = Pattern.compile("([0-9]{2}):([0-9]{2}):([0-9]{2})");
                    Matcher m3 = p.matcher(dbStartPart[1]);
                    if (m3.matches()) {
                        String hourString3 = m3.group(1);
                        String minuteString3 = m3.group(2);
                        int hour3 = Integer.parseInt(hourString3);
                        int minute3 = Integer.parseInt(minuteString3);
                        if (dbStartPart[0].equals(startD)) {

                            if ((hour == hour3) && (minute3 - minute <= 15) && (minute3 - minute >= 0)) {

                                Alert alert = new Alert(Alert.AlertType.INFORMATION, "You have an appointment within the next 15 minutes, ID: " + tempID + " on " + dbStartPart[0] + " at time: " + dbStartPart[1]);
                            alert.showAndWait();
                            check = true;
                            return;

                        }
                        if ((hour == hour3 - 1) && (minute + 15 - 60 >= minute3)) {
                            Alert alert = new Alert(Alert.AlertType.INFORMATION, "You have an appointment within the next 15 minutes, ID: " + tempID + " on " + dbStartPart[0] + " at time: " + dbStartPart[1]);
                            alert.showAndWait();
                            check = true;
                            return;
                        }
                    }


                }


            }


            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        Alert alert = new Alert(Alert.AlertType.INFORMATION,"you do not have an appointment within 15 minutes");
        alert.showAndWait();

            check = true;
    }


    /**discussion of lambda
     *  note all three populate appt methods below employ my pop lambda function saving a lot of lines of code.
     *  and making it much more readable
     *  Populates appointment table with all appointments*/
    public void populateAppointmentTable() {
        try {
            allAppointments.clear();
            String appointmentsSearch = ("select appointments.Appointment_ID, appointments.Title, appointments.Description," +
                    " appointments.Location, contacts.Contact_Name, appointments.Type, appointments.Start, appointments.End," +
                    " appointments.Customer_ID, appointments.User_ID from WJ07zq4.appointments, WJ07zq4.contacts where appointments.Contact_ID = contacts.Contact_ID");

            DBQuery.searchDB(appointmentsSearch);
            ResultSet rs = DBQuery.getResult();

            while (rs.next()) {
     myLambda.pop(rs);
            }


        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        apptId.setCellValueFactory(new PropertyValueFactory<>("appointmentId"));
        apptTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        apptDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
       apptLocation.setCellValueFactory(new PropertyValueFactory<>("location"));
        apptContact.setCellValueFactory(new PropertyValueFactory<>("contact"));
        apptType.setCellValueFactory(new PropertyValueFactory<>("type"));
       apptStart.setCellValueFactory(new PropertyValueFactory<>("start"));
        apptEnd.setCellValueFactory(new PropertyValueFactory<>("end"));
        apptCustomerId.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        apptUserId.setCellValueFactory(new PropertyValueFactory<>("userId"));


        appointmentTable.setItems(allAppointments);
    }

    /**populates monthly view in the appointment table. uses similar logic to checkAppt method above.
     * gets time parses it and compares to all times in db using if statements to see if it fits criteria.
     * also operates the search button and search fields. default if left empty is current month view
     * uses lambda to reduce alert code
     * uses other lambda to further reduce code in setting/getting from result set
     * */
    public void populateMonthlyAppointmentTable() {
        String startD = LocalDate.now().toString();
        Pattern p = Pattern.compile("([0-9]{4})-([0-9]{2})-([0-9]{2})");
        Matcher m = p.matcher(startD);
        if (m.matches()) {

            String yearString = m.group(1);
            String monthString = m.group(2);
            String dayString = m.group(3);
            int year = Integer.parseInt(yearString);
            int month = Integer.parseInt(monthString);
            int day = Integer.parseInt(dayString);

            if (!monthSearchField.getText().isEmpty()) {
                if (monthSearchField.getText().matches("(1|2|3|4|5|6|7|8|9|10|11|12)")) {
                    month = Integer.parseInt(monthSearchField.getText());
                }
                    if (!monthSearchField.getText().matches("(1|2|3|4|5|6|7|8|9|10|11|12)")) {
                        myFunction.lamBDA("Month search Number is Invalid");
                        return;
                    }
            }
            if (!YearSearchField.getText().isEmpty()) {
                if (YearSearchField.getText().matches("^[0-9]{4}$")) {
                    year = Integer.parseInt(YearSearchField.getText());
                }
                if (!YearSearchField.getText().matches("^[0-9]{4}$")) {
                   myFunction.lamBDA("Year search number is invalid");
                    return;
                }
            }

                try {
                    allAppointments.clear();
                    String appointmentsSearch = "select appointments.Appointment_ID, appointments.Title, appointments.Description, appointments.Location, contacts.Contact_Name, appointments.Type, appointments.Start, appointments.End, appointments.Customer_ID, appointments.User_ID from WJ07zq4.appointments, WJ07zq4.contacts where appointments.Contact_ID = contacts.Contact_ID";
                    DBQuery.searchDB(appointmentsSearch);
                    ResultSet rs = DBQuery.getResult();

                    while (rs.next()) {
                        String StartP = rs.getString("Start");
                        String[] startA = StartP.split(" ");
                        Pattern p2 = Pattern.compile("([0-9]{4})-([0-9]{2})-([0-9]{2})");
                        Matcher m2 = p2.matcher(startA[0]);
                        if (m2.matches()) {
                            String yearString2 = m2.group(1);
                            String monthString2 = m2.group(2);
                            String dayString2 = m2.group(3);
                            int year2 = Integer.parseInt(yearString2);
                            int month2 = Integer.parseInt(monthString2);
                            int day2 = Integer.parseInt(dayString2);

                            if (year == year2 && month == month2) {


                               myLambda.pop(rs);
                            }
                        }

                    }
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }

            apptId.setCellValueFactory(new PropertyValueFactory<>("appointmentId"));
            apptTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
            apptDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
            apptLocation.setCellValueFactory(new PropertyValueFactory<>("location"));
            apptContact.setCellValueFactory(new PropertyValueFactory<>("contact"));
            apptType.setCellValueFactory(new PropertyValueFactory<>("type"));
            apptStart.setCellValueFactory(new PropertyValueFactory<>("start"));
            apptEnd.setCellValueFactory(new PropertyValueFactory<>("end"));
            apptCustomerId.setCellValueFactory(new PropertyValueFactory<>("customerId"));
            apptUserId.setCellValueFactory(new PropertyValueFactory<>("userId"));


            appointmentTable.setItems(allAppointments);
        }

    /** gets this weeks monday/sunday. then parses every appointment to see if it falls between or is equal to those dates.
     *uses other lambda to further reduce code in setting/getting from result set
     * */
    public void populateWeeklyAppointmentTable() {

        LocalDate today = LocalDate.now();

        LocalDate monday = today.with(previousOrSame(MONDAY));
        LocalDate sunday = today.with(nextOrSame(SUNDAY));

            try {
                allAppointments.clear();
                String appointmentsSearch = "select appointments.Appointment_ID, appointments.Title, appointments.Description, appointments.Location, contacts.Contact_Name, appointments.Type, appointments.Start, appointments.End, appointments.Customer_ID, appointments.User_ID from WJ07zq4.appointments, WJ07zq4.contacts where appointments.Contact_ID = contacts.Contact_ID";
                DBQuery.searchDB(appointmentsSearch);
                ResultSet rs = DBQuery.getResult();

                while (rs.next()) {
                    String StartP = rs.getString("Start");
                    String[] startA = StartP.split(" ");
                    Pattern p2 = Pattern.compile("([0-9]{4})-([0-9]{2})-([0-9]{2})");
                    Matcher m2 = p2.matcher(startA[0]);
                    if (m2.matches()) {
                        String yearString2 = m2.group(1);
                        String monthString2 = m2.group(2);
                        String dayString2 = m2.group(3);
                        int year2 = Integer.parseInt(yearString2);
                        int month2 = Integer.parseInt(monthString2);
                        int day2 = Integer.parseInt(dayString2);

                        LocalDate cDate = LocalDate.parse(startA[0]);

                        if (cDate.isAfter(monday) && cDate.isBefore(sunday) || cDate.isEqual(monday) || cDate.isEqual(sunday)) {

                            myLambda.pop(rs);
                        }
                    }

                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

        apptId.setCellValueFactory(new PropertyValueFactory<>("appointmentId"));
        apptTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        apptDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        apptLocation.setCellValueFactory(new PropertyValueFactory<>("location"));
        apptContact.setCellValueFactory(new PropertyValueFactory<>("contact"));
        apptType.setCellValueFactory(new PropertyValueFactory<>("type"));
        apptStart.setCellValueFactory(new PropertyValueFactory<>("start"));
        apptEnd.setCellValueFactory(new PropertyValueFactory<>("end"));
        apptCustomerId.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        apptUserId.setCellValueFactory(new PropertyValueFactory<>("userId"));


        appointmentTable.setItems(allAppointments);
    }

        }


