package api;

import api.user.User;
import api.user.UserClient;
import io.restassured.RestAssured;
import net.datafaker.Faker;
import org.junit.Before;

public abstract class BaseTest {

    protected final UserClient userClient = new UserClient();
    protected final Faker faker = new Faker();
    protected User testUser;
    protected String accessToken;

    @Before
    public void setUp() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();

        testUser = new User(
                faker.internet().emailAddress(),
                "123456",
                faker.name().firstName()
        );

        var createResponse = userClient.createUser(testUser);
        if (createResponse.statusCode() == 200) {
            accessToken = createResponse.then().extract().path("accessToken");
        }
    }
}

