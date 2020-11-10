package view_controller;

import model.User;
import util.DBQuery;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import util.LogTracker;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.time.ZoneId;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.TimeZone;

/**@author Chris Steedley*/

/**controller for login screen. Deals with setting locale language and login attempts as well as translating messages*/
public class loginScreenController implements Initializable {

/** initialize languageSet. just used for switching between eng and fr*/
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        languageSet();
    }
/**just buttons and labels */
    @FXML
    private Label loginLabel;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField usernameField;

    @FXML
    private Label usernamelabel;

    @FXML
    private Label passwordlabel;

    @FXML
    private Label enterPasswordLabel;

    @FXML
    private Label welcomeLabel;

    @FXML
    private Label locationLabel;

    @FXML
    private Label countryLabel;

    @FXML
    private Button loginButton;

    Locale currentLocale = Locale.getDefault();
    /**creates user, and  on correct loggin and adds data for online user. used throughout project for checking user name, appointments etc  */
   public static User onlineUser = new User(0, "", "");

    public static User getOnlineUser() {
        return onlineUser;
    }

    public static void setOnlineUser(User onlineUser) {
        loginScreenController.onlineUser = onlineUser;
    }

    public static int ID;

/**checks locale language and changes login screen to fr if needed.*/
    public void languageSet() {
        String userLanguage = currentLocale.getLanguage();
        ZoneId z = TimeZone. getDefault().toZoneId();
        System.out.println(userLanguage);
        countryLabel.setText(z.toString());
        if (userLanguage.equals("fr")) {
            loginLabel.setText("écran de connexion");
            welcomeLabel.setText("Bienvenue, vous avez des besoins de planification? Pas de problème, vous avez couvert.");
            usernamelabel.setText("Nom d'utilisateur");
            passwordlabel.setText("mot de passe");
            enterPasswordLabel.setText("Veuillez s'il vous plaît entrer votre nom d'utilisateur et votre mot de passe");
            locationLabel.setText("l' emplacement");
            loginButton.setText("s'identifier");
        }
    }
/**runs boolean method on query results vs text field. if they match a users returns true */
    public static Boolean loggingIn(String username, String password) {
        try {
            String login = "select * from users";
            DBQuery.searchDB(login);
            ResultSet rs = DBQuery.getResult();

            while (rs.next()) {

                if (rs.getString("User_Name").equals(username) && rs.getString("Password").equals(password)){
                ID = rs.getInt("User_ID");
                onlineUser.setUserId(rs.getInt("User_ID"));
                onlineUser.setUserName(rs.getString("User_Name"));
                onlineUser.setUserPassword(rs.getString("Password"));



                    return true;}

            }
            return false;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }



/**checks if isUser is true. logs in if is user. prints to log on all attempts to detect suspicious activity. */
    @FXML
    private void login(MouseEvent event) throws IOException {
        String username = usernameField.getText();
        String password = passwordField.getText();

        boolean isUser = loggingIn(username, password);

        if (isUser){
            mainScreen(event);
            LogTracker.trackLog(username, true);


        }
        else {
            if(currentLocale.getLanguage().equals("fr")){
                Alert alert = new Alert(Alert.AlertType.ERROR, "le nom d'utilisateur ou le mot de passe était incorrect!");
                alert.showAndWait();

                LogTracker.trackLog(username, false);
            }
            else{
                Alert alert = new Alert(Alert.AlertType.ERROR, "The username or password was incorrect!");
                alert.showAndWait();

                LogTracker.trackLog(username, false);
            }


        }
    }
/**just for exiting whole program*/
    @FXML
    void exitProgram(MouseEvent event) {
    System.exit(0);
    }

/** loads main screen. just loads main screen gui. called by login method if is user is true */
        public void mainScreen (Event event){
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
            } catch (IOException e){}
        }

    }



