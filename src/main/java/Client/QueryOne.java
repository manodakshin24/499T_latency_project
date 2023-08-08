package Client;

import com.proto.query.Query;
import com.proto.query.QueryRequest;
import com.proto.query.QueryResponse;
import com.proto.query.QueryServiceGrpc;
import io.grpc.ManagedChannel;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class QueryOne implements Runnable {
    private Connection connection;
    private boolean local;
    private String query = "SELECT * FROM socialnetwork.tmptable WHERE serverID = X";
    private int localId;
    private int sleepTime;
    private boolean beganBranching  = true;
    private ArrayList<Integer> neighbors;
    private HashMap<Integer, ManagedChannel> idToChannel;

    public QueryOne(Connection connection, int localId, ArrayList<Integer> neighbors, HashMap<Integer, ManagedChannel> idToChannel, int sleepTime) {
        this.localId = localId;
        this.sleepTime = sleepTime;
        this.connection = connection;
        this.neighbors = neighbors;
        this.idToChannel =idToChannel;
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

        try {

//            for (int i = 0; i < this.neighbors.size(); i++) {
//                int neighborId = neighbors.get(i);
//                Thread thread = new Thread(()->gRPC(neighborId));
//                thread.start();
//            }

            ResultSet resultSet = this.connection.createStatement().executeQuery(modifiedQueryStr);
            while (resultSet.next()) {
                if (this.beganBranching) {
                    this.beganBranching = false;
                    for (int i = 0; i < this.neighbors.size(); i++) {
                        int neighborId = neighbors.get(i);
                        Thread thread = new Thread(()->gRPC(neighborId));
                        thread.start();
                    }
                }
                System.out.printf("\tStudent %s: %s\n", resultSet.getInt("userId"), resultSet.getString("name"));
            }
        } catch (Exception e) {
            System.out.println("Query failed at ClientNode " + this.localId);
            return;
        }

        System.out.println("I Client Node " + this.localId + " have successfully finished the query!");
    }

    public void gRPC(int neighborId) {
        System.out.println("ClientNode " + this.localId + " pinging ClientNode " + neighborId);
        QueryServiceGrpc.QueryServiceBlockingStub client = QueryServiceGrpc.newBlockingStub(this.idToChannel.get(neighborId));
        Query query = Query.newBuilder().setQuery(this.localId).build();
        QueryRequest request = QueryRequest.newBuilder().setQuery(query).build();
        QueryResponse response = client.query(request);
        String msg = response.getResult();
        System.out.println(msg);
    }
}
