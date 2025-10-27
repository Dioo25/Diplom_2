package api;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import io.restassured.http.ContentType;

public abstract class BaseClient {

    private static final String BASE_URI = "https://stellarburgers.education-services.ru";
    private static final String BASE_PATH = "/api";

    protected RequestSpecification getSpec() {
        return new RequestSpecBuilder()
                .setBaseUri(BASE_URI)
                .setBasePath(BASE_PATH)
                .setContentType(ContentType.JSON)
                .build();
    }

    // Совместимость: некоторые клиенты вызывают getBaseSpec()
    protected RequestSpecification getBaseSpec() {
        return getSpec();
    }
}