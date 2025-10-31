package api.user;

import api.BaseTest;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

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

    /**
     * Список заголовков Authorization (в формате "Bearer <token>") для созданных/залогиненных пользователей,
     * которые нужно удалить в @After.
     */
    private final List<String> createdUserAuthHeaders = new ArrayList<>();

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
        Response createExistingResp = userClientLocal.createUser(existingUser);
        // если создан — сохранить токен для удаления, иначе попробовать залогиниться и взять токен
        if (createExistingResp != null && createExistingResp.statusCode() == 200) {
            String raw = createExistingResp.then().extract().path("accessToken");
            if (raw != null && !raw.isBlank()) {
                createdUserAuthHeaders.add(raw.startsWith("Bearer ") ? raw : ("Bearer " + raw));
            }
        } else {
            // возможно пользователь уже существует — попробуем логин и получить токен
            Response loginResp = userClientLocal.loginUser(existingUser);
            if (loginResp != null && loginResp.statusCode() == 200) {
                String raw = loginResp.then().extract().path("accessToken");
                if (raw != null && !raw.isBlank()) {
                    createdUserAuthHeaders.add(raw.startsWith("Bearer ") ? raw : ("Bearer " + raw));
                }
            }
        }

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

    @After
    public void tearDownLocal() {
        // удаляем всех пользователей, которые были созданы или для которых получили токены
        for (String header : createdUserAuthHeaders) {
            try {
                userClientLocal.deleteUser(header);
            } catch (Exception ex) {
                // логируем, но не кидаем — не ломаем выполнение
                System.err.println("Warning: failed to delete user with header " + header + " -> " + ex.getMessage());
            }
        }
        createdUserAuthHeaders.clear();
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

        // Сохраняем токен для удаления в @After
        String raw = response.then().extract().path("accessToken");
        if (raw != null && !raw.isBlank()) {
            createdUserAuthHeaders.add(raw.startsWith("Bearer ") ? raw : ("Bearer " + raw));
        }
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