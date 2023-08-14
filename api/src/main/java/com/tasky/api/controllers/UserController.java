package com.tasky.api.controllers;

import com.tasky.api.dto.user.UserAuthenticationRequest;
import com.tasky.api.dto.user.UserAuthenticationResponse;
import com.tasky.api.dto.user.UserRegistrationRequest;
import com.tasky.api.dto.user.UserRegistrationResponse;
import com.tasky.api.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller class to handle user-related API endpoints.
 */

@RestController
@RequestMapping("/api/v1/user")
public class UserController {
    private final UserService userService;
    private final Logger logger = LoggerFactory.getLogger(UserController.class);

    /**
     * Constructs a UserController with the specified UserService.
     *
     * @param userService The UserService implementation to be used.
     */

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Endpoint for user authentication.
     *
     * @param request The UserAuthenticationRequest containing user authentication credentials.
     * @return ResponseEntity containing the UserAuthenticationResponse with authentication result and token.
     * */
    @PostMapping("auth")
    public ResponseEntity<UserAuthenticationResponse> login(@RequestBody UserAuthenticationRequest request) {

        logger.info("POST /api/v1/user/auth with account :"+ request.username());

        UserAuthenticationResponse response = userService.login(request);

        return ResponseEntity
                .ok()
                .header(HttpHeaders.AUTHORIZATION,response.token())
                .body(response);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserRegistrationResponse postUser(@RequestBody UserRegistrationRequest request) {
        logger.info("POST /api/v1/user");
        return userService.userRegistration(request);
    }

}
