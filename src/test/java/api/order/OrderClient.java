package api.order;

import api.BaseClient;
import io.qameta.allure.Step;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class OrderClient extends BaseClient {

    @Step("Создание заказа")
    public Response createOrder(Order order, String token) {
        var request = given()
                .spec(getBaseSpec())
                .body(order);

        if (token != null) {
            request.header("Authorization", token);
        }

        return request.post("/orders");
    }
}