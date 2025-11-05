package api;

import api.user.User;
import api.user.UserClient;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import net.datafaker.Faker;
import org.junit.After;
import org.junit.Before;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


public abstract class BaseTest {

    protected final UserClient userClient = new UserClient();
    protected final Faker faker = new Faker();
    protected User testUser;
    protected String accessToken;

    @Before
    public void setUp() {
        // Путь для Allure — должен совпадать с тем, что указан в POM
        String allureResultsDir = "target/site/allure-results";
        System.setProperty("allure.results.directory", allureResultsDir);

        // Создаю папку заранее, чтобы адаптеры могли туда писать
        try {
            Path resultsDir = Paths.get(allureResultsDir);
            if (Files.notExists(resultsDir)) {
                Files.createDirectories(resultsDir);
                System.out.println("✅ Создана папка для Allure: " + resultsDir.toAbsolutePath());
            }
        } catch (Exception e) {
            System.err.println("Не удалось создать папку Allure: " + e.getMessage());
        }

        // RestAssured + Allure фильтр
        RestAssured.baseURI = "https://stellarburgers.education-services.ru/api";
        RestAssured.filters(new AllureRestAssured());
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();

        // Создаю тестового пользователя
        testUser = new User(
                faker.internet().emailAddress(),
                "123456",
                faker.name().firstName()
        );

        var createResponse = userClient.createUser(testUser);
        if (createResponse != null && createResponse.statusCode() == 200) {
            accessToken = createResponse.then().extract().path("accessToken");
        } else {
            var loginResponse = userClient.loginUser(testUser);
            if (loginResponse != null && loginResponse.statusCode() == 200) {
                accessToken = loginResponse.then().extract().path("accessToken");
            }
        }
    }

    @After
    public void tearDown() {
        if (accessToken != null && !accessToken.isEmpty()) {
            try {
                userClient.deleteUser(accessToken);
            } catch (Exception e) {
                System.err.println("Ошибка удаления пользователя в @After: " + e.getMessage());
            } finally {
                accessToken = null;
            }
        }
    }
}
