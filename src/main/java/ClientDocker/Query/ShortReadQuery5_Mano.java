package ClientDocker.Query;

import com.proto.ShortReadQuery5.Request;
import com.proto.ShortReadQuery5.ShortReadQuery5Request;
import com.proto.ShortReadQuery5.ShortReadQuery5Response;
import com.proto.ShortReadQuery5.ShortReadQuery5ServiceGrpc;
import io.grpc.ManagedChannel;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.util.Date;
import java.sql.Timestamp;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.*;

public class ShortReadQuery5_Mano implements Runnable {
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
    private Boolean existsInlocalMessage = false;
    private Boolean existsInlocalPerson = false;
    private Boolean existsInCacheMessage = false;
    private Boolean existsInCachePerson = false;
    private int maxCachecount = 100;

    //Query 5
    private String find_person_id = "SELECT * FROM message WHERE id="+this.messageID+" AND serverId="+this.localId;
    //cache
    /*
    private String find_message_id_cache = "SELECT * FROM message WHERE id="+this.messageID+" AND serverId="+this.localId +
                                            " UNION " +
                                            "SELECT * FROM message_query5 WHERE id="+this.messageID+" AND serverId="+this.localId;
     */
    private String find_message_id_cache = "SELECT * FROM message_query5 WHERE id="+this.messageID+" AND serverId="+this.localId;

    //private String find_person_id = "SELECT * FROM message_query5 WHERE id="+this.messageID+" AND serverId="+this.localId;

    public ShortReadQuery5_Mano(Connection connection, int localId, ArrayList<Integer> neighbors, HashMap<Integer, ManagedChannel> idToChannel, int sleepTime, int messageID) {
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
            
            //bulkInsertMessage();
            //bulkInsertPerson();
            /*
            try{
                Thread.sleep(10000);
            }
            catch(InterruptedException e) {
                e.printStackTrace();
            }
             */
            //Create a statement
            Statement statement = connection.createStatement();
            Statement statementCache = connection.createStatement();
            //Use the 'socialnetwork' database
            statement.execute("USE socialnetwork");
            //Run the query locally
            find_person_id = "SELECT * FROM message WHERE id="+randArray[0]+" AND serverId="+this.localId;
            find_message_id_cache = "SELECT * FROM message_query5 WHERE id="+randArray[0]+" AND serverId="+this.localId;
            System.out.println("Query1111111111 orig:" +find_person_id);
            
            ResultSet resultSetOriginal = statement.executeQuery(find_person_id);
            
            
            if(resultSetOriginal.next()) {
                existsInlocalMessage = true;
                this.creatorPersonID = resultSetOriginal.getLong("CreatorPersonId");
                System.out.println("PersonID from Message: "+creatorPersonID);
            }
            else {
                //Check the cache
                System.out.println("message else");
                System.out.println("Query1111111111 cache:" +find_message_id_cache);
                ResultSet resultCache = statementCache.executeQuery(find_message_id_cache);
                if (resultCache.next()){
                    existsInCacheMessage = true;
                    this.creatorPersonID = resultCache.getLong("CreatorPersonId");
                    System.out.println("PersonID from Message Cache: "+creatorPersonID);
                }
                else{
                    System.out.println("message else else");
                    /*
                    java.util.Date today = new java.util.Date();
                    String InsertMessageCache = "INSERT INTO socialnetwork.message_query5 VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";               
                    PreparedStatement preparedStatement = connection.prepareStatement(InsertMessageCache);
                    //preparedStatement.setTimeStamp(1, new java.util.Timestamp(System.currentTimeMillis()));
                    preparedStatement.setDate(1, new java.sql.Date(today.getTime()));
                    preparedStatement.setInt(2,100);
                    preparedStatement.setString(3,"FA");
                    preparedStatement.setString(4,"Content");
                    preparedStatement.setString(5,"ImageFile");
                    preparedStatement.setString(6,"77.245.239.11");
                    preparedStatement.setString(7,"Firefox");
                    preparedStatement.setInt(8,108);
                    preparedStatement.setInt(9,0);
                    preparedStatement.setInt(10,0);
                    preparedStatement.setInt(11,80);
                    preparedStatement.setInt(12,0);
                    preparedStatement.setInt(13,1);               
                    System.out.println("result::::" +result);
                    
                    Statement statementInsert = connection.createStatement();
                    Date date = new Date();
                    Timestamp timestamp = new Timestamp(date.getTime());
                    String currentTime = timestamp.toString();
                    int result = statementInsert.executeUpdate("Insert into socialnetwork.message_query5 VALUES ('" +currentTime + "', 101, 'fa', 'About Wolfgang Amadeus Mozart, financial security. DuringAbout Thomas Jefferson, al slave trade, and adAbout Hen', NULL, '77.245.239.11', 'Firefox', 108, 14, 0, 80, NULL, 1) ");
                    System.out.println("after insertinging into cache");               
                    System.out.println("result::::" +result);
                    */
                    //insert into cache ends

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
                                String serverSubquery = subquery + neighbors.get(i) + "";
                                //Future<String> future = executor.submit(new gRPC(serverSubquery, 1, this.localId, this.neighbors.get(i), this.messageID, this.queryID, this.temp_message_table_name, this.idToChannel));
                                Future<String> future = executor.submit(new gRPC(serverSubquery, 1, this.localId, this.neighbors.get(i), randArray[0], this.queryID, this.temp_message_table_name, this.idToChannel));
                                
                                message_futures.add(future);
                            }
                        }
                    }
                    catch(Exception e) {
                        System.out.println("Query failed at node: "+this.localId);
                        e.printStackTrace();
                    }
                }
            }
        }
        catch(SQLException e) {
            e.printStackTrace();
            System.out.println("Some Exception:" + e.getMessage());
        }

        System.out.println("Got the Message: "+System.currentTimeMillis());

        if (! existsInlocalMessage && !existsInCacheMessage) 
        {
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
            String fetch_person_id = "SELECT * FROM "+temp_message_table_name;
        // Statement statementInsert = connection.createStatement();
            Date date = new Date();
            Timestamp timestamp = new Timestamp(date.getTime());
            String currentTime = timestamp.toString();
            //int result = statementInsert.executeUpdate("Insert into socialnetwork.message_query5 VALUES ('" +currentTime + "', 101, 'fa', 'About Wolfgang Amadeus Mozart, financial security. DuringAbout Thomas Jefferson, al slave trade, and adAbout Hen', NULL, '77.245.239.11', 'Firefox', 108, 14, 0, 80, NULL, 1) ");
            //System.out.println("after insertinging into cache");   
            
            boolean hasDataInTemp=false;
            String creationDate_c="";
            long id_c = 0;
            String language_c = "it";
            String content_c = "";
            String imageFile_c = "";
            String locationIP_c = "";
            String browserUsed_c="";
            long length_c=0;
            long CreatorPersonId_c=0;
            long ContainerForumId_c=0;
            long LocationCountryId_c=0;
            long ParentMessageId_c=0;
            long serverId_c=0;

            try {
                Statement statement = connection.createStatement();
                statement.execute("USE socialnetwork");
                System.out.println("Query222222222222:" +fetch_person_id);
                ResultSet fetchSet = statement.executeQuery(fetch_person_id);
                if(fetchSet.next()) {
                    hasDataInTemp = true;
                    this.creatorPersonID = fetchSet.getLong("CreatorPersonId");
                    id_c = fetchSet.getLong("id");
                    language_c = fetchSet.getString("language");
                    content_c = fetchSet.getString("content");
                    imageFile_c = fetchSet.getString("imageFile");
                    locationIP_c = fetchSet.getString("locationIP");
                    browserUsed_c = fetchSet.getString("browserUsed");
                    length_c = fetchSet.getLong("length");
                    ContainerForumId_c = fetchSet.getLong("ContainerForumId");
                    LocationCountryId_c = fetchSet.getLong("LocationCountryId");
                    ParentMessageId_c = fetchSet.getLong("ParentMessageId");
                    serverId_c = fetchSet.getLong("serverId");

                    System.out.println("PersonID: "+creatorPersonID);
                }
                else {
                    System.out.println("Message row NOT found in temp message table");
                }
                if(hasDataInTemp){
                    Statement statementCacheCheck = connection.createStatement();
                    statementCacheCheck.execute("USE socialnetwork");
                    String SQLToCheckIfDataAlreadyExistsInCache = "SELECT * FROM message_query5 WHERE id="+id_c+" AND serverId="+serverId_c;
                    ResultSet resultSetCheck = statementCacheCheck.executeQuery(SQLToCheckIfDataAlreadyExistsInCache);

                    String selectCountFromCacheTable = "SELECT COUNT(*) FROM socialnetwork.message_query5";

                    String deleteServerId = "DELETE FROM socialnetwork.message_query5 WHERE (id,serverId) = (SELECT id, serverId FROM socialnetwork.message_query5 order by(creationDate) ASC LIMIT 1)";
                    
                    Statement statementForCount = connection.createStatement();
                    Statement statementForDeletion = connection.createStatement();
                    ResultSet count = statementForCount.executeQuery(selectCountFromCacheTable);
                    //ResultSet recordToDelete = statementCacheCheck.executeQuery(selectIdToBeDeleted);
                    if(count.next()){
                        if(count.getInt(1) == maxCachecount){
                            System.out.println(count.getInt(1));
                            System.out.println("Starting deletion from cache");
                            statementForDeletion.executeUpdate(deleteServerId);
                            System.out.println("Deletion from cache complete");
                        }
                        else{
                            System.out.println("Cache Count: " + count.getInt(1));
                            System.out.println("Cache is still open");
                        }
                    }
                    
            
                    if(!resultSetCheck.next()) {
                        Statement statementInsert = connection.createStatement();
                        System.out.println("No data in cache for message, so insert now to cache");
                        statementInsert.executeUpdate("Insert into socialnetwork.message_query5 VALUES ('" +currentTime + "', " + id_c + ", '" + language_c + "', '"+ content_c + "', '" + imageFile_c + "', '" + locationIP_c +"', 'Firefox', 108, " + this.creatorPersonID + ", 0, 80, NULL, " + serverId_c + ") ");
                        System.out.println("Inserted into cache");
                    }
                    else{
                        existsInCacheMessage = true;
                        System.out.println("SQL::" +SQLToCheckIfDataAlreadyExistsInCache);
                    }
                }
            }
            catch(SQLException e) {
                e.printStackTrace();
                System.out.println("Exception:" + e.getMessage());
            }
        }   
        System.out.println("existsInlocalMessage:" + existsInlocalMessage);
        System.out.println("existsInCacheMessage:" +existsInCacheMessage);
        if(existsInCacheMessage)
        {        
            Date date = new Date();
            Timestamp timestamp = new Timestamp(date.getTime());
            String currentTime = timestamp.toString();
            // Run update to set the creationdate to currenttime
            try{
                Statement statement = connection.createStatement();
                statement.executeUpdate("UPDATE socialnetwork.message_query5 SET creationDate = '" + currentTime + "' WHERE id = " + randArray[0]);
            }catch(Exception e){
                System.out.println("Exception in updating cache time stamp" + e.getMessage());
            }
            

        }
        //Create temp person table
        //Fetch person.firstName and person.lastName locally

        System.out.println("Start the Person: "+System.currentTimeMillis());

        try {
            int personID = (int)creatorPersonID;
            Statement statement = connection.createStatement();
            Statement statementCachePerson = connection.createStatement();
            statement.execute("USE socialnetwork");
            String findPerson = "SELECT * FROM person WHERE id="+personID+" AND serverId="+this.localId;
            // Union query 
            /*
            String find_person_id_cache = "SELECT * FROM person WHERE id="+personID+" AND serverId="+this.localId +
                                                " UNION " +
                                           "SELECT * FROM person_query5 WHERE id="+personID+" AND serverId="+this.localId;
             */
            //String findPerson = "SELECT * FROM person_query5 WHERE id="+personID+" AND serverId="+this.localId;

            String find_person_id_cache = "SELECT * FROM person_query5 WHERE id="+personID+" AND serverId="+this.localId;
            System.out.println("Query>>findPerson>>>>>>>>>" +findPerson);
            
            ResultSet personSet = statement.executeQuery(findPerson);

            if(personSet.next()) {
                existsInlocalPerson = true;
                this.firstName = personSet.getString("firstName");
                this.lastName = personSet.getString("lastName");
                System.out.println("First Name: "+this.firstName);
                System.out.println("Last Name: "+this.lastName);
            }
            else {
                System.out.println("person else");
                System.out.println("Query>>person>> cache>>>>>>>>" +find_person_id_cache);
                ResultSet personSetCache = statementCachePerson.executeQuery(find_person_id_cache);
                if(personSetCache.next()){
                    existsInCachePerson = true;
                    this.firstName = personSetCache.getString("firstName");
                    this.lastName = personSetCache.getString("lastName");
                    System.out.println("First Name from Cache: "+this.firstName);
                    System.out.println("Last Name from Cache: "+this.lastName);
                }
                else{   
                    System.out.println("person else else");           
                    System.out.println("No rows found in the result person set locally.");
                    //Create temp_message table
                    statement.execute(create_temp_person_table);
                    System.out.println("Temp person table created successfully");
                    String subquery = "SELECT * FROM person WHERE id="+personID+" AND serverId=";
                    //String subquery = "SELECT * FROM person_query5 WHERE id="+personID+" AND serverId=";

                    //Ping other nodes
                    for(int i = 0; i < this.neighbors.size(); ++i) {
                        String person_subquery = subquery + this.neighbors.get(i) + "";
                        //Future<String> future = executor.submit(new gRPC(person_subquery, 2, this.localId, this.neighbors.get(i), this.messageID, this.queryID, this.temp_person_table_name, this.idToChannel));
                        Future<String> future = executor.submit(new gRPC(person_subquery, 2, this.localId, this.neighbors.get(i), randArray[0], this.queryID, this.temp_person_table_name, this.idToChannel));                    
                        person_futures.add(future);
                    }
                }
            }
        }
        catch(SQLException e) {
            e.printStackTrace();
        }

        System.out.println("Got the Person: "+System.currentTimeMillis());

        if(!existsInlocalPerson && !existsInCachePerson){
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

            boolean hasDataInTempPerson=false;
            long id_p=0;
            String gender_p="";
            String birthdayDate_p="";
            String locationIP_p="";
            String browserUsed_p="";
            String LocationCityId_p ="";
            String speaks_p="";
            String email_p="";
            long serverId_p=0;

            Date date = new Date();
            Timestamp timestampP = new Timestamp(date.getTime());
            String currentTimeP = timestampP.toString();

            //Fetch person.firstName and person.lastName from the person temp table
            String fetch_person_name = "SELECT * FROM "+temp_person_table_name;
            try {
                Statement statement = connection.createStatement();
                statement.execute("USE socialnetwork");
                ResultSet nameSet = statement.executeQuery(fetch_person_name);
                if(nameSet.next()) {
                    hasDataInTempPerson = true;
                    this.firstName = nameSet.getString("firstName");
                    this.lastName = nameSet.getString("lastName");
                    id_p = nameSet.getLong("id");
                    gender_p = nameSet.getString("gender");
                    //birthdayDate_p = nameSet.getString("birthday");
                    locationIP_p = nameSet.getString("locationIP");
                    browserUsed_p = nameSet.getString("browserUsed");
                    LocationCityId_p = nameSet.getString("LocationCityId");
                    speaks_p = nameSet.getString("speaks");
                    email_p = nameSet.getString("email");

                    System.out.println("First Name: "+this.firstName);
                    System.out.println("Last Name: "+this.lastName);
                }
                else {
                    System.out.println("Person row NOT found in temp message table");
                }
                if(hasDataInTempPerson){
                    Statement statementInsertPerson = connection.createStatement();
                    System.out.println("No data in cache for person, so insert now to cache");
                    //statementInsertPerson.executeUpdate("Insert into socialnetwork.message_query5 VALUES ('" +currentTime + "', " + id_c + ", '" + language_c + "', '"+ content_c + "', '" + imageFile_c + "', '" + locationIP_c +"', 'Firefox', 108, " + this.creatorPersonID + ", 0, 80, NULL, 0) ");
                    //statementInsertPerson.executeUpdate("Insert into socialnetwork.person_query5 VALUES ('2010-01-03 15:10:31.499+00', 15, 'Hossein', 'Forouhar', 'male', '1984-03-11', '77.245.239.11', 'Firefox', 1166, 'fa;ku;en', 'Hossein14@hotmail.com', 2) ");
                    statementInsertPerson.executeUpdate("Insert into socialnetwork.person_query5 VALUES ('" + currentTimeP + "', " + id_p + ", '" + this.firstName + "', '" + this.lastName + "', '" + gender_p + "', '" + "1984-03-11', '77.245.239.11', 'Firefox', 1166, 'fa;ku;en', 'Hossein14@hotmail.com', 1) ");
                    System.out.println("Inserted into person cache");
                    String selectCountFromPersonCacheTable = "SELECT COUNT(*) FROM socialnetwork.person_query5";
                    String deleteServerId = "DELETE FROM socialnetwork.person_query5 WHERE (id,serverId) = (SELECT id, serverId FROM socialnetwork.person_query5 order by(creationDate) ASC LIMIT 1)";
                    Statement personStatementForCount = connection.createStatement();
                    Statement personStatementForDeletion = connection.createStatement();
                    ResultSet count = personStatementForCount.executeQuery(selectCountFromPersonCacheTable);
                    //ResultSet recordToDelete = statementCacheCheck.executeQuery(selectIdToBeDeleted);
                    if(count.next()){
                        if(count.getInt(1) == maxCachecount){
                            System.out.println(count.getInt(1));
                            System.out.println("Starting deletion from person cache table ");
                            personStatementForDeletion.executeUpdate(deleteServerId);
                            System.out.println("Deletion from person cache complete");
                        }
                        else{
                            System.out.println("Cache Count for Person Table : " + count.getInt(1));
                            System.out.println("Cache is still open for person table");
                        }
                    }
                }
            }
            catch(SQLException e) {
                e.printStackTrace();
                System.out.println("Exception in person:" + e.getMessage());
            }
        }

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
            ShortReadQuery5Response SR_response = client.shortReadQuery5(SR_request);
            String msg = SR_response.getSRQ5Response().getNodeResponse();
            System.out.println("message:" +msg);
            return msg;
        }
    }

    private void bulkInsertMessage(){
	    int M = 20000;
        /*
	    int[] populatedMessageIdArray = new int[M];
	    for(int i = 0; i < M; i++) {
	    	populatedMessageIdArray[i] = (int)(Math.random() * M); // Generates a random double between 0.0 (inclusive) and 1.0 (exclusive)
        }
         */
	    int P = 1000;
        /* 
	    int[] populatedPersonIdArray = new int[P];
	    for(int i = 0; i < P; i++) {
	        populatedPersonIdArray[i] = (int)(Math.random() * P); 
	        
	    }
        */
	    String bulkInsertQuery = "Insert into socialnetwork.message VALUES ";
	    StringBuilder sb = new StringBuilder(bulkInsertQuery);
	    for(int i=10000; i< M; i++){
	    	sb.append(" ('2023-01-03 20:20:36.28+00', ");
	        sb.append(i);
	        sb.append(",'fa', 'About ', NULL, '77.245.239.11', 'Firefox', 108,");
            /*
	        if(i >= P) {
	        	sb.append(populatedPersonIdArray[M/P - 1]);
	        }else {
		        sb.append(populatedPersonIdArray[i]);
	        }
             */
            sb.append(i); //This line is person Id
	        sb.append(", 0, 80, NULL, 2");
	        sb.append("),");
	        

	    }
	    sb.append("");
	    //System.out.println(sb.toString().substring(0, sb.toString().length() - 1) + ";");
        String SQL = sb.toString().substring(0, sb.toString().length() - 1) + ";";
        try{
            Statement statementInsert = connection.createStatement();
            int result = statementInsert.executeUpdate(SQL);
            System.out.println("build insert is compete" +result);
        }catch(Exception e){
            System.out.println("Exception in bulkinsert" +e.getMessage());
        }
	    //int result = statementInsert.executeUpdate("Insert into socialnetwork.message_query5 VALUES ('2023-01-03 20:20:36.28+00', 101, 'fa', 'About Wolfgang Amadeus Mozart, financial security. DuringAbout Thomas Jefferson, al slave trade, and adAbout Hen', NULL, '77.245.239.11', 'Firefox', 108, 14, 0, 80, NULL, 1) ");
        //String bulkInsertPersonQuery
    }

    private void bulkInsertPerson(){
    	int M = 20000;
	    int[] populatedMessageIdArray = new int[M];
	    for(int i = 0; i < M; i++) {
	    	populatedMessageIdArray[i] = (int)(Math.random() * M); // Generates a random double between 0.0 (inclusive) and 1.0 (exclusive)
        }
	
	    int P = 10000;
	    int[] populatedPersonIdArray = new int[P];
	    for(int i = 0; i < P; i++) {
	        populatedPersonIdArray[i] = (int)(Math.random() * P); 
	        
	    }
	
	    //String bulkInsertQuery = "Insert into socialnetwork.message VALUES ";
	    String bulkInsertQuery = "Insert into socialnetwork.person VALUES ";
	    StringBuilder sb = new StringBuilder(bulkInsertQuery);
        //StringBuilder sb = new StringBuilder(bulkInsertQuery);
	    for(int i=10000; i< M; i++){
	    	sb.append("('2010-01-03 15:10:31.499+00', ");
	        sb.append(i);
	        sb.append(", 'Hossein', 'Forouhar");
	        sb.append("-"+i+"'");
	        sb.append(", 'male', '1984-03-11', '77.245.239.11', 'Firefox', 1166, 'fa;ku;en', 'Hossein14@hotmail.com', 2");
	        /*if(i >= P) {
	        	sb.append(populatedPersonIdArray[M/P - 1]);
	        }else {
		        sb.append(populatedPersonIdArray[i]);
	        }*/
	        //sb.append(", 0, 80, NULL, 1");
	        sb.append("),");
	        //sb.append(");");
	        

	    }
	    //sb.append("");
	    //sb.append(";");
	    //System.out.println(sb.toString().substring(0, sb.toString().length() - 1) + ";");
        String SQL = sb.toString().substring(0, sb.toString().length() - 1) + ";";
        /*
	    for(int i=P; i< M; i++){
	    	sb.append("('2010-01-03 15:10:31.499+00', ");
	        sb.append(i);
	        sb.append(", 'Hos', 'F', 'male', '1984-03-11', '77.245.239.11', 'Firefox', 1166, 'fa;ku;en', 'hosf', 2");
	        /*if(i >= P) {
	        	sb.append(populatedPersonIdArray[M/P - 1]);
	        }else {
		        sb.append(populatedPersonIdArray[i]);
	        }
	        //sb.append(", 0, 80, NULL, 1");
	        sb.append("),");
	        //sb.append(");");
	        

	    }
	    //sb.append("");
	    //sb.append(";");
	    //System.out.println(sb.toString().substring(0, sb.toString().length() - 1) + ";");
        String SQL = sb.toString().substring(0, sb.toString().length() - 1) + ";";
        */

	    //String SQL = sb.toString().substring(0, sb.toString().length() - 1) + ";";
        //System.out.println("done");
	    //System.out.println("output:" +SQL);
	    try{
            Statement statementInsert = connection.createStatement();
            int result = statementInsert.executeUpdate(SQL);
            System.out.println("build insert is compete" +result);
        }catch(Exception e){
            System.out.println("Exception in bulkinsertPerson: " +e.getMessage());
        }
	   // System.out.println(sb.toString().substring(0, sb.toString().length() - 1) + ";");
	    //int result = statementInsert.executeUpdate("Insert into socialnetwork.message_query5 VALUES ('2023-01-03 20:20:36.28+00', 101, 'fa', 'About Wolfgang Amadeus Mozart, financial security. DuringAbout Thomas Jefferson, al slave trade, and adAbout Hen', NULL, '77.245.239.11', 'Firefox', 108, 14, 0, 80, NULL, 1) ");
	}

}
