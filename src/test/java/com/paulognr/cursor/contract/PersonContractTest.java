package com.paulognr.cursor.contract;

import java.util.List;
import com.paulognr.cursor.Application;
import com.paulognr.cursor.extension.HttpConfigExtension;
import com.paulognr.cursor.resource.PersonResource;
import com.paulognr.cursor.service.PersonDTO;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
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
    public void testNavigationTwoPages() {
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

    @Test
    public void testNavigationThreePages() {
        String after = PersonResource.search(3).then()
                .assertThat()
                    .statusCode(HttpStatus.SC_OK)
                    .body("data", hasSize(3))
                    .body("cursor.hasBefore", is(false))
                    .body("cursor.before", is(emptyOrNullString()))
                    .body("cursor.hasAfter", is(true))
                    .body("cursor.after", is(notNullValue()))
                .extract()
                    .body().jsonPath().getString("cursor.after");

        after = PersonResource.searchAfter(after).then()
                .assertThat()
                    .statusCode(HttpStatus.SC_OK)
                    .body("data", hasSize(3))
                    .body("cursor.hasAfter", is(true))
                    .body("cursor.after", is(notNullValue()))
                    .body("cursor.hasBefore", is(true))
                    .body("cursor.before", is(notNullValue()))
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

        before = PersonResource.searchBefore(before).then()
                .assertThat()
                    .statusCode(HttpStatus.SC_OK)
                    .body("data", hasSize(3))
                    .body("cursor.hasAfter", is(true))
                    .body("cursor.after", is(notNullValue()))
                    .body("cursor.hasBefore", is(true))
                    .body("cursor.before", is(notNullValue()))
                .extract()
                    .body().jsonPath().getString("cursor.before");

        PersonResource.searchBefore(before).then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("data", hasSize(3))
                .body("cursor.hasBefore", is(false))
                .body("cursor.before", is(emptyOrNullString()))
                .body("cursor.hasAfter", is(true))
                .body("cursor.after", is(notNullValue()));
    }

    @Test
    public void testItemPositionThreePages() {
        List<PersonDTO> peopleOrderedById = extractPeople(PersonResource.search(7).then().extract());

        // first page 1 - 3 / 7
        ExtractableResponse<Response> extractableResponse = PersonResource.search(3).then().extract();
        String after = extractAfter(extractableResponse);
        List<PersonDTO> responsePage = extractPeople(extractableResponse);

        assertThat(responsePage, hasSize(3));
        for(int i = 0; i < responsePage.size(); i++) {
            assertThat(responsePage.get(i).getId(), is(equalTo(peopleOrderedById.get(i).getId())));
        }

        // second page 4 - 6 / 7
        extractableResponse = PersonResource.searchAfter(after).then().extract();
        after = extractAfter(extractableResponse);
        responsePage = extractPeople(extractableResponse);

        assertThat(responsePage, hasSize(3));
        for(int i = 0; i < responsePage.size(); i++) {
            assertThat(responsePage.get(i).getId(), is(equalTo(peopleOrderedById.get(i + 3).getId())));
        }

        // last page 7 / 7
        extractableResponse = PersonResource.searchAfter(after).then().extract();
        String before = extractBefore(extractableResponse);
        responsePage = extractPeople(extractableResponse);

        assertThat(responsePage, hasSize(1));
        assertThat(responsePage.get(0).getId(), is(equalTo(peopleOrderedById.get(6).getId())));

        // second page 4 - 6 / 7
        extractableResponse = PersonResource.searchBefore(before).then().extract();
        before = extractBefore(extractableResponse);
        responsePage = extractPeople(extractableResponse);

        assertThat(responsePage, hasSize(3));
        for(int i = 0; i < responsePage.size(); i++) {
            assertThat(responsePage.get(i).getId(), is(equalTo(peopleOrderedById.get(i + 3).getId())));
        }

        // first page 1 - 3 / 7
        responsePage = extractPeople(PersonResource.searchBefore(before).then().extract());

        assertThat(responsePage, hasSize(3));
        for(int i = 0; i < responsePage.size(); i++) {
            assertThat(responsePage.get(i).getId(), is(equalTo(peopleOrderedById.get(i).getId())));
        }
    }

    @Test
    public void testItemPositionTwoPages() {
        List<PersonDTO> peopleOrderedById = extractPeople(PersonResource.search(7).then().extract());

        ExtractableResponse<Response> extractableResponse = PersonResource.search(6).then().extract();
        String after = extractAfter(extractableResponse);
        List<PersonDTO> firstPage = extractPeople(extractableResponse);

        assertThat(firstPage, hasSize(6));
        for(int i = 0; i < firstPage.size(); i++) {
            assertThat(firstPage.get(i).getId(), is(equalTo(peopleOrderedById.get(i).getId())));
        }

        extractableResponse = PersonResource.searchAfter(after).then().extract();
        String before = extractBefore(extractableResponse);
        List<PersonDTO> lastPage = extractPeople(extractableResponse);

        assertThat(lastPage, hasSize(1));
        assertThat(lastPage.get(0).getId(), is(equalTo(peopleOrderedById.get(6).getId())));

        firstPage = extractPeople(PersonResource.searchBefore(before).then().extract());

        assertThat(firstPage, hasSize(6));
        for(int i = 0; i < firstPage.size(); i++) {
            assertThat(firstPage.get(i).getId(), is(equalTo(peopleOrderedById.get(i).getId())));
        }
    }

    @Test
    public void testSortByIdDesc() {
        List<PersonDTO> peopleOrderedById = extractPeople(PersonResource.search(7).then().extract());
        List<PersonDTO> peopleOrderedByIdDesc = extractPeople(PersonResource.search(7, "-id").then().extract());

        assertThat(peopleOrderedByIdDesc, hasSize(7));
        for(int i = 0; i < peopleOrderedByIdDesc.size(); i++) {
            assertThat(peopleOrderedByIdDesc.get(i).getId(), is(equalTo(peopleOrderedById.get(peopleOrderedById.size() - (i + 1)).getId())));
        }
    }

    @Test
    public void testSortByIdDescTwoPages() {
        List<PersonDTO> peopleOrderedById = extractPeople(PersonResource.search(7).then().extract());

        ExtractableResponse<Response> extractableResponse = PersonResource.search(6, "-id").then().extract();
        String after = extractAfter(extractableResponse);
        List<PersonDTO> firstPage = extractPeople(extractableResponse);

        assertThat(firstPage, hasSize(6));
        for(int i = 0; i < firstPage.size(); i++) {
            assertThat(firstPage.get(i).getId(), is(equalTo(peopleOrderedById.get(peopleOrderedById.size() - (i + 1)).getId())));
        }

        extractableResponse = PersonResource.searchAfter(after).then().extract();
        String before = extractBefore(extractableResponse);
        List<PersonDTO> lastPage = extractPeople(extractableResponse);

        assertThat(lastPage, hasSize(1));
        assertThat(lastPage.get(0).getId(), is(equalTo(peopleOrderedById.get(0).getId())));

        firstPage = extractPeople(PersonResource.searchBefore(before).then().extract());

        assertThat(firstPage, hasSize(6));
        for(int i = 0; i < firstPage.size(); i++) {
            assertThat(firstPage.get(i).getId(), is(equalTo(peopleOrderedById.get(peopleOrderedById.size() - (i + 1)).getId())));
        }
    }

    private List<PersonDTO> extractPeople(ExtractableResponse<Response> extractableResponse) {
        return extractableResponse.body().jsonPath().getList("data", PersonDTO.class);
    }

    private String extractAfter(ExtractableResponse<Response> extractableResponse) {
        return extractableResponse.body().jsonPath().getString("cursor.after");
    }

    private String extractBefore(ExtractableResponse<Response> extractableResponse) {
        return extractableResponse.body().jsonPath().getString("cursor.before");
    }

}
