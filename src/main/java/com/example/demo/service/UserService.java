package com.example.demo.service;

import com.example.demo.Model.User;
import com.example.demo.dto.ProfileDto;
import com.example.demo.dto.UserDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface UserService {
    Page<User> search(String keyword, Pageable pageable);
    Optional<User> findById(Long id);
    void save(UserDTO userDTO);
    void deleteById(Long id);
    long countTotalScanners();

    Optional<User> findByUsername(String username);
    void updateProfile(String username, ProfileDto profileDto, MultipartFile avatarFile);
    void changePassword(String username, String oldPassword, String newPassword);
    List<User> findByRole(String roleName);
}
