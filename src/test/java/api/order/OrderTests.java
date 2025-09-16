package api.order;

import api.BaseTest;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import org.json.JSONObject;
import org.junit.Test;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@DisplayName("Тесты заказов")
public class OrderTests extends BaseTest {

    private final String SAMPLE_INGREDIENT = "61c0c5a71d1f82001bdaaa6d";

    @Test
    @DisplayName("Создание заказа с авторизацией")
    @Description("Создаём заказ с ингредиентами для авторизованного пользователя")
    public void createOrderWithAuth() {
        JSONObject user = generateUser();
        createUser(user);
        String token = login(user.getString("email"), user.getString("password"));

        createOrder(token, new String[]{SAMPLE_INGREDIENT}, 200, true, null);
    }

    @Test
    @DisplayName("Создание заказа без авторизации")
    @Description("Создаём заказ с ингредиентами без авторизации")
    public void createOrderWithoutAuth() {
        createOrder(null, new String[]{SAMPLE_INGREDIENT}, 200, true, null);
    }

    @Test
    @DisplayName("Создание заказа без ингредиентов")
    @Description("Проверяем ошибку при создании заказа без ингредиентов")
    public void createOrderWithoutIngredients() {
        createOrder(null, new String[]{}, 400, false, "Ingredient ids must be provided");
    }

    @Test
    @DisplayName("Создание заказа с неверным хешем ингредиентов")
    @Description("Проверяем серверную ошибку при неверном хеше ингредиентов")
    public void createOrderWithWrongHash() {
        // Проверяем только статус 500, поле success отсутствует
        createOrder(null, new String[]{"wrong_hash"}, 500, null, null);
    }

    // ------------------- Steps -------------------

    @Step("Генерация уникального пользователя")
    private JSONObject generateUser() {
        return new JSONObject()
                .put("email", "order" + System.currentTimeMillis() + "@mail.com")
                .put("password", "123456")
                .put("name", "OrderUser");
    }

    @Step("Создание пользователя: {user}")
    private void createUser(JSONObject user) {
        given()
                .header("Content-type", "application/json")
                .body(user.toString())
                .when()
                .post("/api/auth/register")
                .then()
                .statusCode(200);
    }

    @Step("Логин пользователя {email}")
    private String login(String email, String password) {
        JSONObject creds = new JSONObject().put("email", email).put("password", password);

        Response response = given()
                .header("Content-type", "application/json")
                .body(creds.toString())
                .when()
                .post("/api/auth/login")
                .then()
                .statusCode(200)
                .extract()
                .response();

        return response.path("accessToken");
    }

    @Step("Создание заказа с токеном: {token} и ингредиентами: {ingredients}")
    private void createOrder(String token, String[] ingredients, int expectedStatus, Boolean expectedSuccess, String expectedMessage) {
        JSONObject order = new JSONObject().put("ingredients", ingredients);

        var request = given()
                .header("Content-type", "application/json")
                .body(order.toString());

        if (token != null) {
            request.header("Authorization", token);
        }

        var response = request
                .when()
                .post("/api/orders")
                .then()
                .statusCode(expectedStatus);

        // Проверяем success только если expectedSuccess != null
        if (expectedSuccess != null) {
            response.body("success", equalTo(expectedSuccess));
        }

        // Проверяем message только если expectedMessage != null
        if (expectedMessage != null) {
            response.body("message", equalTo(expectedMessage));
        }
    }
}
