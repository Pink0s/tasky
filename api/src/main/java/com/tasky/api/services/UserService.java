package com.tasky.api.services;

import com.tasky.api.dto.user.UserAuthenticationRequest;
import com.tasky.api.dto.user.UserAuthenticationResponse;

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
}
