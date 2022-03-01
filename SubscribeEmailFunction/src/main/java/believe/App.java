package believe;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import believe.entity.User;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.PutItemSpec;
import com.amazonaws.services.dynamodbv2.model.ConditionalCheckFailedException;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Handler for requests to Lambda function.
 */
public class App implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    User user;
    private DynamoDB dynamoDb;
    private String DYNAMO_DB_TABLE_NAME = "Users";
    private Regions REGION = Regions.US_WEST_1;

    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent input, final Context context) {
        LambdaLogger logger = context.getLogger();
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("X-Custom-Header", "application/json");

        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent()
                .withHeaders(headers);
        try {
            String output = "{ Success }";
            initDynamoDbClient();
            user = gson.fromJson(input.getBody(), User.class);
            if (user != null) {
                persistData(user);
            }
            return response
                    .withStatusCode(200)
                    .withBody(output);
        } catch (Exception e) {
            logger.log("Error : " + e.getMessage());
            return response
                    .withBody("{error  "+e.getMessage()+"}")
                    .withStatusCode(500);
        }
    }

    private PutItemOutcome persistData(User user)
            throws ConditionalCheckFailedException {
        Table table = this.dynamoDb.getTable(DYNAMO_DB_TABLE_NAME);
        Item item = new Item().withPrimaryKey("emailId", user.getEmailId())
                .withString("firstName",user.getFirstName())
                .withString("lastName", user.getLastName())
                .withBoolean("subscribe", user.getSubscribe());
        PutItemOutcome outcome = table.putItem(item);
        return outcome;
    }

    private String getPageContents(String address) throws IOException{
        URL url = new URL(address);
        try(BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()))) {
            return br.lines().collect(Collectors.joining(System.lineSeparator()));
        }
    }

    private void initDynamoDbClient() {
        AmazonDynamoDBClient client = new AmazonDynamoDBClient();
        client.setRegion(Region.getRegion(REGION));
        this.dynamoDb = new DynamoDB(client);
    }
}
