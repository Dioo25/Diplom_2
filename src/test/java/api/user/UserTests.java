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

    private UserClient userClient;
    private User uniqueUser;
    private User existingUser;
    private User userWithoutPassword;

    @Before
    @Step("Подготовка данных пользователей перед тестами")
    public void setUp() {
        userClient = new UserClient();

        // Уникальный пользователь (email меняется при каждом запуске)
        uniqueUser = new User(
                faker.internet().emailAddress(),
                faker.internet().password(),
                faker.name().firstName()
        );

        // Пользователь, которого зарегистрируем для проверки дубликата
        existingUser = new User(
                "existing" + System.currentTimeMillis() + "@mail.com",
                "password123",
                "John"
        );
        userClient.createUser(existingUser);

        // Пользователь без пароля (для негативного сценария)
        userWithoutPassword = new User(
                faker.internet().emailAddress(),
                "",
                faker.name().firstName()
        );
    }

    @Test
    @DisplayName("Создание уникального пользователя")
    @Description("Проверяем успешную регистрацию нового пользователя")
    public void createUniqueUser() {
        Response response = userClient.createUser(uniqueUser);

        response.then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("accessToken", notNullValue());
    }

    @Test
    @DisplayName("Создание уже зарегистрированного пользователя")
    @Description("Проверяем ошибку при попытке повторной регистрации того же пользователя")
    public void createExistingUser() {
        Response response = userClient.createUser(existingUser);

        response.then()
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("User already exists"));
    }

    @Test
    @DisplayName("Создание пользователя без обязательного поля (пароля)")
    @Description("Проверяем ошибку при регистрации без пароля")
    public void createUserWithoutPassword() {
        Response response = userClient.createUser(userWithoutPassword);

        response.then()
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }
}