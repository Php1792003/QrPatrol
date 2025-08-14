package com.example.demo.Model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Nationalized;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idUser;

    @Column(length = 50, nullable = false, unique = true)
    private String username;

    @Column(length = 255, nullable = false)
    private String password;

    @Nationalized
    @Column(name = "full_name",length = 100, nullable = false)
    private String fullName;

    private boolean enabled;

    @Column(length = 255, nullable = true)
    private String avatar;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();
}
