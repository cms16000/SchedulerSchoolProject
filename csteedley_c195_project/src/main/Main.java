package main;

import util.DBConnection;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.Locale;
/**starts the program. enjoy. this was a lot of work. */
public class Main extends Application {



    /**loads login screen */
    @Override
    public void start(Stage stage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view_controller/loginScreen.fxml"));
        view_controller.loginScreenController controller = new view_controller.loginScreenController();
        loader.setController(controller);
        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setResizable(true);
        stage.show();
    }

    /**connects to db and disconnects when program close.
     * @param args
     * @throws SQLException*/
    public static void main(String[] args) throws SQLException {
        DBConnection.startConnection();


        launch(args);

        DBConnection.closeConnection();
    }
}
