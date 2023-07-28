package Client;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {

    private static DBConnection dbInstance;
    private static Connection con;
    private String url;
    private String db;
    private String driver;
    private String username;
    private String password;


    private DBConnection() {
        // private constructor //
    }

    public static DBConnection getInstance() {
        if (dbInstance == null) {
            dbInstance = new DBConnection();
        }
        return dbInstance;
    }

    public DBConnection setConnectionParams(String url, String db, String driver, String username, String password) {
        this.url = url;
        this.db = db;
        this.driver = driver;
        this.username = username;
        this.password = password;
        return dbInstance;
    }

    public Connection getConnection() {

        if (con == null) {
            String url = this.url; //"jdbc:postgresql://127.0.0.1:26257/";
            String dbName = this.db; //"school?sslmode=disable";
            String driver = this.driver;
            String userName = this.username;
            String password = this.password;
            try {
                Class.forName(driver).newInstance();
                this.con = DriverManager.getConnection(url + dbName, userName, password);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        return con;
    }
}
