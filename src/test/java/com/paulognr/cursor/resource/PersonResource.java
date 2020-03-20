package com.paulognr.cursor.resource;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class PersonResource {

    private static final String ROOT_PATH = "cursor/v1/people/";

    public static Response search(int size){
        return search(null, null, size, null);
    }

    public static Response search(int size, String sort){
        return search(null, null, size, sort);
    }

    public static Response searchAfter(String after){
        return search(null, after, 0, null);
    }

    public static Response searchBefore(String before){
        return search(before, null, 0, null);
    }

    private static Response search(String before, String after, int size, String sort){
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
        if (sort != null) {
            request.param("sort", sort);
        }
        return request.when().get(ROOT_PATH + "search").thenReturn();
    }
}
