package model;


/**@author Chris steedley*/

/**class user. for creating user objects */
public class User {
    private int userId;
    private String userName;
    private String userPassword;
    private boolean isOnline;


    /**
     * Constructor for building user object
     */
    public User(int userId, String userName, String userPassword) {
        this.userId = userId;
        this.userName = userName;
        this.userPassword = userPassword;

    }

    /**
     * getters and setters for object user information
     *
     */
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

}


