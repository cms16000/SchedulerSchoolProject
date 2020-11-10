package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


/**@author Chris Steedley*/

/**establishes connection to database. establishes jdbc driver and jdbc url*/
public class DBConnection {

    public static String protocol = "jdbc";
    public static String vendorName = ":mysql:";
    public static String serverName = "//wgudb.ucertify.com/WJ07zq4";

    private static final String jdbcURL = protocol + vendorName + serverName;

    private static final String JDBCDriver = "com.mysql.cj.jdbc.Driver";
    private static Connection conn = null;

    private static final String userName = "U07zq4";
    private static final String password = "53689178820";

    /** method that does the actual connecting to database. On successful connection Prints connection successful in console*/
    public static Connection startConnection()
    {
        try {
            Class.forName(JDBCDriver);
            conn = DriverManager.getConnection(jdbcURL, userName, password);
        }
        catch (ClassNotFoundException | SQLException e)
        {
            System.out.println(e.getMessage());
        }
        return conn;
    }
/** closes connection to database. on success prints connection closed to console.*/
    public static void closeConnection()
    {
        try {
            conn.close();
        }
        catch (SQLException e)
        {
            System.out.println(("Error: " + e.getMessage()));
        }
    }

}
