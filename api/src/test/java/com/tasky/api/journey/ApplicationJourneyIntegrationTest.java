package com.tasky.api.journey;

import com.tasky.api.AbstractTestContainer;
import com.tasky.api.dto.comment.CreateCommentRequest;
import com.tasky.api.dto.comment.UpdateCommentRequest;
import com.tasky.api.dto.feature.CreateFeatureRequest;
import com.tasky.api.dto.feature.UpdateFeatureRequest;
import com.tasky.api.dto.project.AddUserToProjectRequest;
import com.tasky.api.dto.project.CreateProjectRequest;
import com.tasky.api.dto.project.UpdateProjectRequest;
import com.tasky.api.dto.run.CreateRunRequest;
import com.tasky.api.dto.run.UpdateRunRequest;
import com.tasky.api.dto.toDo.CreateToDoRequest;
import com.tasky.api.dto.toDo.UpdateTodoRequest;
import com.tasky.api.dto.user.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for the admin journey, focusing on authentication and login functionality.
 * These tests are performed within the context of a Spring Boot application.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ApplicationJourneyIntegrationTest extends AbstractTestContainer {

    @Autowired
    private WebTestClient webTestClient;
    private static final String LOGIN_URI = "/api/v1/user/auth";

    @Value("#{'${server.default-admin-password}'}")
    private String adminPassword;
    @Value("#{'${server.default-admin-account}'}")
    private String adminAccount;

    private static String token;

    /**
     * Tests the login journey for admin users.
     * This method sends a user authentication request, verifies that it is unauthorized,
     * and then sends an admin authentication request to obtain an authorization token.
     * The obtained token is checked to ensure it is not empty.
     */

    @Test
    void LoginJourney() {
        String first_name = FAKER.name().firstName();
        String last_name = FAKER.name().lastName();
        String email = last_name+"."+first_name+ UUID.randomUUID()+"@gmail.com";
        UserAuthenticationRequest request = new UserAuthenticationRequest(email,"aeraerazer");

        webTestClient.post()
                .uri(LOGIN_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(request),UserAuthenticationRequest.class)
                .exchange()
                .expectStatus().isUnauthorized();

        UserAuthenticationRequest adminRegistrationRequest = new UserAuthenticationRequest(adminAccount,adminPassword);

        token = Objects.requireNonNull(webTestClient.post()
                        .uri(LOGIN_URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .body(Mono.just(adminRegistrationRequest), UserAuthenticationRequest.class)
                        .exchange()
                        .expectStatus()
                        .isOk()
                        .returnResult(Void.class)
                        .getResponseHeaders()
                        .get(HttpHeaders.AUTHORIZATION))
                .get(0);
        assertFalse(token.isEmpty());

        UserRegistrationRequest createNewUser = new UserRegistrationRequest("testus","testus","testus@tasky.test");

        UserRegistrationResponse userRegistrationResponse = webTestClient.post()
                .uri("/api/v1/user")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION,String.format("Bearer %s", token))
                .body(Mono.just(createNewUser), UserRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(UserRegistrationResponse.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(userRegistrationResponse);
        Long id = userRegistrationResponse.userDto().id();

        assertNotNull(id);

        webTestClient.get()
                .uri("/api/v1/user/profile")
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION,String.format("Bearer %s", token))
                .exchange()
                .expectStatus()
                .isOk();

        SearchUsersResponse response = webTestClient
                .get()
                .uri("/api/v1/user")
                .header(HttpHeaders.AUTHORIZATION,String.format("Bearer %s", token))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(SearchUsersResponse.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(response);

        UserDto userDto = webTestClient
                .get()
                .uri("/api/v1/user/"+id)
                .header(HttpHeaders.AUTHORIZATION,String.format("Bearer %s", token))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(UserDto.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(userDto);

        UpdateUserRequest updateUserRequest = new UpdateUserRequest(true,null);
        UpdateUserResponse updateUserResponse = webTestClient
                .patch()
                .uri("/api/v1/user/"+id)
                .header(HttpHeaders.AUTHORIZATION,String.format("Bearer %s", token))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(updateUserRequest),UpdateUserRequest.class)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(UpdateUserResponse.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(updateUserResponse);

        webTestClient
                .delete()
                .uri("/api/v1/user/"+id)
                .header(HttpHeaders.AUTHORIZATION,String.format("Bearer %s", token))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isNoContent();

        UpdatePasswordRequest updatePasswordRequest = new UpdatePasswordRequest(adminPassword,"test123456");
        webTestClient
                .patch()
                .uri("/api/v1/user/profile")
                .header(HttpHeaders.AUTHORIZATION,String.format("Bearer %s", token))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(updatePasswordRequest),UpdatePasswordRequest.class)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .returnResult()
        ;
    }


    @Test
    void projectManagerJourney() {

        UserRegistrationRequest createNewUser = new UserRegistrationRequest("testus","testus","testuss@tasky.test");

        UserRegistrationResponse userRegistrationResponse = webTestClient.post()
                .uri("/api/v1/user")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION,String.format("Bearer %s", token))
                .body(Mono.just(createNewUser), UserRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(UserRegistrationResponse.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(userRegistrationResponse);
        Long id = userRegistrationResponse.userDto().id();

        assertNotNull(id);

        UpdateUserRequest updateUserRequest = new UpdateUserRequest(false,"PROJECT_MANAGER");
        UpdateUserResponse updateUserResponse = webTestClient
                .patch()
                .uri("/api/v1/user/"+id)
                .header(HttpHeaders.AUTHORIZATION,String.format("Bearer %s", token))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(updateUserRequest),UpdateUserRequest.class)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(UpdateUserResponse.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(updateUserResponse);

        UserAuthenticationRequest ProjectManagerRegistrationRequest = new UserAuthenticationRequest(userRegistrationResponse.userDto().email(),userRegistrationResponse.password());

        String tokenPM = Objects.requireNonNull(webTestClient.post()
                        .uri(LOGIN_URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .body(Mono.just(ProjectManagerRegistrationRequest), UserAuthenticationRequest.class)
                        .exchange()
                        .expectStatus()
                        .isOk()
                        .returnResult(Void.class)
                        .getResponseHeaders()
                        .get(HttpHeaders.AUTHORIZATION))
                .get(0);
        assertFalse(tokenPM.isEmpty());

        CreateProjectRequest createProjectRequest =  new CreateProjectRequest("name", 1692881836L,"desc");
        webTestClient.post()
                .uri("/api/v1/project")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION,String.format("Bearer %s", tokenPM))
                .body(Mono.just(createProjectRequest), UserRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isCreated();

        webTestClient.post()
                .uri("/api/v1/project")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION,String.format("Bearer %s", tokenPM))
                .body(Mono.just(createProjectRequest), UserRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isCreated();

        UpdateProjectRequest updateProjectRequest = new UpdateProjectRequest("test","desc","In progress",null);
        webTestClient
                .patch()
                .uri("/api/v1/project/1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION,String.format("Bearer %s", tokenPM))
                .body(Mono.just(updateProjectRequest),UpdateProjectRequest.class)
                .exchange()
                .expectStatus()
                .isOk();

        webTestClient
                .get()
                .uri("/api/v1/project/1")
                .accept(
                        MediaType.APPLICATION_JSON
                )
                .header(
                        HttpHeaders
                                .AUTHORIZATION
                        ,String.format(
                                "Bearer %s", tokenPM
                        )
                )
                .exchange()
                .expectStatus()
                .isOk();

        webTestClient
                .get()
                .uri("/api/v1/project")
                .accept(
                        MediaType.APPLICATION_JSON
                )
                .header(
                        HttpHeaders
                                .AUTHORIZATION
                        ,String.format(
                                "Bearer %s", tokenPM
                        )
                )
                .exchange()
                .expectStatus()
                .isOk();

        webTestClient
                .delete()
                .uri("/api/v1/project/2")
                .accept(
                        MediaType.APPLICATION_JSON
                )
                .header(
                        HttpHeaders
                                .AUTHORIZATION
                        ,String.format(
                                "Bearer %s", tokenPM
                        )
                )
                .exchange()
                .expectStatus()
                .isNoContent();

        AddUserToProjectRequest request = new AddUserToProjectRequest(1L);
        webTestClient
                .patch()
                .uri("/api/v1/project/1/addUser")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header(
                        HttpHeaders
                                .AUTHORIZATION
                        ,String.format(
                                "Bearer %s", tokenPM
                        )
                )
                .body(Mono.just(request),AddUserToProjectRequest.class)
                .exchange()
                .expectStatus()
                .isOk();

        CreateRunRequest createRunRequest = new CreateRunRequest("name","description",1692881822L,1692881836L);

        webTestClient
                .post()
                .uri("/api/v1/run/project/1")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .header(
                        HttpHeaders
                                .AUTHORIZATION
                        ,String.format(
                                "Bearer %s", tokenPM
                        )
                )
                .body(Mono.just(createRunRequest),CreateRunRequest.class)
                .exchange()
                .expectStatus()
                .isCreated();

        CreateRunRequest createRunRequest2 = new CreateRunRequest("name2","description",1692881822L,1692881836L);

        webTestClient
                .post()
                .uri("/api/v1/run/project/1")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .header(
                        HttpHeaders
                                .AUTHORIZATION
                        ,String.format(
                                "Bearer %s", tokenPM
                        )
                )
                .body(Mono.just(createRunRequest2),CreateRunRequest.class)
                .exchange()
                .expectStatus()
                .isCreated();

        webTestClient
                .get()
                .uri("/api/v1/run/2")
                .accept(MediaType.APPLICATION_JSON)
                .header(
                        HttpHeaders
                                .AUTHORIZATION
                        ,String.format(
                                "Bearer %s", tokenPM
                        )
                )
                .exchange()
                .expectStatus()
                .isOk();

        webTestClient
                .get()
                .uri("/api/v1/run/project/1")
                .accept(MediaType.APPLICATION_JSON)
                .header(
                        HttpHeaders
                                .AUTHORIZATION
                        ,String.format(
                                "Bearer %s", tokenPM
                        )
                )
                .exchange()
                .expectStatus()
                .isOk();

        UpdateRunRequest updateRunRequest = new UpdateRunRequest(null,null,null,null,"In progress");
        webTestClient
                .patch()
                .uri("/api/v1/run/1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header(
                        HttpHeaders
                                .AUTHORIZATION
                        ,String.format(
                                "Bearer %s", tokenPM
                        )
                )
                .body(Mono.just(updateRunRequest),UpdateRunRequest.class)
                .exchange()
                .expectStatus()
                .isOk();

        webTestClient
                .delete()
                .uri("/api/v1/run/2")
                .accept(MediaType.APPLICATION_JSON)
                .header(
                        HttpHeaders
                                .AUTHORIZATION
                        ,String.format(
                                "Bearer %s", tokenPM
                        )
                )
                .exchange()
                .expectStatus()
                .isNoContent();

        CreateFeatureRequest createFeatureRequest = new CreateFeatureRequest("name","descr");
        webTestClient
                .post()
                .uri("/api/v1/feature/run/1")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .header(
                        HttpHeaders
                                .AUTHORIZATION
                        ,String.format(
                                "Bearer %s", tokenPM
                        )
                )
                .body(Mono.just(createFeatureRequest),CreateFeatureRequest.class)
                .exchange()
                .expectStatus()
                .isCreated();

        CreateFeatureRequest createFeatureRequest2 = new CreateFeatureRequest("name","descr");
        webTestClient
                .post()
                .uri("/api/v1/feature/run/1")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .header(
                        HttpHeaders
                                .AUTHORIZATION
                        ,String.format(
                                "Bearer %s", tokenPM
                        )
                )
                .body(Mono.just(createFeatureRequest2),CreateFeatureRequest.class)
                .exchange()
                .expectStatus()
                .isCreated();

        webTestClient
                .get()
                .uri("api/v1/feature/1")
                .accept(MediaType.APPLICATION_JSON)
                .header(
                        HttpHeaders
                                .AUTHORIZATION
                        ,String.format(
                                "Bearer %s", tokenPM
                        )
                )
                .exchange()
                .expectStatus()
                .isOk();

        webTestClient
                .get()
                .uri("api/v1/feature/1")
                .accept(MediaType.APPLICATION_JSON)
                .header(
                        HttpHeaders
                                .AUTHORIZATION
                        ,String.format(
                                "Bearer %s", tokenPM
                        )
                )
                .exchange()
                .expectStatus()
                .isOk();

        webTestClient
                .get()
                .uri("api/v1/feature/run/1")
                .accept(MediaType.APPLICATION_JSON)
                .header(
                        HttpHeaders
                                .AUTHORIZATION
                        ,String.format(
                                "Bearer %s", tokenPM
                        )
                )
                .exchange()
                .expectStatus()
                .isOk();

        UpdateFeatureRequest updateFeatureRequest = new UpdateFeatureRequest(null,null,"In progress");
        webTestClient
                .patch()
                .uri("api/v1/feature/1")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .header(
                        HttpHeaders
                                .AUTHORIZATION
                        ,String.format(
                                "Bearer %s", tokenPM
                        )
                )
                .body(Mono.just(updateFeatureRequest), UpdateFeatureRequest.class)
                .exchange()
                .expectStatus()
                .isOk();

        webTestClient
                .delete()
                .uri("api/v1/feature/2")
                .header(
                        HttpHeaders
                                .AUTHORIZATION
                        ,String.format(
                                "Bearer %s", tokenPM
                        )
                )
                .exchange()
                .expectStatus()
                .isNoContent();

        CreateToDoRequest createToDoRequest = new CreateToDoRequest("name","task","descr");

        webTestClient
                .post()
                .uri("/api/v1/toDo/feature/1")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .header(
                        HttpHeaders
                                .AUTHORIZATION
                        ,String.format(
                                "Bearer %s", tokenPM
                        )
                )
                .body(Mono.just(createToDoRequest),CreateToDoRequest.class)
                .exchange()
                .expectStatus()
                .isCreated();

        CreateToDoRequest createToDoRequest2 = new CreateToDoRequest("name","task","descr");

        webTestClient
                .post()
                .uri("/api/v1/toDo/feature/1")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .header(
                        HttpHeaders
                                .AUTHORIZATION
                        ,String.format(
                                "Bearer %s", tokenPM
                        )
                )
                .body(Mono.just(createToDoRequest2),CreateToDoRequest.class)
                .exchange()
                .expectStatus()
                .isCreated();

        webTestClient
                .get()
                .uri("/api/v1/toDo/1")
                .accept(MediaType.APPLICATION_JSON)
                .header(
                        HttpHeaders
                                .AUTHORIZATION
                        ,String.format(
                                "Bearer %s", tokenPM
                        )
                )
                .exchange()
                .expectStatus()
                .isOk();

        webTestClient
                .get()
                .uri("/api/v1/toDo/profile")
                .accept(MediaType.APPLICATION_JSON)
                .header(
                        HttpHeaders
                                .AUTHORIZATION
                        ,String.format(
                                "Bearer %s", tokenPM
                        )
                )
                .exchange()
                .expectStatus()
                .isOk();

        webTestClient
                .get()
                .uri("/api/v1/toDo/feature/1")
                .accept(MediaType.APPLICATION_JSON)
                .header(
                        HttpHeaders
                                .AUTHORIZATION
                        ,String.format(
                                "Bearer %s", tokenPM
                        )
                )
                .exchange()
                .expectStatus()
                .isOk();

        UpdateTodoRequest updateTodoRequest = new UpdateTodoRequest(null,null,null,null,"In progress");
        webTestClient
                .patch()
                .uri("/api/v1/toDo/1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header(
                        HttpHeaders
                                .AUTHORIZATION
                        ,String.format(
                                "Bearer %s", tokenPM
                        )
                )
                .body(Mono.just(updateTodoRequest),UpdateTodoRequest.class)
                .exchange()
                .expectStatus()
                .isOk();

        webTestClient
                .delete()
                .uri("/api/v1/toDo/2")
                .header(
                        HttpHeaders
                                .AUTHORIZATION
                        ,String.format(
                                "Bearer %s", tokenPM
                        )
                )
                .exchange()
                .expectStatus()
                .isNoContent();

        CreateCommentRequest createCommentRequest = new CreateCommentRequest("name","content");

        webTestClient
                .post()
                .uri("/api/v1/comment/toDo/1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header(
                        HttpHeaders
                                .AUTHORIZATION
                        ,String.format(
                                "Bearer %s", tokenPM
                        )
                )
                .body(Mono.just(createCommentRequest),CreateCommentRequest.class)
                .exchange()
                .expectStatus()
                .isCreated();

        webTestClient
                .get()
                .uri("/api/v1/comment/1")

                .accept(MediaType.APPLICATION_JSON)
                .header(
                        HttpHeaders
                                .AUTHORIZATION
                        ,String.format(
                                "Bearer %s", tokenPM
                        )
                )
                .exchange()
                .expectStatus()
                .isOk();

        webTestClient
                .get()
                .uri("/api/v1/comment/toDo/1")
                .accept(MediaType.APPLICATION_JSON)
                .header(
                        HttpHeaders
                                .AUTHORIZATION
                        ,String.format(
                                "Bearer %s", tokenPM
                        )
                )
                .exchange()
                .expectStatus()
                .isOk();

        UpdateCommentRequest updateCommentRequest = new UpdateCommentRequest("namee","zaereazrae");

        webTestClient
                .patch()
                .uri("/api/v1/comment/1")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .header(
                        HttpHeaders
                                .AUTHORIZATION
                        ,String.format(
                                "Bearer %s", tokenPM
                        )
                )
                .body(Mono.just(updateCommentRequest),UpdateCommentRequest.class)
                .exchange()
                .expectStatus()
                .isOk();

        webTestClient
                .delete()
                .uri("/api/v1/comment/1")
                .header(
                        HttpHeaders
                                .AUTHORIZATION
                        ,String.format(
                                "Bearer %s", tokenPM
                        )
                )
                .exchange()
                .expectStatus()
                .isNoContent();


    }


}
