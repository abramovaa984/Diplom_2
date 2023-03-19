package utils;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

public class UserSettings {
    private static final String URL = "https://stellarburgers.nomoreparties.site";

    protected RequestSpecification getSpec() {
        return new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setBaseUri(URL)
                .build();
    }

    protected RequestSpecification getSpecForDelete() {
        return new RequestSpecBuilder()
                .setBaseUri(URL)
                .build();
    }

    protected RequestSpecification getSpecForGetOrders() {
        return new RequestSpecBuilder()
                .setBaseUri(URL)
                .build();
    }
}