package Client;

import com.proto.query.Query;
import com.proto.query.QueryRequest;
import com.proto.query.QueryResponse;
import com.proto.query.QueryServiceGrpc;
import io.grpc.stub.StreamObserver;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ClientNodeQueryOneImpl extends QueryServiceGrpc.QueryServiceImplBase {

    private ClientNode clientNode;

    Lock lock = new ReentrantLock();

    public ClientNodeQueryOneImpl(ClientNode clientNode) {
        this.clientNode = clientNode;
    }

    @Override
    public void query(QueryRequest request, StreamObserver<QueryResponse> responseObserver) {

        Query query = request.getQuery();
        int prevClientNodeID = query.getQuery();

        this.lock.lock();
        if (this.clientNode.isVisited()) {
            this.lock.unlock();
            System.out.println("ClientNode " + this.clientNode.getId() + " already visited");
        } else {
            this.clientNode.setJustCameFromClientNodeId(prevClientNodeID);
            this.clientNode.executeQueryOne();
            this.lock.unlock();
        }

        QueryResponse response = QueryResponse.newBuilder().setResult("Ping from ClientNode " + prevClientNodeID + " to ClientNode " + this.clientNode.getId() + " success!").build();

        //Send response
        responseObserver.onNext(response);

        //Complete the RPC call
        responseObserver.onCompleted();
    }




}
