package com.paulognr.cursor.extension;

import java.nio.charset.StandardCharsets;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class HttpConfigExtension implements BeforeAllCallback {

    public static String BASE_URL = "http://localhost";
    public static int PORT = 8090;

    public HttpConfigExtension() {
        super();
    }

    public HttpConfigExtension(String baseUrl, int port) {
        BASE_URL = baseUrl;
        PORT = port;
    }

    @Override
    public void beforeAll(ExtensionContext extensionContext) {
        RestAssured.baseURI = BASE_URL;
        RestAssured.port = PORT;
        RestAssured.requestSpecification = new RequestSpecBuilder().build();
        RestAssured.requestSpecification.contentType(ContentType.JSON.withCharset(StandardCharsets.UTF_8));
    }
}