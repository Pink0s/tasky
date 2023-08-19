package com.tasky.api.journey;

import com.tasky.api.AbstractTestContainer;
import com.tasky.api.dto.project.CreateProjectRequest;
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

        CreateProjectRequest createProjectRequest =  new CreateProjectRequest("name", Timestamp.from(Instant.now()),"desc");
        webTestClient.post()
                .uri("/api/v1/project")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION,String.format("Bearer %s", tokenPM))
                .body(Mono.just(createProjectRequest), UserRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isCreated();
    }
}
