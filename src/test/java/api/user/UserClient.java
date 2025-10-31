package api.user;

import api.BaseClient;
import io.qameta.allure.Step;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class UserClient extends BaseClient {

    @Step("Создание пользователя через API: {user.email}")
    public Response createUser(User user) {
        return given()
                .spec(getBaseSpec())
                .body(user)
                .when()
                .post("/auth/register");
    }

    @Step("Авторизация пользователя через API: {user.email}")
    public Response loginUser(User user) {
        return given()
                .spec(getBaseSpec())
                .body(new LoginRequest(user.getEmail(), user.getPassword()))
                .when()
                .post("/auth/login");
    }

    @Step("Удаление пользователя через API по токену")
    public Response deleteUser(String token) {
        return given()
                .spec(getBaseSpec())
                .header("Authorization", token)
                .when()
                .delete("/auth/user");
    }

    private static class LoginRequest {
        public String email;
        public String password;
        public LoginRequest(String email, String password) {
            this.email = email; this.password = password;
        }
    }
}