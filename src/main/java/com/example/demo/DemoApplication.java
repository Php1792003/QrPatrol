package com.example.demo;

import com.example.demo.Model.Role;
import com.example.demo.Model.User;
import com.example.demo.Repository.RoleRepository;
import com.example.demo.Repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@SpringBootApplication
public class DemoApplication {

    // 1. Xóa @Autowired khỏi các trường. Thêm 'final' để đảm bảo bất biến.
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    // 2. Tạo một constructor để nhận các phụ thuộc
    // Spring sẽ tự động tìm và tiêm các bean vào đây
    public DemoApplication(UserRepository userRepository,
                           RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }


    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    // Bean CommandLineRunner vẫn giữ nguyên, không thay đổi
    @Bean
    CommandLineRunner run() {
        return args -> {
            if (userRepository.count() == 0) {
                System.out.println("CSDL trống, đang tạo các người dùng mẫu...");

                Role adminRole = roleRepository.findById(1)
                        .orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy quyền ROLE_ADMIN (ID=1)."));
                Role viewerRole = roleRepository.findById(2)
                        .orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy quyền ROLE_VIEWER (ID=2)."));
                Role scannerRole = roleRepository.findById(3)
                        .orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy quyền ROLE_SCANNER (ID=3)."));

                // ... (code tạo user giữ nguyên)
                // Tạo user Admin
                User adminUser = new User();
                adminUser.setUsername("admin");
                adminUser.setPassword(passwordEncoder.encode("admin123"));
                adminUser.setFullName("Quản Trị Viên");
                adminUser.setEnabled(true);
                adminUser.setRoles(Set.of(adminRole));
                userRepository.save(adminUser);

                // Tạo user Viewer
                User viewerUser = new User();
                viewerUser.setUsername("viewer");
                viewerUser.setPassword(passwordEncoder.encode("viewer123"));
                viewerUser.setFullName("Người Xem");
                viewerUser.setEnabled(true);
                viewerUser.setRoles(Set.of(viewerRole));
                userRepository.save(viewerUser);

                // Tạo user Scanner
                User scannerUser = new User();
                scannerUser.setUsername("scanner");
                scannerUser.setPassword(passwordEncoder.encode("scanner123"));
                scannerUser.setFullName("Nhân Viên Tuần Tra");
                scannerUser.setEnabled(true);
                scannerUser.setRoles(Set.of(scannerRole));
                userRepository.save(scannerUser);

                System.out.println("Đã tạo thành công 3 người dùng mẫu.");
            }
        };
    }
}