package com.example.demo.Repository;

import com.example.demo.Model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role,Integer> {
    Optional<Role> findByNameRole(String nameRole);
}
