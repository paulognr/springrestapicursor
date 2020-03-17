package com.paulognr.cursor.contract;

import com.paulognr.cursor.Application;
import com.paulognr.cursor.extension.HttpConfigExtension;
import com.paulognr.cursor.resource.PersonResource;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;

@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ExtendWith(HttpConfigExtension.class)
public class PersonContractTest {

    @Test
    public void testAllResultsSearch() {
        PersonResource.search(8).then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("data", hasSize(7))
                .body("cursor.hasAfter", is(false))
                .body("cursor.after", is(emptyOrNullString()))
                .body("cursor.hasBefore", is(false))
                .body("cursor.before", is(emptyOrNullString()));
    }

    @Test
    public void testNavigation2Pages() {
        String after = PersonResource.search(6).then()
                .assertThat()
                    .statusCode(HttpStatus.SC_OK)
                    .body("data", hasSize(6))
                    .body("cursor.hasBefore", is(false))
                    .body("cursor.before", is(emptyOrNullString()))
                    .body("cursor.hasAfter", is(true))
                    .body("cursor.after", is(notNullValue()))
                .extract()
                    .body().jsonPath().getString("cursor.after");

        String before = PersonResource.searchAfter(after).then()
                .assertThat()
                    .statusCode(HttpStatus.SC_OK)
                    .body("data", hasSize(1))
                    .body("cursor.hasAfter", is(false))
                    .body("cursor.after", is(emptyOrNullString()))
                    .body("cursor.hasBefore", is(true))
                    .body("cursor.before", is(notNullValue()))
                .extract()
                    .body().jsonPath().getString("cursor.before");

        PersonResource.searchBefore(before).then()
                .assertThat()
                    .statusCode(HttpStatus.SC_OK)
                    .body("data", hasSize(6))
                    .body("cursor.hasBefore", is(false))
                    .body("cursor.before", is(emptyOrNullString()))
                    .body("cursor.hasAfter", is(true))
                    .body("cursor.after", is(notNullValue()));
    }

}
