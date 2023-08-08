package gRPCClient;

import com.proto.hello.Hello;
import com.proto.hello.HelloRequest;
import com.proto.hello.HelloResponse;
import com.proto.hello.HelloServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class Client {
    public static void main(String[] args) {
        if(args.length < 2) {
            System.out.println("Usage: Client <server-ip> <server-port>");
            return;
        }

        String serverIP = args[0];
        int serverPort = Integer.parseInt(args[1]);

        System.out.println("Hello! I'm a running Client stub");

        ManagedChannel channel = ManagedChannelBuilder
                .forAddress(serverIP, serverPort)
                .usePlaintext()
                .build();

        //Create a synchronous client
        HelloServiceGrpc.HelloServiceBlockingStub syncClient = HelloServiceGrpc.newBlockingStub(channel);
        //Create a protocol buffer message & send it
        Hello hello = Hello.newBuilder().setClientID(1).build();
        HelloRequest request = HelloRequest.newBuilder().setHello(hello).build();
        HelloResponse response = syncClient.hello(request);
        System.out.println(response.getResult());

        System.out.println("Shutting down ClientOne channel");
        channel.shutdown();
    }
}
