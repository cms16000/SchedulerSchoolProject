package util;


/**@author Christopher Steedley*/

import javafx.collections.ObservableList;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;


/** tracks login activity. tracks login activity and prints to txt file*/
public class LogTracker {
    /** location of log */
    public static String filename = "login_activity.txt";


/**method to track log ins. takes a string of username and if they logged in or not(bool) and prints appropriate message to log*/
    public static void trackLog(String user, boolean successLogIn) {

        try {

            FileWriter write= new FileWriter(filename, true);
            PrintWriter prfile = new PrintWriter(write);
            LocalDateTime dt = LocalDateTime.now();
            if (successLogIn){
            prfile.println(user + " accessed at:  " + dt);
            prfile.close();
            }
            if (!successLogIn){
                prfile.println(user + " attempted to access at:  " + dt);
                prfile.close();
            }

        } catch (IOException e) {
            System.out.println("error: " + e.getMessage());
        }

    }
}
