package Client;

import com.proto.query.Query;
import com.proto.query.QueryRequest;
import com.proto.query.QueryResponse;
import com.proto.query.QueryServiceGrpc;
import io.grpc.stub.StreamObserver;

public class ClientNodeQueryOneImpl extends QueryServiceGrpc.QueryServiceImplBase {

    private ClientNode clientNode;

    public ClientNodeQueryOneImpl(ClientNode clientNode) {
        this.clientNode = clientNode;
    }

    @Override
    public void query(QueryRequest request, StreamObserver<QueryResponse> responseObserver) {

        Query query = request.getQuery();
        int prevClientNodeID = query.getQuery();

        if (this.clientNode.isVisited()) {
            System.out.println("ClientNode " + this.clientNode.getId() + " already visited");
        } else {
            this.clientNode.setJustCameFromClientNodeId(prevClientNodeID);
            this.clientNode.executeQueryOne();
        }

        QueryResponse response = QueryResponse.newBuilder().setResult("Ping from ClientNode " + prevClientNodeID + " to ClientNode " + this.clientNode.getId() + " success!").build();

        //Send response
        responseObserver.onNext(response);

        //Complete the RPC call
        responseObserver.onCompleted();
    }




}
