package com.tasky.api.services.user;

import com.tasky.api.dao.user.UserDao;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Custom UserDetailsService implementation for loading user details from the database.
 */
@Service
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

    private final UserDao userDao;

    /**
     * Constructs a UserDetailsService with the specified UserDao.
     *
     * @param userDao The UserDao implementation to use for retrieving user data.
     */
    public UserDetailsService(@Qualifier("JPA") UserDao userDao) {
        this.userDao = userDao;
    }

    /**
     * Load user details by the given username (email).
     *
     * @param username The email address of the user to retrieve details for.
     * @return UserDetails object containing the user's details.
     * @throws UsernameNotFoundException if the user with the given email is not found.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userDao.selectUserByEmail(username).orElseThrow(
                () -> new UsernameNotFoundException("User with email " + username + " not found")
        );
    }
}
