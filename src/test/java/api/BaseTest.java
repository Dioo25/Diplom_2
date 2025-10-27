package api;

import api.user.User;
import api.user.UserClient;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import net.datafaker.Faker;
import org.junit.Before;

public abstract class BaseTest {

    protected final UserClient userClient = new UserClient();
    protected final Faker faker = new Faker();
    protected User testUser;
    protected String accessToken;

    @Before
    public void setUp() {
        // Устанавливаем базовый URL
        RestAssured.baseURI = "https://stellarburgers.education-services.ru/api";

        // AllureRestAssured для сбора логов в отчет
        RestAssured.filters(new AllureRestAssured());

        //  логирование запросов и ответов при ошибках
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();

        // Генерация случайного пользователя
        testUser = new User(
                faker.internet().emailAddress(),
                "123456",
                faker.name().firstName()
        );

        // Создание пользователя
        var createResponse = userClient.createUser(testUser);

        if (createResponse != null && createResponse.statusCode() == 200) {
            accessToken = createResponse.then().extract().path("accessToken");
        } else {
            // Если пользователь уже существует — пробуем залогиниться
            var loginResponse = userClient.loginUser(testUser);
            if (loginResponse != null && loginResponse.statusCode() == 200) {
                accessToken = loginResponse.then().extract().path("accessToken");
            }
        }
    }
}
