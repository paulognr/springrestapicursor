package com.paulognr.cursor.resource;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class PersonResource {

    private static final String ROOT_PATH = "cursor/v1/people/";

    public static Response search(int size){
        return search(null, null, size);
    }

    public static Response searchAfter(String after){
        return search(null, after, 0);
    }

    public static Response searchBefore(String before){
        return search(before, null, 0);
    }

    private static Response search(String before, String after, int size){
        RequestSpecification request = given();
        if (before != null) {
            request.param("before", before);
        }
        if (after != null) {
            request.param("after", after);
        }
        if (size > 0) {
            request.param("size", size);
        }
        return request.when().get(ROOT_PATH + "search").thenReturn();
    }
}
