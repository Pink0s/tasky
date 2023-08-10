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
    @Column(nullable = false) private String first_name;
    @Column(nullable = false) private String last_name;
    @Column(nullable = false) private String email;
    @Column(nullable = false) private String password;
    @Column(nullable = false) private Boolean never_connected;
    @Column(nullable = false) private String role;
    @Column(nullable = false) private Timestamp created_at;
    @Column(nullable = false) private Timestamp updated_at;

    /**
     * Creates a user with the given parameters.
     *
     * @param first_name The first name of the user.
     * @param last_name The last name of the user.
     * @param email The email address of the user.
     * @param password The password of the user.
     */
    public User(String first_name, String last_name, String email, String password) {
        Timestamp timestamp = Timestamp.from(Instant.now());
        this.first_name = first_name;
        this.last_name = last_name;
        this.email = email;
        this.password = password;
        this.never_connected = true;
        this.role = "USER";
        this.created_at = timestamp;
        this.updated_at = timestamp;
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
