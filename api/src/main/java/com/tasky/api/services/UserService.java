package com.tasky.api.services;

import com.tasky.api.dto.user.UserAuthenticationRequest;
import com.tasky.api.dto.user.UserAuthenticationResponse;
import com.tasky.api.dto.user.UserRegistrationRequest;
import com.tasky.api.dto.user.UserRegistrationResponse;
import org.springframework.lang.Nullable;

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
}
