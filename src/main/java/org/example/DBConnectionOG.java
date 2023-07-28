package org.example;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnectionOG {

    private static DBConnectionOG dbInstance;
    private static Connection con;


    private DBConnectionOG() {
        // private constructor //
    }

    public static DBConnectionOG getInstance() {
        if (dbInstance == null) {
            dbInstance = new DBConnectionOG();
        }
        return dbInstance;
    }

    public Connection getConnection() {

        if (con == null) {
            String url = "jdbc:postgresql://localhost:26257/"; //"jdbc:postgresql://127.0.0.1:26257/";
            String dbName = "socialnetwork?sslmode=disable"; //"school?sslmode=disable";
            String driver = "org.postgresql.Driver";
            String userName = "anish";
            String password = "";
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
