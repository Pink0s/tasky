package com.tasky.api.controllers;

import com.tasky.api.dto.user.*;
import com.tasky.api.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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

    /**
     * Creates a new user by registering their information.
     *
     * @param request The {@link UserRegistrationRequest} containing the user's registration information.
     * @return A {@link UserRegistrationResponse} containing the newly registered user's information.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserRegistrationResponse postUser(@RequestBody UserRegistrationRequest request) {
        logger.info("POST /api/v1/user");
        return userService.userRegistration(request);
    }

    /**
     * Retrieves the profile information of the authenticated user.
     *
     * @param authentication The authentication object representing the current authenticated user.
     * @return The user's profile information as a {@link UserDto}.
     */
    @GetMapping("profile")
    public UserDto getProfile(Authentication authentication) {
        logger.info("GET /api/v1/user/profile");
        return userService.getProfile(authentication);
    }

    /**
     * Retrieves user information by their ID.
     *
     * @param userId The ID of the user to retrieve information for.
     * @return The user's information as a {@link UserDto}.
     */
    @GetMapping("{userId}")
    public UserDto getUserById(@PathVariable Long userId) {
        logger.info("GET /api/v1/user/"+userId);
        return userService.getUserById(userId);
    }

    /**
     * Retrieves a list of users based on the provided search criteria.
     *
     * @param request The {@link SearchUsersRequest} containing the search criteria.
     * @return A {@link SearchUsersResponse} containing the list of matching users.
     */
    @GetMapping
    public SearchUsersResponse getUsers(@RequestBody SearchUsersRequest request) {
        logger.info("GET /api/v1/user");
        return userService.searchUsers(request);
    }

    /**
     * Updates user information for a specific user.
     *
     * @param userId  The ID of the user to update.
     * @param request The {@link UpdateUserRequest} containing the updated user information.
     * @return The updated user information as a {@link UserDto}.
     */
    @PutMapping("{userId}")
    public UserDto updateUser(@PathVariable Long userId, @RequestBody UpdateUserRequest request) {
        logger.info("PUT /api/v1/user/"+userId);
        return userService.updateUser(request,userId);
    }

    /**
     * Updates the password of the authenticated user.
     *
     * @param authentication The authentication object representing the current authenticated user.
     * @param request        The {@link UpdatePasswordRequest} containing the updated password information.
     */
    @PutMapping("profile")
    public void changePassword(Authentication authentication,UpdatePasswordRequest request) {
        logger.info("PUT /api/v1/user/profile");
        userService.updatePassword(authentication,request);
    }

    /**
     * Deletes a user based on their ID.
     *
     * @param id The ID of the user to be deleted.
     */
    @DeleteMapping("{id}")
    public void deleteUser(@PathVariable Long id) {
        logger.info("DELETE /api/v1/user"+id);
        userService.deleteUserById(id);
    }

}
