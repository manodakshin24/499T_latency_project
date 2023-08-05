package org.example;

import Client.ClientNode;
import Client.ClientNodeMap;

import java.io.IOException;
import java.util.ArrayList;


// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    public static void main(String[] args) {
        // Press Opt+Enter with your caret at the highlighted text to see how
        // IntelliJ IDEA suggests fixing it.
        System.out.println("Hello and welcome!");

        ArrayList<int[]> idPortPairs = new ArrayList<>();

        int[] pair1 = { 1, 50051 };
        int[] pair2 = { 2, 50052 };
        int[] pair3 = { 3, 50053 };
        int[] pair4 = { 4, 50054 };
        int[] pair5 = { 5, 50055 };
        int[] pair6 = { 6, 50056 };
//
        idPortPairs.add(pair1);
        idPortPairs.add(pair2);
        idPortPairs.add(pair3);
        idPortPairs.add(pair4);
        idPortPairs.add(pair5);
        idPortPairs.add(pair6);
//
        ClientNodeMap clientNodeMap = new ClientNodeMap(idPortPairs);
//
        clientNodeMap.addClientNodeConnection(1, 2);
        clientNodeMap.addClientNodeConnection(1, 3);
        clientNodeMap.addClientNodeConnection(1, 4);
        clientNodeMap.addClientNodeConnection(2, 1);
        clientNodeMap.addClientNodeConnection(2, 3);
        clientNodeMap.addClientNodeConnection(2, 5);
        clientNodeMap.addClientNodeConnection(3, 1);
        clientNodeMap.addClientNodeConnection(3, 2);
        clientNodeMap.addClientNodeConnection(3, 6);
        clientNodeMap.addClientNodeConnection(4, 1);
        clientNodeMap.addClientNodeConnection(5, 2);
        clientNodeMap.addClientNodeConnection(6, 3);
//
//
        ClientNode nodeOne = new ClientNode("jdbc:postgresql://localhost:26257/",
                                            "socialnetwork?sslmode=disable",
                                            "org.postgresql.Driver",
                                            "anish",
                                            "",
                                            clientNodeMap,
                                            50051,
                                            1);

        ClientNode nodeTwo = new ClientNode("jdbc:postgresql://localhost:26257/",
                                            "socialnetwork?sslmode=disable",
                                            "org.postgresql.Driver",
                                            "anish",
                                            "",
                                            clientNodeMap,
                                            50052,
                                            2);

        ClientNode nodeThree = new ClientNode("jdbc:postgresql://localhost:26257/",
                                            "socialnetwork?sslmode=disable",
                                            "org.postgresql.Driver",
                                            "anish",
                                            "",
                                            clientNodeMap,
                                            50053,
                                            3);

        ClientNode nodeFour = new ClientNode("jdbc:postgresql://localhost:26257/",
                "socialnetwork?sslmode=disable",
                "org.postgresql.Driver",
                "anish",
                "",
                clientNodeMap,
                50054,
                4);

        ClientNode nodeFive = new ClientNode("jdbc:postgresql://localhost:26257/",
                "socialnetwork?sslmode=disable",
                "org.postgresql.Driver",
                "anish",
                "",
                clientNodeMap,
                50055,
                5);

        ClientNode nodeSix = new ClientNode("jdbc:postgresql://localhost:26257/",
                "socialnetwork?sslmode=disable",
                "org.postgresql.Driver",
                "anish",
                "",
                clientNodeMap,
                50056,
                6);
//
        nodeOne.connectDB();
        nodeTwo.connectDB();
        nodeThree.connectDB();
        nodeFour.connectDB();
        nodeFive.connectDB();
        nodeSix.connectDB();
//
        nodeOne.startMessengers();
        nodeTwo.startMessengers();
        nodeThree.startMessengers();
        nodeFour.startMessengers();
        nodeFive.startMessengers();
        nodeSix.startMessengers();
//
        try {
            nodeOne.startReceiver();
        } catch (IOException e) {
            System.out.println(e);
        }
        try {
            nodeTwo.startReceiver();
        } catch (IOException e) {
            System.out.println(e);
        }
        try {
            nodeThree.startReceiver();
        } catch (IOException e) {
            System.out.println(e);
        }
        try {
            nodeFour.startReceiver();
        } catch (IOException e) {
            System.out.println(e);
        }
        try {
            nodeFive.startReceiver();
        } catch (IOException e) {
            System.out.println(e);
        }
        try {
            nodeSix.startReceiver();
        } catch (IOException e) {
            System.out.println(e);
        }


        nodeSix.executeQueryOne();
//
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
//
//        nodeOne.stopMessengers();
//        nodeTwo.stopMessengers();
//        nodeThree.stopMessengers();
//
//        nodeOne.stopReceiver();
//        nodeTwo.stopReceiver();
//        nodeThree.stopReceiver();
    }

}
