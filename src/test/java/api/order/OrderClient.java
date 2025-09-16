package api.order;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class OrderClient {
    private static final String BASE_URL = "https://stellarburgers.nomoreparties.site/api";

    public Response createOrder(Order order, String token) {
        if (token != null) {
            return RestAssured.given()
                    .header("Content-type", "application/json")
                    .header("Authorization", token)
                    .body(order)
                    .post(BASE_URL + "/orders");
        } else {
            return RestAssured.given()
                    .header("Content-type", "application/json")
                    .body(order)
                    .post(BASE_URL + "/orders");
        }
    }
}