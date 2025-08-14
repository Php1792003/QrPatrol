package com.example.demo.Repository;

import com.example.demo.Model.Role;
import com.example.demo.Model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    @Query("SELECT COUNT(u) FROM User u JOIN u.roles r WHERE r.nameRole = :roleName")
    long countByRoleName(@Param("roleName") String roleName);

    @Query(value = "SELECT u FROM User u WHERE " +
            "(:keyword IS NULL OR LOWER(u.username) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(u.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')))",
            countQuery = "SELECT count(u) FROM User u WHERE " +
                    "(:keyword IS NULL OR LOWER(u.username) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(u.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<User> findUserPage(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.roles WHERE u IN :users")
    List<User> findUsersWithRoles(@Param("users") List<User> users);

    List<User> findByRolesContains(Role role);
}