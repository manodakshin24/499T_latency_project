package Client;

import com.proto.hello.Hello;
import com.proto.hello.HelloRequest;
import com.proto.hello.HelloResponse;
import com.proto.hello.HelloServiceGrpc;
import com.proto.query.Query;
import com.proto.query.QueryRequest;
import com.proto.query.QueryServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import java.util.ArrayList;

import java.io.IOException;
import java.sql.Connection;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ClientNode {

    private String url;
    private String db;
    private String driver;
    private String username;
    private String password;
    private ArrayList<Integer> messengerPorts;
    private int receiverPort;
    private Connection connection;
    private boolean isConnected = false;
    private ArrayList<ManagedChannel> messengers = new ArrayList<ManagedChannel>();
    private Server receiver;
    private int id;
    private boolean receiverRunning = false;

    public ClientNode(String url, String db, String driver, String username, String password, ArrayList<Integer> messengerPorts, int receiverPort, int id) {
        this.url = url;
        this.db = db;
        this.driver = driver;
        this.username = username;
        this.password = password;
        this.messengerPorts = messengerPorts;
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

    public void startMessengers() {
        for (int i = 0; i < this.messengerPorts.size(); i++) {
            this.messengers.add(
                    ManagedChannelBuilder
                            .forAddress("localhost", this.messengerPorts.get(i))
                            .usePlaintext()
                            .build()
            );
            System.out.println("Client Node " + this.id + " started messenger for port: " + this.messengerPorts.get(i));
        }
    }

    public void sendHelloMessages() {
        //Create a synchronous client
        HelloServiceGrpc.HelloServiceBlockingStub syncClient;
        for (int i = 0; i < this.messengers.size(); i++) {
            syncClient = HelloServiceGrpc.newBlockingStub(this.messengers.get(i));
            //Create a protocol buffer message & send it
            Hello hello = Hello.newBuilder().setClientID(this.id).build();
            HelloRequest request = HelloRequest.newBuilder().setHello(hello).build();
            HelloResponse response = syncClient.hello(request);
            System.out.println(response.getResult());
        }
    }

    public void stopMessengers() {
        for (int i = 0; i < this.messengers.size(); i++) {
            this.messengers.get(i).shutdown();
        }
        System.out.println("Client Node " + this.id + " has shut down all messengers");
    }

    public void startReceiver() throws IOException {
        this.receiver = ServerBuilder.forPort(this.receiverPort)
                .addService(new ClientNodeReceiverImpl(this.id))
                .addService(new ClientNodeQueryOneImpl(this.id, getDBConnection()))
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

    public void executeQueryOne() {
        int numOfNeighbors = this.messengers.size();

        ArrayList<Thread> threads = new ArrayList<>();

        Random random = new Random();

        int randomNumber = random.nextInt(9001) + 1000;

        for (int i = 0; i < numOfNeighbors + 1; i++) {

            randomNumber = random.nextInt(9001) + 1000;

            System.out.println(randomNumber);

            if (i == 0) {
                threads.add(new Thread(new QueryOne(null, null, getDBConnection(), true, this.id, 0)));
            } else {
                QueryServiceGrpc.QueryServiceBlockingStub syncClient = QueryServiceGrpc.newBlockingStub(this.messengers.get(i - 1));
                //Create a protocol buffer message & send it
                Query query = Query.newBuilder().setQuery("SELECT * FROM socialnetwork.tmptable WHERE serverID = X").build();
                QueryRequest request = QueryRequest.newBuilder().setQuery(query).build();

                threads.add(new Thread(new QueryOne(syncClient, request, null, false, this.id, 0)));
            }

        }

        for (int i = 0; i < numOfNeighbors + 1; i++) {
            threads.get(i).start();
        }

    }

    public Connection getDBConnection() {
        return this.connection;
    }
}
