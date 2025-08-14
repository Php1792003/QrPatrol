// --- START OF FILE UserServiceImpl.java ---
package com.example.demo.service.impl;

import com.example.demo.Model.Role;
import com.example.demo.Model.User;
import com.example.demo.Repository.RoleRepository;
import com.example.demo.Repository.UserRepository;
import com.example.demo.dto.ProfileDto;
import com.example.demo.dto.UserDTO;
import com.example.demo.service.FileStorageService;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final FileStorageService fileStorageService;

    @Value("${file.upload-dir.avatars}")
    private String avatarUploadDir;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, FileStorageService fileStorageService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.fileStorageService = fileStorageService;
    }

    @Override
    public Page<User> search(String keyword, Pageable pageable) {
        if (keyword != null && keyword.trim().isEmpty()) {
            keyword = null;
        }

        Page<User> userPage = userRepository.findUserPage(keyword, pageable);

        List<User> usersWithRoles = userRepository.findUsersWithRoles(userPage.getContent());

        return new PageImpl<>(usersWithRoles, userPage.getPageable(), userPage.getTotalElements());
    }

    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public void save(UserDTO userDTO) {
        User user = userRepository.findById(Optional.ofNullable(userDTO.getId()).orElse(-1L))
                .orElse(new User());

        user.setUsername(userDTO.getUsername());
        user.setFullName(userDTO.getFullName());
        user.setEnabled(userDTO.isEnabled());

        if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        }

        if (userDTO.getRoleIds() != null && !userDTO.getRoleIds().isEmpty()) {
            Set<Role> roles = new HashSet<>(roleRepository.findAllById(userDTO.getRoleIds()));
            user.setRoles(roles);
        }

        userRepository.save(user);
    }

    @Override
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public long countTotalScanners() {
        return userRepository.countByRoleName("ROLE_SCANNER");
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public void updateProfile(String username, ProfileDto profileDto, MultipartFile avatarFile) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        user.setFullName(profileDto.getFullName());

        if (avatarFile != null && !avatarFile.isEmpty()) {
            // Xóa file avatar cũ nếu có
            if (user.getAvatar() != null && !user.getAvatar().isEmpty()) {
                fileStorageService.deleteFile(user.getAvatar(), avatarUploadDir);
            }
            // Lưu file avatar mới
            String fileExtension = StringUtils.getFilenameExtension(avatarFile.getOriginalFilename());
            String newFileName = "user-" + user.getIdUser() + "-" + System.currentTimeMillis() + "." + fileExtension;
            fileStorageService.storeFile(avatarFile, newFileName, avatarUploadDir);
            user.setAvatar(newFileName);
        }
        userRepository.save(user);
    }

    @Override
    public void changePassword(String username, String oldPassword, String newPassword) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("Mật khẩu cũ không chính xác.");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Override
    public List<User> findByRole(String roleName) {
        Role role = roleRepository.findByNameRole(roleName);

        if (role == null) {
            return Collections.emptyList();
        }

        return userRepository.findByRolesContains(role);
    }
}
