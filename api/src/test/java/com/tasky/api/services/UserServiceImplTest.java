package com.tasky.api.services;

import com.tasky.api.dto.user.UserAuthenticationRequest;
import com.tasky.api.dto.user.UserAuthenticationResponse;
import com.tasky.api.dto.user.UserDto;
import com.tasky.api.mappers.UserDtoMapper;
import com.tasky.api.models.User;
import com.tasky.api.utilities.JwtUtility;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the {@link UserServiceImpl} class.
 */
@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock private AuthenticationManager authenticationManager;
    @Mock private JwtUtility jwtUtility;
    @Mock private UserDtoMapper userDtoMapper;

    @InjectMocks private UserServiceImpl underTest;

    /**
     * Tests the {@link UserServiceImpl#login(UserAuthenticationRequest)} method to ensure proper user authentication and token generation.
     */
    @Test
    void login() {
        // GIVEN
        UserAuthenticationRequest authRequest = new UserAuthenticationRequest(
                "username",
                "password"
        );

        User userPrincipal = new User(
                "username",
                "password",
                "email@example.com",
                "encodedPassword"
        );

        userPrincipal
                .setRole("ROLE_USER");

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userPrincipal,
                null
        );

        when(
                authenticationManager
                        .authenticate(
                                any()
                        )
        ).thenReturn(
                authentication
        );


        UserDto userDto = new UserDto(
                userPrincipal.getId(),
                userPrincipal.getFirst_name(),
                userPrincipal.getLast_name(),
                userPrincipal.getEmail(),
                userPrincipal.getRole(),
                userPrincipal.getNever_connected()
        );

        when(
                userDtoMapper
                        .apply(
                                any()
                        )
        ).thenReturn(
                userDto
        );

        when(
                jwtUtility.issueToken(
                        userDto.email(),
                        userDto.role()
                )
        ).thenReturn(
                "testToken"
        );

        // WHEN
        UserAuthenticationResponse response = underTest.login(authRequest);

        // THEN
        assertNotNull(response);
        assertEquals("testToken", response.token());
        assertEquals(userDto, response.userDto());

        verify(authenticationManager, times(1)).authenticate(any());
        verify(userDtoMapper, times(1)).apply(any());
        verify(jwtUtility, times(1)).issueToken(
                userDto.email(),
                userDto.role()
        );
    }
}
