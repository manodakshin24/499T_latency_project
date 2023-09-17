package ClientDocker.Query;

import com.proto.ping.NodeInfo;
import com.proto.ping.PingRequest;
import com.proto.ping.PingResponse;
import com.proto.ping.PingServiceGrpc;
import io.grpc.ManagedChannel;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.*;

public class QueryDemoNew implements Runnable {
    private Connection connection;
    private String query = "SELECT * FROM socialnetwork.tmptable WHERE serverID = X";
    private int localId;
    private int sleepTime;
    private boolean beganBranching  = true;
    private ArrayList<Integer> neighbors;
    private HashMap<Integer, ManagedChannel> idToChannel;
    private ArrayList<Thread> threads = new ArrayList<>();
    private ExecutorService executor;

    public QueryDemoNew(Connection connection, int localId, ArrayList<Integer> neighbors, HashMap<Integer, ManagedChannel> idToChannel, int sleepTime) {
        this.localId = localId;
        this.sleepTime = sleepTime;
        this.connection = connection;
        this.neighbors = neighbors;
        this.idToChannel = idToChannel;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(this.sleepTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        String modifiedQueryStr = this.query.replace("serverID = X", "serverID = " + this.localId);

        System.out.println("I Client Node " + this.localId + " am running the query: " + modifiedQueryStr);

        List<Future<String>> list = new ArrayList<>();

        try {

            ResultSet resultSet = this.connection.createStatement().executeQuery(modifiedQueryStr);
            while (resultSet.next()) {
                if (this.beganBranching && this.neighbors.size() > 0) {
                    this.beganBranching = false;
                    this.executor = Executors.newFixedThreadPool(this.neighbors.size());
                    for (int i = 0; i < this.neighbors.size(); i++) {
                        Future<String> future = executor.submit(new gRPC(this.neighbors.get(i), this.localId, this.idToChannel));
                        list.add(future);
                    }
                }
                System.out.printf("\tStudent %s: %s\n", resultSet.getInt("userId"), resultSet.getString("name"));
            }

        } catch (Exception e) {
            System.out.println("Query failed at ClientNode " + this.localId);
            return;
        }

        System.out.println("I Client Node " + this.localId + " have successfully finished the query!");

        List<String> results = new ArrayList<>();

        if (this.neighbors.size() > 0) {
            for (Future<String> fut : list) {
                try {
                    results.add(fut.get());

                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
            this.executor.shutdown();
        }



        System.out.println("I Client Node " + this.localId + " had all threads return");
    }

    class gRPC implements Callable<String> {

        private int neighborId;
        private int localId;

        private HashMap<Integer, ManagedChannel> idToChannel;

        public gRPC(int neighborId, int localId, HashMap<Integer, ManagedChannel> idToChannel) {
            this.neighborId = neighborId;
            this.localId = localId;
            this.idToChannel = idToChannel;
        }

        @Override
        public String call() {
            System.out.println("ClientNode " + this.localId + " pinging ClientNode " + this.neighborId);
            PingServiceGrpc.PingServiceBlockingStub client = PingServiceGrpc.newBlockingStub(this.idToChannel.get(neighborId));
            NodeInfo nodeInfo = NodeInfo.newBuilder().setId(this.localId).build();
            PingRequest request = PingRequest.newBuilder().setNodeInfo(nodeInfo).build();
            PingResponse response = client.pingQuery(request);
            String msg = response.getResult();
            System.out.println(msg);
            return msg;
        }
    }
}
