package com.tasky.api.services.user;

import com.github.javafaker.Faker;
import com.tasky.api.configurations.errors.BadRequestException;
import com.tasky.api.configurations.errors.DuplicationException;
import com.tasky.api.configurations.errors.NotFoundException;
import com.tasky.api.dao.user.UserDao;
import com.tasky.api.dto.PageableDto;
import com.tasky.api.dto.user.*;
import com.tasky.api.mappers.UserDtoMapper;
import com.tasky.api.models.User;
import com.tasky.api.utilities.JwtUtility;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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
        String password = faker.internet().password();
        User user = new User(
                request.firstName(),
                request.lastName(),
                request.email(),
                passwordEncoder.encode(
                        password
                )
        );

        User createdUser = userDao.insertUser(user);

        UserDto userDto = userDtoMapper.apply(createdUser);

        return new UserRegistrationResponse(userDto,password);
    }

    /**
     * Searches for users based on the provided search criteria and returns the results along with pagination details.
     *
     * @param request The SearchUsersRequest containing the search criteria.
     * @return A SearchUsersResponse containing a list of UserDto objects and pagination details.
     * @throws BadRequestException If the request is invalid or contains incorrect parameters.
     */
    @Override
    public SearchUsersResponse searchUsers(SearchUsersRequest request) {
        int page = 0;
        int perPage = 10;
        String pattern = "";
        String type = "";
        List<String> stackTrace = new ArrayList<>();

        if(request == null) {
            type = "email";
        }

        if(request != null && request.page() != null) {
            page = request.page();
        }

        if(request != null && request.type() == null) {
            type = "email";
        } else if (request != null){
            switch (request.type()) {
                case "email" -> type = "email";
                case "firstName" -> type = "firstName";
                case "lastName" -> type = "lastName";
                default -> {
                    stackTrace.add("Invalid type");
                    throw new BadRequestException(stackTrace.toString());
                }
            }
        }


        if(request != null && request.pattern() != null) {
            pattern = request.pattern();
        }


        Pageable pageable = PageRequest.of(page,perPage);
        Page<User> pageResult = null;
        switch (type) {
            case "email" -> pageResult = userDao.selectAllUsersByEmail(pattern, pageable);
            case "firstName" -> pageResult = userDao.selectAllUsersByFirstName(pattern, pageable);
            case "lastName" -> pageResult = userDao.selectAllUsersByLastName(pattern, pageable);
        }

        if(page != 0 && (pageResult.getTotalPages()-1) < page ) {
            stackTrace.add("Page requested does not exists");
            throw new BadRequestException(stackTrace.toString());
        }

        PageableDto pageableDto = new PageableDto(
                page,
                pageResult.getTotalPages()-1 > -1 ? pageResult.getTotalPages()-1 : 0,
                pageResult.getTotalPages(),
                pageResult.getNumberOfElements()
        );

        List<UserDto> users =  pageResult
                .getContent()
                .stream()
                .map(userDtoMapper)
                .toList();

        return new SearchUsersResponse(users,pageableDto);
    }

    /**
     * @param id The unique identifier (ID) of the user to be deleted.
     */
    @Override
    public void deleteUserById(Long id) {

        userDao
                .selectUserById(id)
                .orElseThrow(
                        () -> new NotFoundException("User with id "+id+" Not Found")
                );

        userDao.deleteUserById(id);
    }

    /**
     * Updates user information based on the provided request and user ID.
     *
     * @param request The {@link UpdateUserRequest} containing the updated user information.
     * @param userId The {@link Long} representing the unique identifier (ID) of the user.
     * @return A {@link UserDto} containing the updated user information.
     * @throws NotFoundException If the user with the given ID is not found.
     * @throws BadRequestException If the request is missing a required field or contains invalid data.
     */
    @Override
    public UpdateUserResponse updateUser(UpdateUserRequest request, Long userId) {
        boolean changes = false;
        boolean passwdChanges = false;
        String passwd = null;

        User user = userDao
                .selectUserById(userId)
                .orElseThrow(
                        () -> new NotFoundException("User with id "+userId+" Not Found")
                );

        if(request == null) {
            throw new BadRequestException("Missing request body");
        }

        if(request.passwordReset() != null && request.passwordReset()) {
            Faker faker = new Faker();
            user.setNeverConnected(true);
            passwd = faker.internet().password();
            user.setPassword(passwordEncoder.encode(passwd));
            passwdChanges = true;
            changes = true;
        }

        if(request.role() != null) {
            switch (request.role()) {
                case "ADMIN", "USER", "PROJECT_MANAGER" -> {

                    if(!Objects.equals(user.getRole(), request.role())) {
                        user.setRole(request.role());
                        changes = true;
                    }

                }
                default -> {
                    List<String> stackTrace = new ArrayList<>();
                    stackTrace.add("Invalid role");
                    throw new BadRequestException(stackTrace.toString());
                }
            }
        }

        if(!changes) {
            throw new BadRequestException("No changes found");
        }

        user.setUpdatedAt(Timestamp.from(Instant.now()));

        User updatedUser = userDao.updateUser(user);

        if(passwdChanges) {
            return new UpdateUserResponse(
                    userDtoMapper.apply(updatedUser),
                    Optional.of(passwd)
            );
        }

        return new UpdateUserResponse(
                userDtoMapper.apply(updatedUser),
                Optional.empty()
        );

    }

    /**
     * Updates the user's password based on the provided request and the user's current authentication.
     *
     * @param authentication The authentication object representing the currently authenticated user.
     * @param request The {@link UpdatePasswordRequest} containing the updated password information.
     * @throws BadRequestException If the request body is missing required fields or contains invalid data.
     */
    @Override
    public void updatePassword(Authentication authentication, UpdatePasswordRequest request) {
        List<String> stackTrace = new ArrayList<>();

        if(request == null) {
            stackTrace.add("Missing request body");
            throw new BadRequestException(stackTrace.toString());
        }

        User user = (User) authentication.getPrincipal();


        if(request.oldPassword() == null) {
            stackTrace.add("Missing oldPassword");
            throw new BadRequestException(stackTrace.toString());
        }

        if(!passwordEncoder.matches(request.oldPassword(), user.getPassword())) {
            stackTrace.add("Wrong oldPassword");
            throw new BadRequestException(stackTrace.toString());
        }

        if(request.newPassword() == null) {
            stackTrace.add("Missing newPassword");
            throw new BadRequestException(stackTrace.toString());
        }

        if(request.newPassword().equals(request.oldPassword())) {
            stackTrace.add("Old and New password are equals");
            throw new BadRequestException(stackTrace.toString());
        }

        user.setPassword(passwordEncoder.encode(request.newPassword()));

        userDao.updateUser(user);
    }

    /**
     * Retrieves the profile information of the currently authenticated user.
     *
     * @param authentication The authentication object representing the current authenticated user.
     * @return A {@link UserDto} containing the profile information of the authenticated user.
     */
    @Override
    public UserDto getProfile(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return userDtoMapper.apply(user);
    }

    /**
     * Retrieves user information based on the provided user ID.
     *
     * @param id The unique identifier (ID) of the user to retrieve information for.
     * @return A {@link UserDto} containing the user information.
     * @throws NotFoundException If the user with the given ID is not found.
     */
    @Override
    public UserDto getUserById(Long id) {
        User user = userDao
                .selectUserById(id)
                .orElseThrow(() ->
                        new NotFoundException(
                            "User with id %s Not Found".formatted(id)
                        )
                );

        return userDtoMapper.apply(user);
    }


}
