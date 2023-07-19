package ClientTwo;

import com.proto.hello.Hello;
import com.proto.hello.HelloRequest;
import com.proto.hello.HelloResponse;
import com.proto.hello.HelloServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class ClientTwoClient {
    public static void main(String[] args) {
        System.out.println("Hello! I'm the ClientTwo Client stub");

        ManagedChannel channel = ManagedChannelBuilder
                .forAddress("localhost", 50051)
                .usePlaintext()
                .build();

        //Create a synchronous client
        HelloServiceGrpc.HelloServiceBlockingStub syncClient = HelloServiceGrpc.newBlockingStub(channel);
        //Create a protocol buffer message & send it
        Hello hello = Hello.newBuilder().setClientID(2).build();
        HelloRequest request = HelloRequest.newBuilder().setHello(hello).build();
        HelloResponse response = syncClient.hello(request);
        System.out.println(response.getResult());

        System.out.println("Shutting down ClientTwo channel");
        channel.shutdown();
    }
}
