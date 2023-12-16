package ClientDocker.Query;

import com.proto.ShortReadQuery5.Request;
import com.proto.ShortReadQuery5.ShortReadQuery5Request;
import com.proto.ShortReadQuery5.ShortReadQuery5Response;
import com.proto.ShortReadQuery5.ShortReadQuery5ServiceGrpc;
import io.grpc.ManagedChannel;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.*;

public class ShortReadQuery5_Chris implements Runnable {
    private Connection connection;
    private int localId;
    private ArrayList<Integer> neighbors;
    private HashMap<Integer, ManagedChannel> idToChannel;
    private int sleepTime;
    private int messageID=10000;
    private int counter;
    private int queryID;
    private int totalNodes = 4;
    private long creatorPersonID;
    private String firstName;
    private String lastName;
    private List<Future<String>> message_futures = new ArrayList<>();
    private List<Future<String>> person_futures = new ArrayList<>();
    private ExecutorService executor;
    private String temp_message_table_name = "temp_message";
    private String temp_person_table_name = "temp_person";
    private String create_temp_message_table = "CREATE TABLE IF NOT EXISTS temp_message (\n" +
            "    creationDate timestamp with time zone NOT NULL,\n" +
            "    id bigint PRIMARY KEY,\n" +
            "    language varchar(80),\n" +
            "    content varchar(2000),\n" +
            "    imageFile varchar(80),\n" +
            "    locationIP varchar(80) NOT NULL,\n" +
            "    browserUsed varchar(80) NOT NULL,\n" +
            "    length int NOT NULL,\n" +
            "    CreatorPersonId bigint NOT NULL,\n" +
            "    ContainerForumId bigint,\n" +
            "    LocationCountryId bigint NOT NULL,\n" +
            "    ParentMessageId bigint,\n" +
            "    serverId int NOT NULL, -- Add the \"serverId\" field of type INT\n" +
            "    queryId int NOT NULL,\n" +
            "    INDEX (LocationCountryId),\n" +
            "    INDEX (CreatorPersonId),\n" +
            "    INDEX (ContainerForumId),\n" +
            "    INDEX (ParentMessageId)\n" +
            ")";
    private String create_temp_person_table = "CREATE TABLE IF NOT EXISTS temp_person (\n" +
            "    creationDate timestamp with time zone NOT NULL,\n" +
            "    id bigint PRIMARY KEY,\n" +
            "    firstName varchar(80) NOT NULL,\n" +
            "    lastName varchar(80) NOT NULL,\n" +
            "    gender varchar(80) NOT NULL,\n" +
            "    birthday date NOT NULL,\n" +
            "    locationIP varchar(80) NOT NULL,\n" +
            "    browserUsed varchar(80) NOT NULL,\n" +
            "    LocationCityId bigint NOT NULL,\n" +
            "    speaks varchar(640) NOT NULL,\n" +
            "    email varchar(8192) NOT NULL,\n" +
            "    serverId int NOT NULL, -- Add the \"serverId\" field of type INT\n" +
            "    queryId int NOT NULL,\n" +
            "    INDEX (LocationCityId)\n" +
            ")";
    //Query 5
    private Boolean existsInlocalMessage = false;
    private Boolean existsInlocalPerson = false;

    private String find_person_id = "SELECT * FROM message WHERE id="+this.messageID+" AND serverId="+this.localId;
    //private String find_person_id = "SELECT * FROM message_query5 WHERE id="+this.messageID+" AND serverId="+this.localId;

    public ShortReadQuery5_Chris(Connection connection, int localId, ArrayList<Integer> neighbors, HashMap<Integer, ManagedChannel> idToChannel, int sleepTime, int messageID) {
        this.connection = connection;
        this.localId = localId;
        this.neighbors = neighbors;
        this.idToChannel = idToChannel;
        this.sleepTime = sleepTime;
        this.messageID = messageID;
    }

    @Override
    public void run() {
        int[] randArray = new int[5];
            for(int i = 0; i < randArray.length; i++){
                randArray[i] = ((int)(Math.random() * randArray.length)) + 10000;
            }
        try {
            //Create a statement
            Statement statement = connection.createStatement();
            //Use the 'socialnetwork' database
            statement.execute("USE socialnetwork");
            //Run the query locally
            //creating random 10000 t0 19999 & add in an array
            //int[] randArray = new int[10000];
            
            find_person_id = "SELECT * FROM message WHERE id="+randArray[0]+" AND serverId="+this.localId;
            ResultSet resultSet = statement.executeQuery(find_person_id);
            System.out.println("Query1111111111:" +find_person_id);
            if(resultSet.next()) {
                existsInlocalMessage = true;
                this.creatorPersonID = resultSet.getLong("CreatorPersonId");
                System.out.println("PersonID: "+creatorPersonID);
            }
            else {
                // Rows are not available
                System.out.println("No rows found in the result message set locally.");
                //Create temp_message table
                statement.execute(create_temp_message_table);
                System.out.println("Temp message table created successfully");
                this.queryID = this.totalNodes * this.counter + this.localId;
                ++this.counter;

                //If result not found, ping all other nodes
                try {
                    if(this.neighbors.size() > 0) {
                        this.executor = Executors.newFixedThreadPool(this.neighbors.size());
                        String subquery = "SELECT * FROM message WHERE id="+this.messageID+" AND serverId=";
                        subquery = "SELECT * FROM message WHERE id="+randArray[0]+" AND serverId=";
                        //String subquery = "SELECT * FROM message_query5 WHERE id="+this.messageID+" AND serverId=";
                        for(int i = 0; i < this.neighbors.size(); ++i) {
                            //if(i!=0){
                                String serverSubquery = subquery + neighbors.get(i) + "";
                                //Future<String> future = executor.submit(new gRPC(serverSubquery, 1, this.localId, this.neighbors.get(i), this.messageID, this.queryID, this.temp_message_table_name, this.idToChannel));
                                //Future<String> future = executor.submit(new gRPC(serverSubquery, 1, this.localId, this.neighbors.get(i), 10000, this.queryID, this.temp_message_table_name, this.idToChannel));
                                Future<String> future;
                                
                                if(i==1){
                                    future = executor.submit(new gRPC(serverSubquery, 1, 2, this.neighbors.get(i), randArray[0], this.queryID, this.temp_message_table_name, this.idToChannel));
                                }else{
                                    future = executor.submit(new gRPC(serverSubquery, 1, this.localId, this.neighbors.get(i), randArray[0], this.queryID, this.temp_message_table_name, this.idToChannel));
                                }
                                 
                                //future = executor.submit(new gRPC(serverSubquery, 1, this.localId, this.neighbors.get(i), randArray[0], this.queryID, this.temp_message_table_name, this.idToChannel));
                                //System.out.println("at GRPC call() message start:" + "this.neighborID:" + this.neighbors.get(i) + ":::" +System.currentTimeMillis());
                                System.out.println("future:" + future +":i=" +i);
                                message_futures.add(future);
                           // }
                        }
                    }
                }
                catch(Exception e) {
                    System.out.println("Query failed at node: "+this.localId);
                    e.printStackTrace();
                }
            }
        }
        catch(SQLException e) {
            e.printStackTrace();
        }
        if(!existsInlocalMessage){
            //Wait for acknowledgement from other nodes
            List<String> message_results = new ArrayList<>();
            if(this.neighbors.size() > 0) {
                for(Future<String> fut : message_futures) {
                    try {
                        message_results.add(fut.get());
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                }
            }

            System.out.println("I Client Node " + this.localId + " had all message threads return");

            //Fetch person.id from temp table
            System.out.println("Start Person Search: "+System.currentTimeMillis());

            String fetch_person_id = "SELECT * FROM "+temp_message_table_name;
            try {
                Statement statement = connection.createStatement();
                statement.execute("USE socialnetwork");
                System.out.println("Query222222222222:" +fetch_person_id);
                ResultSet fetchSet = statement.executeQuery(fetch_person_id);
                if(fetchSet.next()) {
                    this.creatorPersonID = fetchSet.getLong("CreatorPersonId");
                    System.out.println("PersonID: "+creatorPersonID);
                }
                else {
                    System.out.println("Message row NOT found in temp message table");
                }
            }
            catch(SQLException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Got the Message: "+System.currentTimeMillis());
        //Create temp person table
        //Fetch person.firstName and person.lastName locally
        try {
            int personID = (int)creatorPersonID;
            //int[] randArray = new int[10000];
            
            Statement statement = connection.createStatement();
            statement.execute("USE socialnetwork");
            String findPerson = "SELECT * FROM person WHERE id="+personID+" AND serverId="+this.localId;
            //String findPerson = "SELECT * FROM person_query5 WHERE id="+personID+" AND serverId="+this.localId;
            System.out.println("Query>>>>>>>>>>>>" +findPerson);
            ResultSet personSet = statement.executeQuery(findPerson);
            if(personSet.next()) {
                existsInlocalPerson = true;
                this.firstName = personSet.getString("firstName");
                this.lastName = personSet.getString("lastName");
                System.out.println("First Name: "+this.firstName);
                System.out.println("Last Name: "+this.lastName);
            }
            else {
                // Rows are not available
                System.out.println("No rows found in the result person set locally.");
                //Create temp_message table
                statement.execute(create_temp_person_table);
                System.out.println("Temp person table created successfully");
                String subquery = "SELECT * FROM person WHERE id="+personID+" AND serverId=";
                //String subquery = "SELECT * FROM person_query5 WHERE id="+personID+" AND serverId=";

                //Ping other nodes
                for(int i = 0; i < this.neighbors.size(); ++i) {
                    //if(i != 0){
                        String person_subquery = subquery + this.neighbors.get(i) + "";
                        //Future<String> future = executor.submit(new gRPC(person_subquery, 2, this.localId, this.neighbors.get(i), this.messageID, this.queryID, this.temp_person_table_name, this.idToChannel));
                        //Future<String> future = executor.submit(new gRPC(person_subquery, 2, this.localId, this.neighbors.get(i), 10000, this.queryID, this.temp_person_table_name, this.idToChannel));                   
                        Future<String> future;
                        
                        if(i==1){
                            future = executor.submit(new gRPC(person_subquery, 2, 2, this.neighbors.get(i), randArray[0], this.queryID, this.temp_person_table_name, this.idToChannel));
                        }else{
                            future = executor.submit(new gRPC(person_subquery, 2, this.localId, this.neighbors.get(i), randArray[0], this.queryID, this.temp_person_table_name, this.idToChannel));
                        }
                         
                        //future = executor.submit(new gRPC(person_subquery, 2, this.localId, this.neighbors.get(i), randArray[0], this.queryID, this.temp_person_table_name, this.idToChannel));
                       
                        //System.out.println("at GRPC call() person start:" + "this.neighborID:" + this.neighbors.get(i) + ":::" +System.currentTimeMillis());
                        person_futures.add(future);
                    //}
                }
            }
        }
        catch(SQLException e) {
            e.printStackTrace();
        }
        if(!existsInlocalPerson){
            //Wait for acknowledgement from other nodes\
            List<String> person_results = new ArrayList<>();
            if(this.neighbors.size() > 0) {
                for(Future<String> fut : person_futures) {
                    try {
                        person_results.add(fut.get());
                    }
                    catch(InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                }
                
                System.out.println("going to call this.executor.shutdown()");
                this.executor.shutdown();
            }

            System.out.println("I Client Node " + this.localId + " had all person threads return");

            //Fetch person.firstName and person.lastName from the person temp table
            String fetch_person_name = "SELECT * FROM "+temp_person_table_name;
            try {
                Statement statement = connection.createStatement();
                statement.execute("USE socialnetwork");
                ResultSet nameSet = statement.executeQuery(fetch_person_name);
                if(nameSet.next()) {
                    this.firstName = nameSet.getString("firstName");
                    this.lastName = nameSet.getString("lastName");
                    System.out.println("First Name: "+this.firstName);
                    System.out.println("Last Name: "+this.lastName);
                }
                else {
                    System.out.println("Person row NOT found in temp message table");
                }
            }
            catch(SQLException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Got the Person: "+System.currentTimeMillis());

        // OUTPUT
        System.out.println("-----------OUTPUT--------------");
        System.out.println("Person ID: "+this.creatorPersonID);
        System.out.println("Person First Name: "+this.firstName);
        System.out.println("Person Last Name: "+this.lastName);
    }

    class gRPC implements Callable<String> {
        private String subquery;
        private int flagID;
        private int localID;
        private int neighborID;
        private int messageID;
        private int queryID;
        private String temp_table_name;
        private HashMap<Integer, ManagedChannel> idToChannel;

        public gRPC(String subquery, int flagID, int localID, int neighborID, int messageID, int queryID, String temp_table_name, HashMap<Integer, ManagedChannel> idToChannel) {
            System.out.println("gRPC constructor");
            this.subquery = subquery;
            this.flagID = flagID;
            this.localID = localID;
            this.neighborID = neighborID;
            this.messageID = messageID;
            this.queryID = queryID;
            this.temp_table_name = temp_table_name;
            this.idToChannel = idToChannel;
        }

        @Override
        public String call() {
            System.out.println("ClientNode " + this.localID + " pinging ClientNode " + this.neighborID);
            if(this.neighborID !=0 ){
                ShortReadQuery5ServiceGrpc.ShortReadQuery5ServiceBlockingStub client = ShortReadQuery5ServiceGrpc.newBlockingStub(idToChannel.get(neighborID));
                Request request = Request.newBuilder()
                        .setSubQuery(subquery)
                        .setFlag(flagID)
                        .setMessageId(messageID)
                        .setQueryId(queryID)
                        .setTempTableName(temp_table_name)
                        .build();
                ShortReadQuery5Request SR_request = ShortReadQuery5Request.newBuilder()
                        .setSRQ5Request(request)
                        .build();
                System.out.println("gRPC SR_request:" +SR_request);

                ShortReadQuery5Response SR_response = client.shortReadQuery5(SR_request);
                System.out.println("gRPC SR_response:" +SR_response);

                String msg = SR_response.getSRQ5Response().getNodeResponse();
                System.out.println("message:" +msg);
                return msg;
            }
            else{
               System.out.println("this.neighborID:::" +this.neighborID); 
               return "fake message";
            }
            //return msg;
        }
    }
}
