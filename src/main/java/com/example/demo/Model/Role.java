package com.example.demo.Model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Role {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private int idRole;

    @Column(length = 20, nullable = false, unique = true)
    private String nameRole;
}
