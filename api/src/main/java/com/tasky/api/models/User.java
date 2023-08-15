package com.tasky.api.models;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Collection;
import java.util.List;

/**
 * Represents a user account in the system.
 */
@Entity @Data @NoArgsConstructor
@Table(
        name = "user_account",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "user_email_unique",
                        columnNames = "email"
                )
        }
)

public class User implements UserDetails {

    @SequenceGenerator(
            name= "user_account_id_seq",
            sequenceName = "user_account_id_seq",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "user_account_id_seq"
    )
    @Id private Long id;
    @Column(nullable = false) private String firstName;
    @Column(nullable = false) private String lastName;
    @Column(nullable = false) private String email;
    @Column(nullable = false) private String password;
    @Column(nullable = false) private Boolean neverConnected;
    @Column(nullable = false) private String role;
    @Column(nullable = false) private Timestamp createdAt;
    @Column(nullable = false) private Timestamp updatedAt;

    /**
     * Creates a user with the given parameters.
     *
     * @param firstName The first name of the user.
     * @param lastName The last name of the user.
     * @param email The email address of the user.
     * @param password The password of the user.
     */
    public User(String firstName, String lastName, String email, String password) {
        Timestamp timestamp = Timestamp.from(Instant.now());
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.neverConnected = true;
        this.role = "USER";
        this.createdAt = timestamp;
        this.updatedAt = timestamp;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_"+role));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
