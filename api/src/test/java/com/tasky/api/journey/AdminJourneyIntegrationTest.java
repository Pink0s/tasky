package com.tasky.api.journey;

import com.tasky.api.AbstractTestContainer;
import com.tasky.api.dto.user.UserAuthenticationRequest;
import com.tasky.api.dto.user.UserRegistrationRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * Integration tests for the admin journey, focusing on authentication and login functionality.
 * These tests are performed within the context of a Spring Boot application.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AdminJourneyIntegrationTest extends AbstractTestContainer {

    @Autowired
    private WebTestClient webTestClient;
    private static final String LOGIN_URI = "/api/v1/user/auth";

    @Value("#{'${server.default-admin-password}'}")
    private String adminPassword;
    @Value("#{'${server.default-admin-account}'}")
    private String adminAccount;

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

        String token = Objects.requireNonNull(webTestClient.post()
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
        webTestClient.post()
                .uri("/api/v1/user")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION,String.format("Bearer %s", token))
                .body(Mono.just(createNewUser), UserRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isCreated();
    }
}
