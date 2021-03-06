package org.melek.tddwithspringframework.integration;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.melek.tddwithspringframework.util.MyTestProfileResolver;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS) //used for non-static before method
@ActiveProfiles(resolver = MyTestProfileResolver.class) //required application-test.properties file
@Sql(scripts = "/sql-files/test-data.sql" ,executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql-files/clear-test-data.sql" ,executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class BooksRestAssuredIT {
    private final static String BASE_URI = "http://localhost";

    @LocalServerPort
    private int port;

    private Response response;

    @BeforeAll
    void configureRestAssured() {
        RestAssured.baseURI = BASE_URI;
        RestAssured.port = port;
    }

    @Test
    void whenGetBooks_shouldReturnAllBooks() throws InterruptedException {
        response = when().get("/books");
        response.then().assertThat()
                .statusCode(equalTo(200))
                .body("size()", is(2))
                .body("name[0]", equalTo("Clean Code"))
                .body("name[1]", equalTo("Continuous Delivery"));
    }

    @Test
    void whenGet_booksWithName_shouldReturnGivenBook() throws Exception {
        response =given().params("bookName","Clean Code").when().get("/books/name");
        response.then().assertThat()
                .statusCode(equalTo(200))
                .body("name", equalTo("Clean Code"));
    }

}
