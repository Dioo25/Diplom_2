package api.user;

import api.BaseTest;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import org.json.JSONObject;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@DisplayName("Тесты пользователей")
public class UserTests extends BaseTest {

    @Test
    @DisplayName("Создание уникального пользователя")
    @Description("Создаём уникального пользователя и проверяем успешный ответ")
    public void createUniqueUser() {
        JSONObject user = generateUser();
        createUser(user, 200, true, null);
    }

    @Test
    @DisplayName("Создание пользователя, который уже зарегистрирован")
    @Description("Проверяем, что повторная регистрация одного пользователя выдаёт ошибку")
    public void createExistingUser() {
        JSONObject user = generateUser();
        createUser(user, 200, true, null); // первый раз

        createUser(user, 403, false, "User already exists"); // второй раз
    }

    @Test
    @DisplayName("Создание пользователя без обязательного поля (без пароля)")
    @Description("Проверяем ошибку при отсутствии обязательного поля password")
    public void createUserWithoutPassword() {
        JSONObject user = new JSONObject()
                .put("email", "test" + System.currentTimeMillis() + "@mail.com")
                .put("name", "NoPass");

        createUser(user, 403, false, "Email, password and name are required fields");
    }

    @Step("Генерация уникального пользователя")
    private JSONObject generateUser() {
        return new JSONObject()
                .put("email", "user" + System.currentTimeMillis() + "@mail.com")
                .put("password", "123456")
                .put("name", "Test User");
    }

    @Step("Создание пользователя: {user}")
    private void createUser(JSONObject user, int expectedStatus, boolean expectedSuccess, String expectedMessage) {
        var request = given()
                .header("Content-type", "application/json")
                .body(user.toString())
                .when()
                .post("/api/auth/register")
                .then()
                .statusCode(expectedStatus)
                .body("success", equalTo(expectedSuccess));

        if (expectedMessage != null) {
            request.body("message", equalTo(expectedMessage));
        }
    }
}
