package api;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

public class BaseClient {

    protected RequestSpecification getBaseSpec() {
        return new RequestSpecBuilder()
                .setBaseUri("https://stellarburgers.education-services.ru/api")
                .setContentType(ContentType.JSON)
                .build();
    }
}