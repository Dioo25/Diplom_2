package api.order;

import api.BaseTest;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@DisplayName("Тесты заказов")
public class OrderTests extends BaseTest {

    private final OrderClient orderClient = new OrderClient();
    // валидный ингредиент с публичного стенда (пример из документации)
    private final String VALID_INGREDIENT = "61c0c5a71d1f82001bdaaa6d";

    @Test
    @DisplayName("Создание заказа с авторизацией")
    @Description("Создание заказа с валидным токеном и ингредиентом")
    public void createOrderWithAuth() {
        Order order = new Order(List.of(VALID_INGREDIENT));

        orderClient.createOrder(order, accessToken)
                .then()
                .statusCode(200)
                .and()
                .body("success", equalTo(true))
                .body("order.number", notNullValue());
    }

    @Test
    @DisplayName("Создание заказа без авторизации")
    @Description("Создание заказа без токена должно быть успешным, но без привязки к пользователю")
    public void createOrderWithoutAuth() {
        Order order = new Order(List.of(VALID_INGREDIENT));

        orderClient.createOrder(order, null)
                .then()
                .statusCode(200)
                .and()
                .body("success", equalTo(true));
    }

    @Test
    @DisplayName("Создание заказа без ингредиентов")
    @Description("Создание заказа без ингредиентов должно возвращать 400 и сообщение об ошибке")
    public void createOrderWithoutIngredients() {
        Order order = new Order(List.of());

        orderClient.createOrder(order, accessToken)
                .then()
                .statusCode(400)
                .and()
                .body("success", equalTo(false))
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Создание заказа с неверным хэшем ингредиентов")
    @Description("Создание заказа с несуществующим хэшем возвращает 500")
    public void createOrderWithWrongHash() {
        Order order = new Order(List.of("wrong_hash"));

        orderClient.createOrder(order, accessToken)
                .then()
                .statusCode(500);
    }
}