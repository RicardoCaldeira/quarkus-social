package io.github.ricardocaldeira.quarkussocial.rest;

import io.github.ricardocaldeira.quarkussocial.rest.dto.CreateUserRequest;
import io.github.ricardocaldeira.quarkussocial.rest.dto.ResponseError;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.json.bind.JsonbBuilder;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;

import java.net.URL;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class) // anotação para definir a ordem dos testes
class UserResourceTest {

    @TestHTTPResource("/users")
    URL apiURL;

    @Test
    @DisplayName("Should create an user succe")
    @Order(1)
    public void createUserTest() {
        var user = new CreateUserRequest();
        user.setName("fulano");
        user.setAge(30);

        var response =
                given()
                    .contentType(ContentType.JSON)
                    .body(JsonbBuilder.create().toJson(user))
                .when()
                    .post(apiURL)
                .then().extract().response();

        assertEquals(201, response.statusCode());
        assertNotNull(response.jsonPath().getString("id"));

    }

    @Test
    @DisplayName("Should return error when json is not valid")
    @Order(2)
    public void createUserValidationErrorTest() {
        var user = new CreateUserRequest();

        var response =
                given()
                        .contentType(ContentType.JSON)
                        .body(JsonbBuilder.create().toJson(user))
                .when()
                        .post(apiURL)
                .then()
                        .extract().response();

        assertEquals(ResponseError.UNPROCESSABLE_ENTITY_STATUS, response.statusCode());
        assertEquals("Validation Error", response.jsonPath().getString("message"));

        List<Map<String, String>> errors = response.jsonPath().getList("errors");
        assertNotNull(errors.get(0).get("message"));
        assertNotNull(errors.get(1).get("message"));
    }

    @Test
    @DisplayName("Should list all users")
    @Order(3)
    public void listAllUsersTest() {
        // neste teste, como se trata de uma listagem não precisamos extrair a resposta após o then()
        // como nos testes acima. Com isso podemos fazer verificações mais diretamente
        given()
                .contentType(ContentType.JSON)
        .when()
                .get(apiURL)
        .then()
                .statusCode(200)
                .body("size()", Matchers.is(1));

    }

}