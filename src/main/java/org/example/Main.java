package org.example;

import java.sql.Connection;
import java.sql.SQLException;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    public static void main(String[] args) {
        // Press Opt+Enter with your caret at the highlighted text to see how
        // IntelliJ IDEA suggests fixing it.
        System.out.printf("Hello and welcome!");
        System.out.println("This is a demo for using JDBC");
        Connection con  = DBConnection.getInstance().getConnection();
        System.out.println(con);
        try {
            con.createStatement().execute("CREATE TABLE socialnetwork.tmptable (userID INT PRIMARY KEY, serverID INT, name VARCHAR(255))");
        } catch (SQLException e) {
            System.out.println("Creation Failed");
            throw new RuntimeException(e);
        }
        try {
            con.createStatement().execute("INSERT INTO socialnetwork.tmptable VALUES (65, 1, 'Bob'), (66, 2, 'Charlie')");
        } catch (SQLException e) {
            System.out.println("Insertion Failed");
            throw new RuntimeException(e);
        }

        // Press Ctrl+R or click the green arrow button in the gutter to run the code.
        for (int i = 1; i <= 5; i++) {

            // Press Ctrl+D to start debugging your code. We have set one breakpoint
            // for you, but you can always add more by pressing Cmd+F8.
            System.out.println("i = " + i);
        }
    }
}