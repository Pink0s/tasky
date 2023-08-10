package com.tasky.api.services;

import com.tasky.api.dto.user.UserAuthenticationRequest;
import com.tasky.api.dto.user.UserAuthenticationResponse;
import com.tasky.api.dto.user.UserDto;
import com.tasky.api.mappers.UserDtoMapper;
import com.tasky.api.models.User;
import com.tasky.api.utilities.JwtUtility;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

/**
 * Implementation of the UserService interface, providing user-related operations.
 */
@Service
public class UserServiceImpl implements UserService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtility jwtUtility;
    private final UserDtoMapper userDtoMapper;

    /**
     * Constructs a UserServiceImpl with the necessary dependencies.
     *
     * @param authenticationManager The AuthenticationManager implementation.
     * @param jwtUtility The JwtUtility implementation for token management.
     * @param userDtoMapper The UserDtoMapper for mapping User entities to DTOs.
     */
    public UserServiceImpl(AuthenticationManager authenticationManager, JwtUtility jwtUtility, UserDtoMapper userDtoMapper) {
        this.authenticationManager = authenticationManager;
        this.jwtUtility = jwtUtility;
        this.userDtoMapper = userDtoMapper;
    }

    /**
     * Performs user authentication based on the provided authentication request.
     *
     * @param request The UserAuthenticationRequest containing user authentication credentials.
     * @return A UserAuthenticationResponse containing authentication result and token.
     */
    @Override
    public UserAuthenticationResponse login(UserAuthenticationRequest request) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()
                )
        );

        User principal = (User) authentication.getPrincipal();

        UserDto user = userDtoMapper.apply(principal);
        String token = jwtUtility.issueToken(user.email(),user.role());

        return new UserAuthenticationResponse(token,user);
    }
}
