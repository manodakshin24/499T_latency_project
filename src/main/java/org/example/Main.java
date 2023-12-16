package org.example;

import ClientLocal.ClientNode;
import ClientLocal.Utils.ClientNodeMap;
import java.io.IOException;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) throws InterruptedException {

        System.out.println("Hello and welcome!");

        ArrayList<int[]> idPortPairs = new ArrayList<>();

        int[] pair1 = { 1, 50051 };
        int[] pair2 = { 2, 50052 };
        int[] pair3 = { 3, 50053 };
        int[] pair4 = { 4, 50054 };
        int[] pair5 = { 5, 50055 };
        int[] pair6 = { 6, 50056 };

        idPortPairs.add(pair1);
        idPortPairs.add(pair2);
        idPortPairs.add(pair3);
        idPortPairs.add(pair4);
        idPortPairs.add(pair5);
        idPortPairs.add(pair6);

        ClientNodeMap clientNodeMap = new ClientNodeMap(idPortPairs);

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

        nodeOne.connectDB();
        nodeTwo.connectDB();
        nodeThree.connectDB();
        nodeFour.connectDB();
        nodeFive.connectDB();
        nodeSix.connectDB();

        nodeOne.startMessengers();
        nodeTwo.startMessengers();
        nodeThree.startMessengers();
        nodeFour.startMessengers();
        nodeFive.startMessengers();
        nodeSix.startMessengers();

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

        ArrayList<Long> times = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            nodeOne.executeQueryOne(true);
            System.out.println("Total time main: " + nodeOne.getTotalTime() + " microseconds");

            times.add(nodeOne.getTotalTime());

            nodeOne.reset();
            nodeTwo.reset();
            nodeThree.reset();
            nodeFour.reset();
            nodeFive.reset();
            nodeSix.reset();

        }

        System.out.println("Average time: " + calculateAverage(times) + " microseconds");

    }

    public static long calculateAverage(ArrayList<Long> numbers) {
        long sum = 0;
        for (Long num : numbers) {
            sum += num;
        }

        // Avoid division by zero
        if (numbers.size() == 0) {
            return 0;
        }

        return sum / numbers.size();
    }

}
