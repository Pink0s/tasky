package com.tasky.api;

import com.tasky.api.models.User;
import com.tasky.api.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@SpringBootApplication
public class ApiApplication {
    @Value("#{'${server.default-admin-password}'}")
    private String password;
    @Value("#{'${server.default-admin-account}'}")
    private String account;

    public static void main(String[] args) {
        SpringApplication.run(ApiApplication.class, args);
    }

    @Bean
    CommandLineRunner commandLineRunner(UserRepository userRepository) {

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        return args -> {
            Optional<User> userAlreadyExist = userRepository.findUserByEmail(account);

            if(userAlreadyExist.isEmpty()) {
                User user = new User(
                        "test",
                        "test",
                        account,
                        passwordEncoder.encode(password)
                );

                user.setRole("ADMIN");
                userRepository.save(user);
            }
        };
    }

}
