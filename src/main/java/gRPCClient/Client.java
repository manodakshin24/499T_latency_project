package gRPCClient;

import com.proto.hello.Hello;
import com.proto.hello.HelloRequest;
import com.proto.hello.HelloResponse;
import com.proto.hello.HelloServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Client {
    public static void main(String[] args) {
        String containerName = System.getenv("CONTAINER_NAME");
        if (containerName == null) {
            containerName = "Unknown";
        }
        int clientID = extractServerNumber(containerName);

        if (args.length < 1) {
            System.out.println("Usage: Client <container-name>");
            return;
        }

        String targetContainerName = args[0];

        System.out.println("Hello! I'm a running Client-"+clientID+" stub");
        System.out.println("Sending a hello request to "+targetContainerName);

        String targetIpAddress = getContainerIpAddress(targetContainerName);

        ManagedChannel channel = ManagedChannelBuilder
                .forAddress(targetIpAddress, 50051)
                .usePlaintext()
                .build();

        // Create a synchronous client
        HelloServiceGrpc.HelloServiceBlockingStub syncClient = HelloServiceGrpc.newBlockingStub(channel);

        // Create a protocol buffer message & send it
        Hello hello = Hello.newBuilder().setClientID(clientID).build();
        HelloRequest request = HelloRequest.newBuilder().setHello(hello).build();

        HelloResponse response = syncClient.hello(request);
        System.out.println(response.getResult());

        System.out.println("Shutting down Client-"+clientID+" channel");
        channel.shutdown();
    }

    private static String getContainerIpAddress(String containerName) {
        try {
            // Use the container name as the hostname to resolve to its IP address
            InetAddress inetAddress = InetAddress.getByName(containerName);
            return inetAddress.getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return "Unknown";
    }

    private static int extractServerNumber(String containerName) {
        int lastHyphenIndex = containerName.lastIndexOf("-");
        if (lastHyphenIndex != -1) {
            try {
                return Integer.parseInt(containerName.substring(lastHyphenIndex + 1));
            } catch (NumberFormatException e) {
                // Ignore and return -1 or handle the error as needed
            }
        }
        return -1; // Unable to extract server number
    }
}

