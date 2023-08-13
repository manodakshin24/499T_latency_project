package Client.Query;

import com.proto.ping.NodeInfo;
import com.proto.ping.PingRequest;
import com.proto.ping.PingResponse;
import com.proto.ping.PingServiceGrpc;
import io.grpc.ManagedChannel;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;

public class QueryDemoOld implements Runnable {
    private Connection connection;
    private String query = "SELECT * FROM socialnetwork.tmptable WHERE serverID = X";
    private int localId;
    private int sleepTime;
    private boolean beganBranching  = true;
    private ArrayList<Integer> neighbors;
    private HashMap<Integer, ManagedChannel> idToChannel;

    public QueryDemoOld(Connection connection, int localId, ArrayList<Integer> neighbors, HashMap<Integer, ManagedChannel> idToChannel, int sleepTime) {
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
        PingServiceGrpc.PingServiceBlockingStub client = PingServiceGrpc.newBlockingStub(this.idToChannel.get(neighborId));
        NodeInfo nodeInfo = NodeInfo.newBuilder().setId(this.localId).build();
        PingRequest request = PingRequest.newBuilder().setNodeInfo(nodeInfo).build();
        PingResponse response = client.pingQuery(request);
        String msg = response.getResult();
        System.out.println(msg);
    }
}
