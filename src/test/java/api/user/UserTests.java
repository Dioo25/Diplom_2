package api.user;

import api.BaseTest;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@DisplayName("Тесты регистрации пользователей")
public class UserTests extends BaseTest {

    private UserClient userClientLocal;
    private User uniqueUser;
    private User existingUser;
    private User userWithoutPassword;
    private User userWithoutEmail;
    private User userWithoutName;

    @Before
    @Step("Подготовка данных пользователей перед тестами")
    public void setUp() {
        userClientLocal = new UserClient();

        uniqueUser = new User(
                faker.internet().emailAddress(),
                faker.internet().password(),
                faker.name().firstName()
        );

        existingUser = new User(
                "existing" + System.currentTimeMillis() + "@mail.com",
                "password123",
                "John"
        );
        // создать существующего пользователя (попытка)
        userClientLocal.createUser(existingUser);

        userWithoutPassword = new User(
                faker.internet().emailAddress(),
                "",
                faker.name().firstName()
        );

        userWithoutEmail = new User(
                "",
                faker.internet().password(),
                faker.name().firstName()
        );

        userWithoutName = new User(
                faker.internet().emailAddress(),
                faker.internet().password(),
                ""
        );
    }

    @Test
    @DisplayName("Создание уникального пользователя")
    @Description("Проверяем успешную регистрацию нового пользователя")
    public void createUniqueUser() {
        Response response = userClientLocal.createUser(uniqueUser);

        response.then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("accessToken", notNullValue());
    }

    @Test
    @DisplayName("Создание уже зарегистрированного пользователя")
    @Description("Проверяем ошибку при повторной регистрации")
    public void createExistingUser() {
        Response response = userClientLocal.createUser(existingUser);

        response.then()
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("User already exists"));
    }

    @Test
    @DisplayName("Создание пользователя без обязательного поля (пароля)")
    @Description("Ошибка при регистрации без пароля")
    public void createUserWithoutPassword() {
        Response response = userClientLocal.createUser(userWithoutPassword);

        response.then()
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Создание пользователя без обязательного поля (email)")
    @Description("Ошибка при регистрации без email")
    public void createUserWithoutEmail() {
        Response response = userClientLocal.createUser(userWithoutEmail);

        response.then()
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Создание пользователя без обязательного поля (имени)")
    @Description("Ошибка при регистрации без имени")
    public void createUserWithoutName() {
        Response response = userClientLocal.createUser(userWithoutName);

        response.then()
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }
}