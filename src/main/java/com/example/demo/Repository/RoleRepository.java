package com.example.demo.Repository;

import com.example.demo.Model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role,Integer> {
    Role findByNameRole(String nameRole);
}
