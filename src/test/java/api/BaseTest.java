package api;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import org.junit.Before;

public class BaseTest {

    @Before
    public void setUp() {
        // Базовый URI
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";

        // Фильтр Allure для логов REST Assured
        RestAssured.filters(new AllureRestAssured());
    }
}

