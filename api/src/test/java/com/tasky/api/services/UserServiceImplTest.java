package com.tasky.api.services;

import com.tasky.api.configurations.errors.BadRequestException;
import com.tasky.api.configurations.errors.DuplicationException;
import com.tasky.api.dao.UserDao;
import com.tasky.api.dto.user.*;
import com.tasky.api.mappers.UserDtoMapper;
import com.tasky.api.models.User;
import com.tasky.api.utilities.JwtUtility;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.sql.Timestamp;
import java.time.Instant;

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
    @Mock private UserDao userDao;
    @Mock private PasswordEncoder passwordEncoder;

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

    /**
     * Unit test to ensure that user registration throws an error when required fields are missing.
     */
    @Test
    void userRegistrationShouldThrowErrorWhenFieldAreMissing() {
        //GIVEN
        UserRegistrationRequest requestWithoutEmail = new UserRegistrationRequest("toto","test",null);
        UserRegistrationRequest requestWithoutFirstname = new UserRegistrationRequest(null,"test","null@test.com");
        UserRegistrationRequest requestWithoutLastname= new UserRegistrationRequest("toto",null,"null@test.com");
        UserRegistrationRequest emptyRequest = new UserRegistrationRequest(null,null,null);

        //WHEN THEN
        assertThrows(BadRequestException.class, () -> underTest.userRegistration(requestWithoutEmail));
        assertThrows(BadRequestException.class, () -> underTest.userRegistration(requestWithoutFirstname));
        assertThrows(BadRequestException.class, () -> underTest.userRegistration(requestWithoutLastname));
        assertThrows(BadRequestException.class, () -> underTest.userRegistration(emptyRequest));
        assertThrows(BadRequestException.class, () -> underTest.userRegistration(null));
    }

    /**
     * Unit test to ensure that user registration throws a duplication error when attempting to register an existing user.
     */
    @Test
    void userRegistrationShouldThrowDuplicationError() {
        //GIVEN
        UserRegistrationRequest request = new UserRegistrationRequest("toto","test","test@test.com");
        Mockito.when(userDao.isUserExists("test@test.com")).thenReturn(true);
        //WHEN THEN
        assertThrows(DuplicationException.class, () -> underTest.userRegistration(request));
    }

    /**
     * Unit test for successful user registration.
     */
    @Test
    void userRegistration() {

        String firstName = "toto";
        String lastname = "lastname";
        String email = "email@email.com";
        String fakeEncodedPassword = "fakeEncodedPassword";

        //GIVEN
        UserRegistrationRequest request = new UserRegistrationRequest(firstName,lastname,email);

        Mockito.when(passwordEncoder.encode(any())).thenReturn(fakeEncodedPassword);

        User user = new User(
                firstName,
                lastname,
                email,
                fakeEncodedPassword
        );

        user.setId(2L);
        user.setCreated_at(Timestamp.from(Instant.now()));
        user.setUpdated_at(Timestamp.from(Instant.now()));

        when(
                userDao.insertUser(any())
        ).thenReturn(user);

        when(
                userDao.isUserExists(email)
        ).thenReturn(false);

        when(
                userDtoMapper.apply(any())
        ).thenReturn(
                new UserDto(
                        user.getId(),
                        user.getFirst_name(),
                        user.getLast_name(),
                        user.getEmail(),
                        user.getRole(),
                        user.getNever_connected())
        );

        //WHEN
        UserRegistrationResponse response = underTest.userRegistration(request);

        //THEN
        assertEquals(response.userDto().email(), request.email());
        assertEquals(response.userDto().last_name(), request.lastName());
        assertEquals(response.userDto().email(), request.email());
    }

}
