package org.scamlet.blogmvc.blog.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Base64;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Username is required")
    @Size(min = 4, max = 36, message = "Username must be between 4 and 36 characters")
    @Column(name="user_name", nullable = false)
    private String userName;

    @NotBlank(message = "Password is required")
    @Column(name="password", nullable = false)
    private String password;

    @Email(message = "Email should be valid.")
    @Column(name="email", nullable = false)
    private String email;

    @Column(name="enabled")
    private boolean enabled = true;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();


    @Column(name="profile_picture")
    private String profilePicture;


    @Column(name="profile_background")
    private String profileBackground;


}
