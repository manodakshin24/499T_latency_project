package gRPCClient;

import com.proto.hello.Hello;
import com.proto.hello.HelloRequest;
import com.proto.hello.HelloResponse;
import com.proto.hello.HelloServiceGrpc;
import io.grpc.stub.StreamObserver;

public class ServerImpl extends HelloServiceGrpc.HelloServiceImplBase {
    @Override
    public void hello(HelloRequest request, StreamObserver<HelloResponse> responseObserver) {
        Hello hello = request.getHello();
        int clientID = hello.getClientID();

        // Access the CONTAINER_NAME environment variable
        String containerName = System.getenv("CONTAINER_NAME");
        if (containerName == null) {
            containerName = "Unknown";
        }

        //Create Response
        String result = "Hello Client "+clientID+"! I "+containerName+" have acknowledged your message";
        HelloResponse response = HelloResponse.newBuilder().setResult(result).build();

        //Send the response
        responseObserver.onNext(response);

        //Complete the RPC call
        responseObserver.onCompleted();
    }
}
