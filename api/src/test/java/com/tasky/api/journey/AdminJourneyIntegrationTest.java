package com.tasky.api.journey;

import com.tasky.api.AbstractTestContainer;
import com.tasky.api.dto.user.UserAuthenticationRequest;
import org.junit.jupiter.api.Assertions;
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
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AdminJourneyIntegrationTest extends AbstractTestContainer {

    @Autowired
    private WebTestClient webTestClient;
    private static final String LOGIN_URI = "/api/v1/user/auth";

    @Value("#{'${server.default-admin-password}'}")
    private String adminPassword;
    @Value("#{'${server.default-admin-account}'}")
    private String adminAccount;

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
    }
}
