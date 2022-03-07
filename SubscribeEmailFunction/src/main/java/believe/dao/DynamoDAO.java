package believe.dao;

import believe.entity.User;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

public class DynamoDAO {
    private static DynamoDAO dao = null;

    private String DYNAMO_DB_TABLE_NAME = "User";
    private static Region REGION = Region.US_WEST_1;

    public DynamoDbEnhancedClient getEnhancedClient() {
        return enhancedClient;
    }

    private  static DynamoDbEnhancedClient enhancedClient;
    private DynamoDAO(){
    }

    public static DynamoDAO getInstance()
    {
        if (dao == null) {
            dao = new DynamoDAO();
            if(enhancedClient==null){
                enhancedClient = dao.initDynamoDbClient(REGION);
            }
        }
        return dao;
    }

/*
    public static void main(String[] args) {
        try{
            DynamoDAO app = new DynamoDAO();
            DynamoDbEnhancedClient enhancedClient  = app.initDynamoDbClient(app.REGION);
//            app.createTable(enhancedClient);

//            app.putTestData(enhancedClient);
//            app.deleteItem(enhancedClient, user);

            User getUser = new User();
            getUser.setUserName("Test");
            User result = app.getItem(enhancedClient, "UserName#Test34", "UserData#Test34");
            System.out.println(result.getEmailId());
        }catch (Exception e){
            e.printStackTrace();
        }
    }
*/

/*
    private void putTestData(DynamoDbEnhancedClient enhancedClient){
        TableSchema<User> USER_TABLE_SCHEMA = TableSchema.fromClass(User.class);
        DynamoDbTable<User> userTable = enhancedClient.table("User", USER_TABLE_SCHEMA);
        User user = new User();
        for (int i = 0; i < 100; i++) {
            user = new User();
            user.setUserName("Test"+i);
            user.setMetadata("UserData#"+"Test"+i);
            user.setFirstName("Coo"+i);
            user.setLastName("VM"+i);
            user.setEmailId("coolvm"+i+"@gmail.com");
            user.setSubscribe(true);
            userTable.putItem(user);
        }
*/
//    }
    public void createTable() throws Exception {
        DynamoDbTable<User> userTable = enhancedClient.table("User",  TableSchema.fromBean(User.class));
        userTable.createTable();
    }

    public void putItem( User newUser) throws Exception {
        DynamoDbTable<User> userTable = enhancedClient.table("User",  TableSchema.fromBean(User.class));
        userTable.putItem(newUser);
    }

    public User getItem(String userName, String metadata) throws Exception {
        DynamoDbTable<User> userTable = enhancedClient.table("User", TableSchema.fromBean(User.class));
        Key key = Key.builder()
                .partitionValue(userName)
                .sortValue(metadata)
                .build();
        return userTable.getItem(key);
    }

    public void deleteItem( User newUser) throws Exception {
        DynamoDbTable<User> userTable = enhancedClient.table("User", TableSchema.fromBean(User.class));
        userTable.deleteItem(newUser);
    }

    private  DynamoDbEnhancedClient initDynamoDbClient(Region region) {
        DynamoDbClient ddb = DynamoDbClient.builder()
//                .endpointOverride(URI.create("http://localhost:8000"))
                .region(region)
//                .credentialsProvider(StaticCredentialsProvider.create(
//                        AwsBasicCredentials.create("v3409", "iphjx")))
                .build();
        DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder()
                .dynamoDbClient(ddb)
                .build();
        return enhancedClient;
    }
}
