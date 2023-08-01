package Client;

import com.proto.query.QueryRequest;
import com.proto.query.QueryResponse;
import com.proto.query.QueryServiceGrpc;
import io.grpc.ManagedChannel;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class QueryOne implements Runnable {
    private Connection connection;
    private boolean local;
    private String query = "SELECT * FROM socialnetwork.tmptable WHERE serverID = X";
    private int localId;
    private int sleepTime;
    private QueryServiceGrpc.QueryServiceBlockingStub syncClient;
    private QueryRequest request;

    public QueryOne(QueryServiceGrpc.QueryServiceBlockingStub syncClient, QueryRequest request, Connection connection, boolean local, int localId, int sleepTime) {
        //this.channel = channel;
        this.local = local;
        this.localId = localId;
        this.sleepTime = sleepTime;
        this.syncClient = syncClient;
        this.request = request;
        this.connection = connection;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(this.sleepTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        String res;
        if (this.local == false) {
            QueryResponse response = this.syncClient.query(this.request);
            res = response.getResult();
            System.out.println(res);
        } else {
            String modifiedQueryStr = this.query.replace("serverID = X", "serverID = " + this.localId);
            try {
                ResultSet resultSet = this.connection.createStatement().executeQuery(modifiedQueryStr);
                while (resultSet.next()) {
                    System.out.printf("\tStudent %s: %s\n", resultSet.getInt("userId"), resultSet.getString("name"));
                }
            } catch (SQLException e) {
                System.out.println("Selection Failed");
                //throw new RuntimeException(e);
            }
            res = "I Client Node " + this.localId + " have ran the query: " + modifiedQueryStr;
            System.out.println(res);
            System.out.println("I Client Node " + this.localId + " have sucessfully finished!");
        }
    }
}
