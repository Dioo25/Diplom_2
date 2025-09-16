package api.user;

import api.BaseTest;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import org.json.JSONObject;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@DisplayName("Тесты логина пользователя")
public class LoginTests extends BaseTest {

    @Test
    @DisplayName("Вход под существующим пользователем")
    @Description("Регистрируем пользователя и проверяем успешный вход")
    public void loginUser() {
        JSONObject user = generateUser();
        createUser(user);

        login(user.getString("email"), user.getString("password"), 200, true, null);
    }

    @Test
    @DisplayName("Вход с неверным логином/паролем")
    @Description("Проверяем ошибку входа при неверных данных")
    public void loginWrongCredentials() {
        JSONObject creds = new JSONObject()
                .put("email", "nonexistent" + System.currentTimeMillis() + "@mail.com")
                .put("password", "badpass");

        login(creds.getString("email"), creds.getString("password"), 401, false, "email or password are incorrect");
    }

    @Step("Генерация уникального пользователя")
    private JSONObject generateUser() {
        return new JSONObject()
                .put("email", "login" + System.currentTimeMillis() + "@mail.com")
                .put("password", "123456")
                .put("name", "LoginUser");
    }

    @Step("Создание пользователя: {user}")
    private void createUser(JSONObject user) {
        given()
                .header("Content-type", "application/json")
                .body(user.toString())
                .when()
                .post("/api/auth/register")
                .then()
                .statusCode(200)
                .body("success", equalTo(true));
    }

    @Step("Вход пользователя {email}")
    private void login(String email, String password, int expectedStatus, boolean expectedSuccess, String expectedMessage) {
        JSONObject creds = new JSONObject().put("email", email).put("password", password);

        var request = given()
                .header("Content-type", "application/json")
                .body(creds.toString())
                .when()
                .post("/api/auth/login")
                .then()
                .statusCode(expectedStatus)
                .body("success", equalTo(expectedSuccess));

        if (expectedMessage != null) {
            request.body("message", equalTo(expectedMessage));
        }
    }
}
