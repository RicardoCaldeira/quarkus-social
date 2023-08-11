package io.github.ricardocaldeira.quarkussocial.rest;

import io.github.ricardocaldeira.quarkussocial.domain.model.Follower;
import io.github.ricardocaldeira.quarkussocial.domain.model.Post;
import io.github.ricardocaldeira.quarkussocial.domain.model.User;
import io.github.ricardocaldeira.quarkussocial.domain.repository.FollowerRepository;
import io.github.ricardocaldeira.quarkussocial.domain.repository.PostRespository;
import io.github.ricardocaldeira.quarkussocial.domain.repository.UserRepository;
import io.github.ricardocaldeira.quarkussocial.rest.dto.CreatePostRequest;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.json.bind.JsonbBuilder;
import jakarta.transaction.Transactional;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@TestHTTPEndpoint(PostResource.class) // metodo alternativo para nao ter q passar a url em todos os testes
class PostResourceTest {

    @Inject
    UserRepository userRepository;

    @Inject
    FollowerRepository followerRepository;

    @Inject
    PostRespository postRespository;

    Long userId;
    Long userNotFollowerId;
    Long userFollowerId;

    @BeforeEach
    @Transactional
    public void setup() {
        // usuario padrão dos testes
        var user = new User();
        user.setAge(30);
        user.setName("Fulano");
        userRepository.persist(user);
        userId = user.getId();

        // criada postagem para o usuario
        Post post = new Post();
        post.setText("Hello");
        post.setUser(user);
        postRespository.persist(post);

        // usuario que não segue ngm
        var userNotFollower = new User();
        userNotFollower.setAge(33);
        userNotFollower.setName("Ciclano");
        userRepository.persist(userNotFollower);
        userNotFollowerId = userNotFollower.getId();

        // usuario seguidor
        var userFollower = new User();
        userFollower.setAge(33);
        userFollower.setName("Ciclano");
        userRepository.persist(userFollower);
        userFollowerId = userFollower.getId();
        Follower follower = new Follower();
        follower.setUser(user);
        follower.setFollower(userFollower);
        followerRepository.persist(follower);
    }

    @Test
    @DisplayName("should create post for an user")
    public void createPostTest() {
        var postRequest = new CreatePostRequest();
        postRequest.setText("Some text");

        given()
                .contentType(ContentType.JSON)
                .body(JsonbBuilder.create().toJson(postRequest))
                .pathParam("userId", userId)
                .when()
                .post()
                .then()
                .statusCode(201);

    }

    @Test
    @DisplayName("should return 404 when trying to create a post from nonexistent user")
    public void postForNonexistentUserTest() {
        var postRequest = new CreatePostRequest();
        postRequest.setText("Some text");

        var nonexistentUserId = 999;

        given()
                .contentType(ContentType.JSON)
                .body(JsonbBuilder.create().toJson(postRequest))
                .pathParam("userId", nonexistentUserId)
                .when()
                .post()
                .then()
                .statusCode(404);

    }

    @Test
    @DisplayName("should return 404 when user does not exist")
    public void listPostUserNotFoundTest() {
        var nonexistentUserId = 999;

        given()
                .pathParam("userId", nonexistentUserId)
                .when()
                .get()
                .then()
                .statusCode(404);
    }

    @Test
    @DisplayName("should return 404 when follower does not exist")
    public void listPostFollowerNotFoundTest() {
        var nonexistentFollowerId = 999;

        given()
                .pathParam("userId", userId)
                .header("followerId", nonexistentFollowerId)
                .when()
                .get()
                .then()
                .statusCode(400)
                .body(Matchers.is("Nonexistent followeId"));
    }

    @Test
    @DisplayName("should return 400 when followerId header is nos present")
    public void listFollowerHeaderNotSendTest() {
        given()
                .pathParam("userId", userId)
                .when()
                .get()
                .then()
                .statusCode(400)
                .body(Matchers.is("You forgot the header followerId"));
    }

    @Test
    @DisplayName("should return 403 when follower is not a follower")
    public void listPostNotAFollowerTest() {
        given()
                .pathParam("userId", userId)
                .header("followerId", userNotFollowerId)
                .when()
                .get()
                .then()
                .statusCode(403)
                .body(Matchers.is("You can't see these posts"));

    }

    @Test
    @DisplayName("should return posts")
    public void listPostsTest() {
        given()
                .pathParam("userId", userId)
                .header("followerId", userFollowerId)
                .when()
                .get()
                .then()
                .statusCode(200)
                .body("size()", Matchers.is(1)); // size() se remete ao tamanho do array retornado na resposta.
                                                         // 1 pq Apenas uma postagem foi criada no SETUP
    }



}