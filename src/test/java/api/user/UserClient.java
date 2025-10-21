package api.user;

import api.BaseClient;
import io.qameta.allure.Step;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class UserClient extends BaseClient {

    @Step("Создание пользователя")
    public Response createUser(User user) {
        return given()
                .spec(getBaseSpec())
                .body(user)
                .when()
                .post("/auth/register");
    }

    @Step("Авторизация пользователя")
    public Response loginUser(User user) {
        return given()
                .spec(getBaseSpec())
                .body(user)
                .when()
                .post("/auth/login");
    }

    @Step("Удаление пользователя")
    public void deleteUser(String token) {
        given()
                .spec(getBaseSpec())
                .header("Authorization", token)
                .when()
                .delete("/auth/user");
    }
}