package api.user;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class UserClient {
    private static final String BASE_URL = "https://stellarburgers.nomoreparties.site/api";

    public Response create(User user) {
        return RestAssured.given()
                .header("Content-type", "application/json")
                .body(user)
                .post(BASE_URL + "/auth/register");
    }

    public Response login(Credentials creds) {
        return RestAssured.given()
                .header("Content-type", "application/json")
                .body(creds)
                .post(BASE_URL + "/auth/login");
    }
}
