package ClientOne;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class ClientOneServer {
    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("Hello! I am the ClientOne Server stub");
        Server server = ServerBuilder.forPort(50051)
                .addService(new ClientOneServerImpl())
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
