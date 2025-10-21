package api.user;

import api.BaseTest;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;

@DisplayName("Тесты логина пользователя")
public class LoginTests extends BaseTest {

    @Test
    @DisplayName("Вход под существующим пользователем")
    @Description("Проверяем успешный логин зарегистрированного пользователя")
    public void loginExistingUser() {
        userClient.loginUser(testUser)
                .then()
                .statusCode(200)
                .and()
                .body("success", equalTo(true));
    }

    @Test
    @DisplayName("Вход с неверным паролем")
    @Description("Проверяем ошибку при входе с неверным паролем")
    public void loginWrongPassword() {
        User wrongPasswordUser = new User(testUser.getEmail(), "wrongpass", testUser.getName());
        userClient.loginUser(wrongPasswordUser)
                .then()
                .statusCode(401)
                .and()
                .body("success", equalTo(false));
    }
}
