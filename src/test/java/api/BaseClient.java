package api;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;

public abstract class BaseClient {

    protected static final String BASE_URL = "https://stellarburgers.education-services.ru/api";

    protected RequestSpecification getBaseSpec() {
        return new RequestSpecBuilder()
                .setBaseUri(BASE_URL)
                .addFilter(new AllureRestAssured())
                .setContentType("application/json")
                .build();
    }
}
