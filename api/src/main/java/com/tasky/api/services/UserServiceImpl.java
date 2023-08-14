package com.tasky.api.services;

import com.github.javafaker.Faker;
import com.tasky.api.configurations.errors.BadRequestException;
import com.tasky.api.configurations.errors.DuplicationException;
import com.tasky.api.dao.UserDao;
import com.tasky.api.dto.user.*;
import com.tasky.api.mappers.UserDtoMapper;
import com.tasky.api.models.User;
import com.tasky.api.utilities.JwtUtility;
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the UserService interface, providing user-related operations.
 */
@Service
public class UserServiceImpl implements UserService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtility jwtUtility;
    private final UserDtoMapper userDtoMapper;
    private final UserDao userDao;

    private final PasswordEncoder passwordEncoder;

    /**
     * Constructs a UserServiceImpl with the necessary dependencies.
     *
     * @param authenticationManager The AuthenticationManager implementation.
     * @param jwtUtility The JwtUtility implementation for token management.
     * @param userDtoMapper The UserDtoMapper for mapping User entities to DTOs.
     * @param userDao The UserDao implementation to use for manipulating user data.
     * @param passwordEncoder The passwordEncoder for perform password encoding.
     */
    public UserServiceImpl(AuthenticationManager authenticationManager, JwtUtility jwtUtility, UserDtoMapper userDtoMapper, UserDao userDao, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtUtility = jwtUtility;
        this.userDtoMapper = userDtoMapper;
        this.userDao = userDao;
        this.passwordEncoder = passwordEncoder;
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

    /**
     * @param request The UserRegistrationRequest containing user details.
     * @return A UserRegistrationResponse containing the user id.
     *
     */
    @Override
    public UserRegistrationResponse userRegistration(@Nullable UserRegistrationRequest request) {

        List<String> stackTrace = new ArrayList<>();
        boolean isBadRequest = false;

        if(request == null) {
            stackTrace.add("Missing Field firstName");
            stackTrace.add("Missing Field lastName");
            stackTrace.add("Missing Field email");
            throw new BadRequestException(stackTrace.toString());
        }

        if(request.firstName() == null ) {
            stackTrace.add("Missing Field firstName");
            isBadRequest=true;
        }

        if(request.lastName() == null) {
            stackTrace.add("Missing Field lastName");
            isBadRequest=true;
        }

        if(request.email() == null) {
            stackTrace.add("Missing Field email");
            isBadRequest=true;
        }

        if(isBadRequest) {
            throw new BadRequestException(stackTrace.toString());
        }

        if(userDao.isUserExists(request.email())) {
            throw new DuplicationException("Email already in use");
        }

        Faker faker = new Faker();

        User user = new User(
                request.firstName(),
                request.lastName(),
                request.email(),
                passwordEncoder.encode(
                        faker.internet().password()
                )
        );

        User createdUser = userDao.insertUser(user);

        UserDto userDto = userDtoMapper.apply(createdUser);

        return new UserRegistrationResponse(userDto);
    }

    /**
     * @param request The {@link SearchUsersRequest} containing the search criteria.
     * @return
     */
    @Override
    public SearchUsersResponse searchUsers(SearchUsersRequest request) {
        return null;
    }

    /**
     * @param id The unique identifier (ID) of the user to be deleted.
     */
    @Override
    public void deleteUserById(Long id) {

    }

    /**
     * @param request The {@link UpdateUserRequest} containing the updated user information.
     * @param userId The {@link Long} containing the id of user.
     * @return
     */
    @Override
    public UserDto updateUser(UpdateUserRequest request, Long userId) {
        return null;
    }

    /**
     * @param request The {@link UpdatePasswordRequest} containing the updated password information.
     */
    @Override
    public void updatePassword(Authentication authentication, UpdatePasswordRequest request) {

    }

    /**
     * @param authentication The authentication object representing the current authenticated user.
     * @return
     */
    @Override
    public UserDto getProfile(Authentication authentication) {
        return null;
    }

    /**
     * @param id The unique identifier (ID) of the use retrieve information
     * @return
     */
    @Override
    public UserDto getUserById(Long id) {
        return null;
    }


}
