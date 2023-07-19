package ClientTwo;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class ClientTwoServer {
    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("Hello! I am the ClientTwo Server stub");
        Server server = ServerBuilder.forPort(50052)
                .addService(new ClientTwoServerImpl())
                .build();
        server.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Received Shutdown Request");
            server.shutdown();
            System.out.println("Successfully stopped the server");
        }));

        server.awaitTermination();
    }
}
