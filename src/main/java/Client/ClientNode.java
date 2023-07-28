package Client;

import com.proto.hello.Hello;
import com.proto.hello.HelloRequest;
import com.proto.hello.HelloResponse;
import com.proto.hello.HelloServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;
import java.sql.Connection;

public class ClientNode {

    private String url;
    private String db;
    private String driver;
    private String username;
    private String password;
    private int messengerPort;
    private int receiverPort;
    private Connection connection;
    private boolean isConnected = false;
    private ManagedChannel messenger;
    private Server receiver;
    private int id;
    private boolean receiverRunning = false;

    public ClientNode(String url, String db, String driver, String username, String password, int messengerPort, int receiverPort, int id) {
        this.url = url;
        this.db = db;
        this.driver = driver;
        this.username = username;
        this.password = password;
        this.messengerPort = messengerPort;
        this.receiverPort = receiverPort;
        this.id = id;
    }

    public void connectDB() throws RuntimeException {
        try {
            this.connection = DBConnection.getInstance().setConnectionParams(this.url, this.db, this.driver, this.username, this.password).getConnection();
            this.isConnected = true;
            System.out.println("Client Node " + this.id + " is connect to DB!");
        } catch (RuntimeException e) {
            System.out.println(e);
        }
    }

    public void startMessenger() {
        this.messenger = ManagedChannelBuilder
                .forAddress("localhost", this.messengerPort)
                .usePlaintext()
                .build();
        System.out.println("Client Node " + this.id + " started messenger");
    }

    public void sendHelloMessenge() {
        //Create a synchronous client
        HelloServiceGrpc.HelloServiceBlockingStub syncClient = HelloServiceGrpc.newBlockingStub(this.messenger);
        //Create a protocol buffer message & send it
        Hello hello = Hello.newBuilder().setClientID(this.id).build();
        HelloRequest request = HelloRequest.newBuilder().setHello(hello).build();
        HelloResponse response = syncClient.hello(request);
        System.out.println(response.getResult());
    }

    public void stopMessenger() {
        this.messenger.shutdown();
        System.out.println("Client Node " + this.id + " has shut down messenger");
    }

    public void startReceiver() throws IOException {
        this.receiver = ServerBuilder.forPort(this.receiverPort)
                .addService(new ClientNodeReceiverImpl(this.id))
                .build();
        this.receiver.start();
        this.receiverRunning = true;
        System.out.println("Client Node " + this.id + " started receiver");

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (this.receiverRunning) {
                System.out.println("Received Shutdown Request");
                this.receiver.shutdown();
                System.out.println("Successfully stopped the server");
            }
        }));
    }

    public void stopReceiver() {
        this.receiver.shutdown();
        this.receiverRunning = false;
        System.out.println("Client Node " + this.id + " stopped receiver");
    }
}
