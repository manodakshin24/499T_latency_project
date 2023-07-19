package ClientTwo;

import com.proto.hello.Hello;
import com.proto.hello.HelloRequest;
import com.proto.hello.HelloResponse;
import com.proto.hello.HelloServiceGrpc;
import io.grpc.stub.StreamObserver;

public class ClientTwoServerImpl extends HelloServiceGrpc.HelloServiceImplBase {
    @Override
    public void hello(HelloRequest request, StreamObserver<HelloResponse> responseObserver) {
        Hello hello = request.getHello();
        int clientID = hello.getClientID();

        //Build Response
        String result = "Hello Client "+clientID+"! I (Server 2) have acknowledged your response.";
        HelloResponse response = HelloResponse.newBuilder().setResult(result).build();

        //Send response
        responseObserver.onNext(response);

        //Complete the RPC call
        responseObserver.onCompleted();
    }
}
