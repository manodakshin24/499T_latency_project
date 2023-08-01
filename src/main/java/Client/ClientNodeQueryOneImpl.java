package Client;

import com.proto.query.Query;
import com.proto.query.QueryRequest;
import com.proto.query.QueryResponse;
import com.proto.query.QueryServiceGrpc;
import io.grpc.stub.StreamObserver;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ClientNodeQueryOneImpl extends QueryServiceGrpc.QueryServiceImplBase {
    private int id;

    private Connection connection;

    public ClientNodeQueryOneImpl(int id, Connection connection) {
        this.id = id;
        this.connection = connection;
    }

    @Override
    public void query(QueryRequest request, StreamObserver<QueryResponse> responseObserver) {
        Query query = request.getQuery();
        String queryStr = query.getQuery();

        String modifiedQueryStr = queryStr.replace("serverID = X", "serverID = " + this.id);

        try {
            ResultSet resultSet = this.connection.createStatement().executeQuery(modifiedQueryStr);
            while (resultSet.next()) {
                System.out.printf("\tStudent %s: %s\n", resultSet.getInt("userId"), resultSet.getString("name"));
            }
        } catch (SQLException e) {
            System.out.println("Selection Failed");
            //throw new RuntimeException(e);
        }

        System.out.println("I Client Node " + this.id + " have ran the query: " + modifiedQueryStr);

        String result = "I Client Node " + this.id + " have sucessfully finished!";
        QueryResponse response = QueryResponse.newBuilder().setResult(result).build();

        //Send response
        responseObserver.onNext(response);

        //Complete the RPC call
        responseObserver.onCompleted();
    }




}
