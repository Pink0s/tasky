package com.tasky.api.services;

import com.tasky.api.dto.user.*;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;

/**
 * Service interface for managing user-related operations.
 */
public interface UserService {
    /**
     * Performs user authentication based on the provided authentication request.
     *
     * @param request The UserAuthenticationRequest containing user authentication credentials.
     * @return A UserAuthenticationResponse containing authentication result and token.
     */
    UserAuthenticationResponse login(UserAuthenticationRequest request);

    /**
     * Performs a user registration based on the provided registration request.
     *
     * @param request The UserRegistrationRequest containing user details.
     * @return A UserRegistrationResponse containing the user id.
     */
    UserRegistrationResponse userRegistration(@Nullable UserRegistrationRequest request);

    /**
     * Searches for users based on the criteria specified in the provided {@link SearchUsersRequest}.
     *
     * @param request The {@link SearchUsersRequest} containing the search criteria.
     * @return A {@link SearchUsersResponse} containing the search results.
     */
    SearchUsersResponse searchUsers(@Nullable SearchUsersRequest request);

    /**
     * Deletes a user from the system based on the unique identifier provided.
     *
     * @param id The unique identifier (ID) of the user to be deleted.
     */
    void deleteUserById(Long id);

    /**
     * Updates a user's information based on the provided {@link UpdateUserRequest}.
     *
     * @param request The {@link UpdateUserRequest} containing the updated user information.
     * @return The updated user information as a {@link UserDto}.
     */
    UpdateUserResponse updateUser(@Nullable UpdateUserRequest request, Long userId);

    /**
     * Updates a user's password based on the provided {@link UpdatePasswordRequest}.
     *
     * @param request The {@link UpdatePasswordRequest} containing the updated password information.
     */
    void updatePassword(Authentication authentication ,@Nullable UpdatePasswordRequest request);

    /**
     * Retrieves the profile information of the authenticated user.
     *
     * @param authentication The authentication object representing the current authenticated user.
     * @return The user's profile information as a {@link UserDto}.
     */
    UserDto getProfile(Authentication authentication);

    /**
     * Retrieves the profile information by id.
     *
     * @param id The unique identifier (ID) of the use retrieve information
     * @return The UserDto information as a {@link UserDto}.
     */
    UserDto getUserById(Long id);

}
