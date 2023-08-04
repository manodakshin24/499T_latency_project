package org.example;

import Client.ClientNode;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    public static void main(String[] args) {
        // Press Opt+Enter with your caret at the highlighted text to see how
        // IntelliJ IDEA suggests fixing it.
        System.out.println("Hello and welcome!");
        System.out.println("This is a demo for using JDBC");
        Connection con  = DBConnectionOG.getInstance().getConnection();
        System.out.println(con);
        try {
            con.createStatement().execute("DROP TABLE socialnetwork.tmptable");
            System.out.println("Deletion Success");
        } catch (SQLException e) {
            System.out.println("Deletion Failed");
            //throw new RuntimeException(e);
        }
        try {
            con.createStatement().execute("CREATE TABLE socialnetwork.tmptable (userID INT PRIMARY KEY, serverID INT, name VARCHAR(255));");
            System.out.println("Creation Success");
        } catch (SQLException e) {
            System.out.println("Creation Failed");
            //throw new RuntimeException(e);
        }
        try {
            con.createStatement().execute("INSERT INTO socialnetwork.tmptable VALUES (65, 1, 'Bob'), (66, 2, 'Charlie'), (67, 3, 'Duncan');");
            System.out.println("Insertion Success");
        } catch (SQLException e) {
            System.out.println("Insertion Failed");
            //throw new RuntimeException(e);
        }
        try {
            ResultSet res = con.createStatement().executeQuery("SELECT * FROM socialnetwork.tmptable WHERE serverID = 3;");
            while (res.next()) {
                System.out.printf("\tStudent %s: %s\n", res.getInt("userId"), res.getString("name"));
            }
        } catch (SQLException e) {
            System.out.println("Selection Failed" + e);
            //throw new RuntimeException(e);
        }
//
//        HashMap<Integer, Integer> myMap = new HashMap<>();
//        myMap.put(50051, 1);
//        myMap.put(50052, 2);
//        myMap.put(50053, 3);
//
//        System.out.println(myMap.get(50051));
//        System.out.println(myMap.get(50052));
//        System.out.println(myMap.get(50053));
//
//
//        ClientNode nodeOne = new ClientNode("jdbc:postgresql://localhost:26257/",
//                                            "socialnetwork?sslmode=disable",
//                                            "org.postgresql.Driver",
//                                            "anish",
//                                            "",
//                                            new ArrayList<>(Arrays.asList(50052, 50053)),
//                                            50051,
//                                            1);
//        ClientNode nodeTwo = new ClientNode("jdbc:postgresql://localhost:26257/",
//                                            "socialnetwork?sslmode=disable",
//                                            "org.postgresql.Driver",
//                                            "anish",
//                                            "",
//                                            new ArrayList<>(Arrays.asList(50051, 50053)),
//                                            50052,
//                                            2);
//
//        ClientNode nodeThree = new ClientNode("jdbc:postgresql://localhost:26257/",
//                                            "socialnetwork?sslmode=disable",
//                                            "org.postgresql.Driver",
//                                            "anish",
//                                            "",
//                                            new ArrayList<>(Arrays.asList(50051, 50052)),
//                                            50053,
//                                            3);
//
//        nodeOne.connectDB();
//        nodeTwo.connectDB();
//        nodeThree.connectDB();
//
//        nodeOne.startMessengers();
//        nodeTwo.startMessengers();
//        nodeThree.startMessengers();
//
//        try {
//            nodeOne.startReceiver();
//        } catch (IOException e) {
//            System.out.println(e);
//        }
//        try {
//            nodeTwo.startReceiver();
//        } catch (IOException e) {
//            System.out.println(e);
//        }
//        try {
//            nodeThree.startReceiver();
//        } catch (IOException e) {
//            System.out.println(e);
//        }
//
//        nodeOne.sendHelloMessages();
//        nodeTwo.sendHelloMessages();
//        nodeThree.sendHelloMessages();
//
//        nodeOne.executeQueryOne();
//
//        try {
//            Thread.sleep(5000);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
//
//        nodeOne.stopMessengers();
//        nodeTwo.stopMessengers();
//        nodeThree.stopMessengers();
//
//        nodeOne.stopReceiver();
//        nodeTwo.stopReceiver();
//        nodeThree.stopReceiver();
//
//
//        myObj ex = new myObj("Anish");
//        changeName(ex);
//        System.out.println(ex.getName());
//
//        HashMap<Integer, myObj> myMap2 = new HashMap<>();
//        myMap2.put(1, ex);
//
//        changeNameMap(myMap2);
//        System.out.println(ex.getName());
//
//        ex = ex.getObj();
//        System.out.println(ex.getName());
//
//
//
//
//        // Press Ctrl+R or click the green arrow button in the gutter to run the code.
//        for (int i = 1; i <= 5; i++) {
//
//            // Press Ctrl+D to start debugging your code. We have set one breakpoint
//            // for you, but you can always add more by pressing Cmd+F8.
//            System.out.println("i = " + i);
//        }
    }
    public static void changeName(myObj obj) {
        obj.setName("Anus");
        changeName2(obj);
    }

    public static void changeNameMap(HashMap<Integer, myObj> map) {
        map.get(1).setName("Anus2");
        changeName2Map(map);
    }

    public static void changeName2(myObj obj) {
        obj.setName("Anushy");
    }

    public static void changeName2Map(HashMap<Integer, myObj> map) {
        map.get(1).setName("Anushy2");
    }

    public static class myObj {
        String name;
        public myObj(String name) {
            this.name = name;
        }
        public void setName(String name) {
            this.name = name;
        }
        public String getName() {
            return this.name;
        }
        public myObj getObj (){
            return this;
        }
    }
}
