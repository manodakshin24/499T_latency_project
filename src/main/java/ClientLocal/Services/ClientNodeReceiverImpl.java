package ClientLocal.Services;

import com.proto.hello.Hello;
import com.proto.hello.HelloRequest;
import com.proto.hello.HelloResponse;
import com.proto.hello.HelloServiceGrpc;
import io.grpc.stub.StreamObserver;

public class ClientNodeReceiverImpl extends HelloServiceGrpc.HelloServiceImplBase {
    private int id;

    public ClientNodeReceiverImpl(int id) {
        this.id = id;
    }
    @Override
    public void hello(HelloRequest request, StreamObserver<HelloResponse> responseObserver) {
        Hello hello = request.getHello();
        int clientID = hello.getClientID();

        //Build Response
        String result = "Hello Client Node "+clientID+"! I Client Node " + this.id + " have acknowledged your request!.";
        HelloResponse response = HelloResponse.newBuilder().setResult(result).build();

        //Send response
        responseObserver.onNext(response);

        //Complete the RPC call
        responseObserver.onCompleted();
    }
}
