package believe;

import java.util.HashMap;
import java.util.Map;

import believe.dao.DynamoDAO;
import believe.entity.User;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

/**
 * Handler for requests to Lambda function.
 */
public class UserData implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    User user;
    private DynamoDbClient dynamoDb;
    private String DYNAMO_DB_TABLE_NAME = "Users";
    private Region REGION = Region.US_WEST_1;
    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent input, final Context context) {
        LambdaLogger logger = context.getLogger();
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("X-Custom-Header", "application/json");

        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent()
                .withHeaders(headers);

        try {
            user = gson.fromJson(input.getBody(), User.class);
            String output = "{\n" +
                    "  \"status\": \"success\",\n" +
                    "  \"data\": {\n" +
                    "    User Details Updated for /* \"" + user.getEmailId() + ". */\"\n" +
                    "  },\n" +
                    "  \"message\": \"/* Email Added to Subscription List */\"\n" +
                    "}";
            if (user != null) {
//                if (user.getUserName() == null)
//                    user.setUserName("UserName#" + user.getUserName());
                if (user.getMetadata() == null)
                    user.setMetadata("UserData#" + user.getUserName());
                DynamoDAO.getInstance().putItem(user);
            }
            return response
                    .withStatusCode(200)
                    .withBody(output);
        } catch (Exception e) {
            logger.log("Error : " + e.getMessage());
            return response
                    .withBody("{\n" +
                            "  \"status\": \"error\",\n" +
                            "  \"data\": null, /* " + e.getMessage() + " */\n" +
                            "  \"message\": \"Error xyz has occurred\"\n" +
                            "}")
                    .withStatusCode(500);
        }
    }
}
