package api;

import api.user.UserClient;
import api.user.User;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import net.datafaker.Faker;
import org.junit.After;
import org.junit.Before;

public abstract class BaseTest {

    protected final UserClient userClient = new UserClient();
    protected final Faker faker = new Faker();
    protected User testUser;
    protected String accessToken;

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.education-services.ru";
        RestAssured.filters(new AllureRestAssured());

        testUser = new User(
                faker.internet().emailAddress(),
                "123456",
                faker.name().firstName()
        );

        var createResponse = userClient.createUser(testUser);
        if (createResponse != null && createResponse.statusCode() == 200) {
            accessToken = createResponse.then().extract().path("accessToken");
        } else {
            var loginResponse = userClient.loginUser(testUser);
            if (loginResponse != null && loginResponse.statusCode() == 200) {
                accessToken = loginResponse.then().extract().path("accessToken");
            }
        }
    }

    @After
    public void tearDown() {
        if (accessToken != null && !accessToken.isBlank()) {
            String headerToken = accessToken.startsWith("Bearer ") ? accessToken : ("Bearer " + accessToken);
            try {
                userClient.deleteUser(headerToken);
            } catch (Exception ignore) {}
        }
    }
}
