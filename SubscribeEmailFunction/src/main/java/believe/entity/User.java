package believe.entity;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

@DynamoDbBean
    public class User {
    private String userName;
    private String metadata;
    private  String firstName;
    private String lastName;
    private String emailId;
    private boolean subscribe;

    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }


    public String getEmailId() {
        return emailId;
    }
    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }
    public boolean isSubscribe() {
        return subscribe;
    }
    public void setSubscribe(boolean subscribe) {
        this.subscribe = subscribe;
    }

    @DynamoDbPartitionKey
    public String getUserName() {
          return userName;
      }
    public void setUserName(String userName) {
        this.userName = "UserName#"+userName;
    }
    @DynamoDbSortKey
    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }
}
