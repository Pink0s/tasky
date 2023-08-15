package com.tasky.api.services;

import com.tasky.api.configurations.errors.BadRequestException;
import com.tasky.api.configurations.errors.DuplicationException;
import com.tasky.api.configurations.errors.NotFoundException;
import com.tasky.api.dao.UserDao;
import com.tasky.api.dto.user.*;
import com.tasky.api.mappers.UserDtoMapper;
import com.tasky.api.models.User;
import com.tasky.api.utilities.JwtUtility;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
                userPrincipal.getFirstName(),
                userPrincipal.getLastName(),
                userPrincipal.getEmail(),
                userPrincipal.getRole(),
                userPrincipal.getNeverConnected()
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
        user.setCreatedAt(Timestamp.from(Instant.now()));
        user.setUpdatedAt(Timestamp.from(Instant.now()));

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
                        user.getFirstName(),
                        user.getLastName(),
                        user.getEmail(),
                        user.getRole(),
                        user.getNeverConnected())
        );

        //WHEN
        UserRegistrationResponse response = underTest.userRegistration(request);

        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);

        Mockito.verify(userDao).insertUser(userArgumentCaptor.capture());
        User userCaptured = userArgumentCaptor.getValue();

        //THEN
        assertEquals(userCaptured.getEmail(),user.getEmail());
        assertEquals(userCaptured.getLastName(),user.getLastName());
        assertEquals(userCaptured.getFirstName(),user.getFirstName());
        assertEquals(userCaptured.getRole(),user.getRole());
        assertEquals(userCaptured.getNeverConnected(),user.getNeverConnected());
        assertEquals(response.userDto().email(), request.email());
        assertEquals(response.userDto().lastName(), request.lastName());
        assertEquals(response.userDto().email(), request.email());
    }

    private List<User> createUserList() {
        List<User> userList = new ArrayList<>();

        User user1 = new User("John", "Doe", "john@example.com", "encodedPassword1");
        user1.setId(1L);
        userList.add(user1);

        User user2 = new User("Jane", "Smith", "jane@example.com", "encodedPassword2");
        user2.setId(2L);
        userList.add(user2);

        return userList;
    }

    private List<UserDto> createUserDtoList(List<User> userList) {
        return userList.stream()
                .map(user -> new UserDto(
                                user.getId(),
                                user.getFirstName(),
                                user.getLastName(),
                                user.getEmail(),
                                user.getRole(),
                                user.getNeverConnected()
                        )
                )
                .toList();
    }

    @Test
    void searchUsersByEmailType() {
        // GIVEN
        SearchUsersRequest request = new SearchUsersRequest("email", "test@example.com", 0);
        List<User> userList = createUserList();
        Page<User> userPage = new PageImpl<>(userList);

        when(userDao.selectAllUsersByEmail(eq("test@example.com"), any(Pageable.class))).thenReturn(userPage);

        List<UserDto> userDto = createUserDtoList(userList);

        when(userDtoMapper.apply(any(User.class))).thenReturn(userDto.get(0), userDto.get(1));

        // WHEN
        SearchUsersResponse response = underTest.searchUsers(request);

        // THEN
        assertNotNull(response);
        assertEquals(userDto, response.users());
        assertNotNull(response.pageableDto());
        assertEquals(0, response.pageableDto().currentPage());
        assertEquals(1, response.pageableDto().numberOfPage());
        assertEquals(2, response.pageableDto().numberOfResult());
    }

    @Test
    void searchUsersByFirstNameType() {
        // GIVEN
        SearchUsersRequest request = new SearchUsersRequest("firstName", "John", 0);
        List<User> userList = createUserList();
        Page<User> userPage = new PageImpl<>(userList);

        when(userDao.selectAllUsersByFirstName(eq("John"), any(Pageable.class))).thenReturn(userPage);

        List<UserDto> userDtos = createUserDtoList(userList);
        when(userDtoMapper.apply(any(User.class))).thenReturn(userDtos.get(0), userDtos.get(1));

        // WHEN
        SearchUsersResponse response = underTest.searchUsers(request);

        // THEN
        assertNotNull(response);
        assertEquals(userDtos, response.users());
        assertNotNull(response.pageableDto());
        assertEquals(0, response.pageableDto().currentPage());
        assertEquals(1, response.pageableDto().numberOfPage());
        assertEquals(2, response.pageableDto().numberOfResult());
    }

    @Test
    void searchUsersByLastNameType() {
        // GIVEN
        SearchUsersRequest request = new SearchUsersRequest("lastName", "Doe", 0);
        List<User> userList = createUserList();
        Page<User> userPage = new PageImpl<>(userList);

        when(userDao.selectAllUsersByLastName(eq("Doe"), any(Pageable.class))).thenReturn(userPage);

        List<UserDto> userDtos = createUserDtoList(userList);
        when(userDtoMapper.apply(any(User.class))).thenReturn(userDtos.get(0), userDtos.get(1));

        // WHEN
        SearchUsersResponse response = underTest.searchUsers(request);

        // THEN
        assertNotNull(response);
        assertEquals(userDtos, response.users());
        assertNotNull(response.pageableDto());
        assertEquals(0, response.pageableDto().currentPage());
        assertEquals(1, response.pageableDto().numberOfPage());
        assertEquals(2, response.pageableDto().numberOfResult());
    }

    @Test
    void searchUsersWithInvalidTypeShouldThrowException() {
        // GIVEN
        SearchUsersRequest request = new SearchUsersRequest("invalidType", "test", 0);

        // WHEN THEN
        assertThrows(BadRequestException.class, () -> underTest.searchUsers(request));
    }

    @Test
    void searchUsersWithNullRequestShouldDefaultToEmailType() {
        // GIVEN

        List<User> userList = createUserList();
        Page<User> userPage = new PageImpl<>(userList);

        when(userDao.selectAllUsersByEmail(eq(""), any(Pageable.class))).thenReturn(userPage);

        List<UserDto> userDtos = createUserDtoList(userList);
        when(userDtoMapper.apply(any(User.class))).thenReturn(userDtos.get(0), userDtos.get(1));

        // WHEN
        SearchUsersResponse response = underTest.searchUsers(null);

        // THEN
        assertNotNull(response);
        assertEquals(userDtos, response.users());
        assertNotNull(response.pageableDto());
        assertEquals(0, response.pageableDto().currentPage());
        assertEquals(1, response.pageableDto().numberOfPage());
        assertEquals(2, response.pageableDto().numberOfResult());
    }

    @Test
    void searchUsersWithNullRequestTypeShouldDefaultToEmailType() {
        // GIVEN
        SearchUsersRequest request = new SearchUsersRequest(null,"test@example.com", 0);
        List<User> userList = createUserList();
        Page<User> userPage = new PageImpl<>(userList);

        when(userDao.selectAllUsersByEmail(eq("test@example.com"), any(Pageable.class))).thenReturn(userPage);

        List<UserDto> userDto = createUserDtoList(userList);

        when(userDtoMapper.apply(any(User.class))).thenReturn(userDto.get(0), userDto.get(1));

        // WHEN
        SearchUsersResponse response = underTest.searchUsers(request);

        // THEN
        assertNotNull(response);
        assertEquals(userDto, response.users());
        assertNotNull(response.pageableDto());
        assertEquals(0, response.pageableDto().currentPage());
        assertEquals(1, response.pageableDto().numberOfPage());
        assertEquals(2, response.pageableDto().numberOfResult());
    }

    @Test
    void searchUsersWithInvalidPageShouldThrowException() {
        // GIVEN
        SearchUsersRequest request = new SearchUsersRequest("email", "test@example.com", 1);

        List<User> userList = createUserList();
        Page<User> userPage = new PageImpl<>(userList);

        // Configure the mock behavior for userDao.selectAllUsersByEmail
        when(userDao.selectAllUsersByEmail(eq("test@example.com"), any(Pageable.class))).thenReturn(userPage);
        System.out.println(userPage.getTotalPages());
        // WHEN THEN
        BadRequestException exception = assertThrows(BadRequestException.class, () -> underTest.searchUsers(request));
        assertTrue(exception.getMessage().contains("Page requested does not exist"));
    }

    @Test
    void deleteNotExistingUserShouldThrowNotFoundException() {
        // GIVEN
        Long id = 333L;
        when(
                userDao.selectUserById(id)
        ).thenReturn(
                Optional.empty()
        );
        // WHEN-THEN
        assertThrows(NotFoundException.class,() -> underTest.deleteUserById(id));
    }

    @Test
    void deleteById() {
        //GIVEN
        Long id = 2L;

        when(
                userDao.selectUserById(id)
        ).thenReturn(Optional.of(
                new User(
                        "firstname",
                        "lastname",
                        "test@test.com",
                        "password")
                )
        );
        //WHEN
        underTest.deleteUserById(id);

        //THEN
        Mockito.verify(userDao).deleteUserById(id);
    }

    @Test
    void updateByIdUserNotFoundShouldThrowNotFoundException() {
        Long userId = 1L;
        UpdateUserRequest request =
                new UpdateUserRequest(
                        true,
                        "ADMIN"
                );

        when(userDao.selectUserById(userId)).thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,() -> underTest.updateUser(request,userId)
        );
    }

    @Test
    void updateUserByIdShouldThrowBadRequestWhenRequestIsNull() {
        Long userId = 1L;

        when(userDao.selectUserById(userId)
        ).thenReturn(
                        Optional.of(
                                new User(
                                        "firstname",
                                        "lastname",
                                        "test@test.com",
                                        "password"
                                )
                        )
                );

        assertThrows(BadRequestException.class,() -> underTest.updateUser(null,userId));
    }

    @Test
    void updateUserByIdShouldThrowBadRequestWhenRoleIsInvalid() {
        Long userId = 1L;
        UpdateUserRequest request =
                new UpdateUserRequest(
                        false,
                        "aezirahzeriuaheuraerz"
                );
        when(userDao.selectUserById(userId)
        ).thenReturn(
                Optional.of(
                        new User(
                                "firstname",
                                "lastname",
                                "test@test.com",
                                "password"
                        )
                )
        );

        assertThrows(BadRequestException.class,() -> underTest.updateUser(request,userId));
    }

    @Test
    void updateUserByIdShouldThrowBadRequestWhenRoleIsSameAsActual() {
        Long userId = 1L;
        UpdateUserRequest request =
                new UpdateUserRequest(
                        false,
                        "USER"
                );
        when(userDao.selectUserById(userId)
        ).thenReturn(
                Optional.of(
                        new User(
                                "firstname",
                                "lastname",
                                "test@test.com",
                                "password"
                        )
                )
        );

        assertThrows(BadRequestException.class,() -> underTest.updateUser(request,userId));
    }

    @Test
    void updateUserByIdShouldUpdateRole() {
        Long userId = 1L;

        User user = new User(
                "firstname",
                "lastname",
                "test@test.com",
                "password"
        );

        UpdateUserRequest request =
                new UpdateUserRequest(
                        false,
                        "ADMIN"
                );
        when(userDao.selectUserById(userId)
        ).thenReturn(
                Optional.of(
                        user
                )
        );

        UserDto userDtoExpected = new UserDto(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getRole(),
                user.getNeverConnected()
        );

        when(
                userDtoMapper
                        .apply(any())
        ).thenReturn(
                userDtoExpected
        );

        UpdateUserResponse response = underTest.updateUser(request,userId);

        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);

        Mockito.verify(userDao).updateUser(userArgumentCaptor.capture());
        User userCaptured = userArgumentCaptor.getValue();

        assertEquals(request.role(),userCaptured.getRole());
        assertEquals(user.getPassword(),userCaptured.getPassword());
        assertEquals(userDtoExpected,response.user());

    }

    @Test
    void updateUserByIdShouldUpdateRoleAndPassword() {
        Long userId = 1L;
        String newPasswd = "newOne";

        User user = new User(
                "firstname",
                "lastname",
                "test@test.com",
                "password"
        );

        UpdateUserRequest request =
                new UpdateUserRequest(
                        true,
                        "ADMIN"
                );

        when(passwordEncoder.encode(any())).thenReturn(newPasswd);

        when(userDao.selectUserById(userId)
        ).thenReturn(
                Optional.of(
                        user
                )
        );

        UserDto userDtoExpected = new UserDto(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getRole(),
                user.getNeverConnected()
        );

        when(
                userDtoMapper
                        .apply(any())
        ).thenReturn(
                userDtoExpected
        );

        UpdateUserResponse response = underTest.updateUser(request,userId);

        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);

        Mockito.verify(userDao).updateUser(userArgumentCaptor.capture());
        User userCaptured = userArgumentCaptor.getValue();

        assertEquals(request.role(),userCaptured.getRole());
        assertEquals(newPasswd,userCaptured.getPassword());
        assertEquals(userDtoExpected,response.user());

    }

    @Test
    void updateUserByIdShouldUpdatePassword() {
        Long userId = 1L;
        String newPasswd = "newOne";

        User user = new User(
                "firstname",
                "lastname",
                "test@test.com",
                "password"
        );

        UpdateUserRequest request =
                new UpdateUserRequest(
                        true,
                        null
                );

        when(passwordEncoder.encode(any())).thenReturn(newPasswd);

        when(userDao.selectUserById(userId)
        ).thenReturn(
                Optional.of(
                        user
                )
        );

        UserDto userDtoExpected = new UserDto(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getRole(),
                user.getNeverConnected()
        );

        when(
                userDtoMapper
                        .apply(any())
        ).thenReturn(
                userDtoExpected
        );

        UpdateUserResponse response = underTest.updateUser(request,userId);

        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);

        Mockito.verify(userDao).updateUser(userArgumentCaptor.capture());
        User userCaptured = userArgumentCaptor.getValue();

        assertEquals(user.getRole(),userCaptured.getRole());
        assertEquals(newPasswd,userCaptured.getPassword());
        assertEquals(userDtoExpected,response.user());

    }

    private User getUserPrincipal(Long userId, String firstName, String lastName, String email, String role, String password, boolean neverConnected) {
        User userPrincipal = new User(firstName, lastName, email, password);
        userPrincipal.setId(userId);
        userPrincipal.setRole(role);
        userPrincipal.setNeverConnected(neverConnected);
        return userPrincipal;
    }
    private Authentication getAuthentication(User userPrincipal) {
        return new UsernamePasswordAuthenticationToken(
                userPrincipal,
                null
        );
    }
    /**
     * Tests the {@link UserServiceImpl#getProfile(Authentication)} method to ensure that the user's profile is retrieved correctly.
     */
    @Test
    void getProfile() {
        // GIVEN

        User userPrincipal = getUserPrincipal(
                1L,
                "John",
                "Doe",
                "john@example.com",
                "USER",
                "oldPasswd",
                false
        );

        Authentication authentication = getAuthentication(userPrincipal);

        UserDto userDto = new UserDto(
                userPrincipal.getId(),
                userPrincipal.getFirstName(),
                userPrincipal.getLastName(),
                userPrincipal.getEmail(),
                userPrincipal.getRole(),
                userPrincipal.getNeverConnected()
        );

        when(userDtoMapper.apply(userPrincipal)).thenReturn(userDto);

        // WHEN
        UserDto result = underTest.getProfile(authentication);

        // THEN
        assertNotNull(result);
        assertEquals(userPrincipal.getId(), result.id());
        assertEquals(userPrincipal.getFirstName(), result.firstName());
        assertEquals(userPrincipal.getLastName(), result.lastName());
        assertEquals(userPrincipal.getEmail(), result.email());
        assertEquals(userPrincipal.getRole(), result.role());
        assertEquals(userPrincipal.getNeverConnected(), result.neverConnected());

        verify(userDtoMapper, times(1)).apply(userPrincipal);
    }

    @Test
    void updatePasswordShouldChangePasswordSuccessfully() {
        // GIVEN

        User userPrincipal = getUserPrincipal(
                1L,
                "John",
                "Doe",
                "john@example.com",
                "USER",
                "oldPasswd",
                false
        );
        String newPassword = "newPassword";
        String fakeEncodedPassword = "fakeEncodedPassword";

        Authentication authentication = getAuthentication(userPrincipal);

        UpdatePasswordRequest updatePasswordRequest = new UpdatePasswordRequest(userPrincipal.getPassword(),newPassword);

        when(passwordEncoder.matches(any(),any())).thenReturn(true);
        when(passwordEncoder.encode(any())).thenReturn(fakeEncodedPassword);

        underTest.updatePassword(authentication,updatePasswordRequest);

        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);

        Mockito.verify(userDao).updateUser(userArgumentCaptor.capture());
        User userCaptured = userArgumentCaptor.getValue();

        assertEquals(userCaptured.getPassword(), fakeEncodedPassword);
    }


    @Test
    void updatePasswordShouldThrowBadRequestWhenRequestBodyNull() {
        User userPrincipal = getUserPrincipal(
                1L,
                "John",
                "Doe",
                "john@example.com",
                "USER",
                "oldPasswd",
                false
        );

        Authentication authentication = getAuthentication(userPrincipal);

        assertThrows(BadRequestException.class,() -> underTest.updatePassword(authentication,null));
    }

    @Test
    void updatePasswordShouldThrowBadRequestWhenRequestOldPasswordIsNull() {
        User userPrincipal = getUserPrincipal(
                1L,
                "John",
                "Doe",
                "john@example.com",
                "USER",
                "oldPasswd",
                false
        );

        Authentication authentication = getAuthentication(userPrincipal);
        UpdatePasswordRequest updatePasswordRequest = new UpdatePasswordRequest(null,"newPassword");
        assertThrows(BadRequestException.class,() -> underTest.updatePassword(authentication,updatePasswordRequest));
    }

    @Test
    void updatePasswordShouldThrowBadRequestWhenRequestNewPasswordIsNull() {
        User userPrincipal = getUserPrincipal(
                1L,
                "John",
                "Doe",
                "john@example.com",
                "USER",
                "oldPasswd",
                false
        );

        Authentication authentication = getAuthentication(userPrincipal);
        when(passwordEncoder.matches(any(),any())).thenReturn(true);
        UpdatePasswordRequest updatePasswordRequest = new UpdatePasswordRequest(userPrincipal.getPassword(),null);
        assertThrows(BadRequestException.class,() -> underTest.updatePassword(authentication,updatePasswordRequest));
    }

    @Test
    void updatePasswordShouldThrowBadRequestWhenRequestNewPasswordAndOldPasswordAreEquals() {
        User userPrincipal = getUserPrincipal(
                1L,
                "John",
                "Doe",
                "john@example.com",
                "USER",
                "oldPasswd",
                false
        );

        Authentication authentication = getAuthentication(userPrincipal);
        UpdatePasswordRequest updatePasswordRequest = new UpdatePasswordRequest(userPrincipal.getPassword(),userPrincipal.getPassword());

        when(passwordEncoder.matches(any(),any())).thenReturn(true);

        assertThrows(BadRequestException.class,() -> underTest.updatePassword(authentication,updatePasswordRequest));
    }

    @Test
    void updatePasswordShouldThrowBadRequestWhenRequestOldPasswordIsWrong() {
        User userPrincipal = getUserPrincipal(
                1L,
                "John",
                "Doe",
                "john@example.com",
                "USER",
                "oldPasswd",
                false
        );

        Authentication authentication = getAuthentication(userPrincipal);
        UpdatePasswordRequest updatePasswordRequest = new UpdatePasswordRequest(userPrincipal.getPassword(),"userPrincipal.getPassword()");

        when(passwordEncoder.matches(any(),any())).thenReturn(false);

        assertThrows(BadRequestException.class,() -> underTest.updatePassword(authentication,updatePasswordRequest));
    }

    @Test
    void getUserByIdShouldBeSuccessfully() {
        User expectedUser = new User(
                "firstname",
                "lastname",
                "email@email.com",
                "password"
        );

        Long id = 1L;
        UserDto expectedUserDto = new UserDto(
                expectedUser.getId(),
                expectedUser.getFirstName(),
                expectedUser.getLastName(),
                expectedUser.getEmail(),
                expectedUser.getRole(),
                expectedUser.getNeverConnected()
        );

        when(userDao.selectUserById(id)).thenReturn(Optional.of(expectedUser));
        when(userDtoMapper.apply(expectedUser)).thenReturn(expectedUserDto);

        UserDto user = underTest.getUserById(id);

        assertEquals(expectedUserDto,user);

    }

    @Test
    void getUserByIdShouldThrowNotFoundException() {
        Long id = 1L;
        when(userDao.selectUserById(id)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> underTest.getUserById(id));
    }

}
