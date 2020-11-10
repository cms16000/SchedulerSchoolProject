package util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**@author Chris Steedley */

/** creates statement objects. Allows the retrieval of queries through setter/getters. */
public class DBQuery {

    private static String query;
    private static Statement statement;
    private static ResultSet rs;

    /** create statment object */
    public static void setStatement(Connection conn) throws SQLException {
        statement = conn.createStatement();
    }

    /** return statement object */
    public static Statement getStatement() {
        return statement;
    }


    public static ResultSet getResult(){
        return rs;
    }
/**easy search method. just makes future searches a little easier/quicker. */
    public static void searchDB(String query) throws SQLException {
        Statement statement = DBConnection.startConnection().createStatement();
       if(query.startsWith("select"))
        rs = statement.executeQuery(query);
       else{
           statement.executeUpdate(query);
       }

    }

}
