package rish.crearo.gcmtester.Database;

/**
 * Created by rish on 10/10/15.
 */
public class User {

    private static String userName, password, designation;
    private static int permissionNumber;

    public static void setUserName(String username) {
        userName = username;
    }

    public static String getUserName() {
        return "201301433";
    }

    public static void setPassword(String pwd) {
        password = pwd;
    }

    public static String getPassword() {
        return "rish";
    }

    public static int getPermissionNumber() {
        return 1;
    }

    public static String getDesignation() {
        return "student";
    }
}