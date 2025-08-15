package com.example.demo.controller;

import com.example.demo.Model.*;
import com.example.demo.Repository.RoleRepository;
import com.example.demo.Repository.TrialAccountRepository;
import com.example.demo.dto.UserDTO;
import com.example.demo.service.*;
import com.google.zxing.WriterException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final QrCodeService qrCodeService;
    private final PatrolService patrolService;
    private final RoleRepository roleRepository;
    private final UserService userService;
    private final VehicleViolationService violationService;
    @Autowired
    private TrialAccountRepository trialAccountRepository;
    @Autowired
    private TrialAccountService trialAccountService;

    @Autowired
    public AdminController(QrCodeService qrCodeService, PatrolService patrolService, RoleRepository roleRepository,
                           UserService userService, VehicleViolationService violationService,
                           TrialAccountRepository trialAccountRepository, TrialAccountService trialAccountService) {
        this.qrCodeService = qrCodeService;
        this.patrolService = patrolService;
        this.roleRepository = roleRepository;
        this.userService = userService;
        this.violationService = violationService;
        this.trialAccountRepository = trialAccountRepository;
        this.trialAccountService = trialAccountService;
    }

    @GetMapping("/users")
    public String listUsers(@RequestParam(required = false) String keyword, @RequestParam(defaultValue = "0") int page, Model model) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by("fullName").ascending());
        Page<User> userPage = userService.search(keyword, pageable);

        model.addAttribute("userPage", userPage);
        model.addAttribute("keyword", keyword);
        return "admin/users_list";
    }

    @GetMapping("/users/new")
    public String showNewUserForm(Model model) {
        model.addAttribute("userDTO", new UserDTO());
        model.addAttribute("allRoles", roleRepository.findAll());
        model.addAttribute("pageTitle", "Thêm Người dùng mới");
        return "admin/user_form";
    }

    @GetMapping("/users/edit/{id}")
    public String showEditUserForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<User> userOpt = userService.findById(id);
        if (userOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy người dùng với ID: " + id);
            return "redirect:/admin/users";
        }
        User user = userOpt.get();
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getIdUser());
        userDTO.setUsername(user.getUsername());
        userDTO.setFullName(user.getFullName());
        userDTO.setEnabled(user.isEnabled());
        userDTO.setRoleIds(user.getRoles().stream().map(Role::getIdRole).collect(Collectors.toSet()));

        model.addAttribute("userDTO", userDTO);
        model.addAttribute("allRoles", roleRepository.findAll());
        model.addAttribute("pageTitle", "Chỉnh sửa Người dùng");
        return "admin/user_form";
    }

    @PostMapping("/users/save")
    public String saveUser(@ModelAttribute UserDTO userDTO, RedirectAttributes redirectAttributes) {
        userService.save(userDTO);
        redirectAttributes.addFlashAttribute("successMessage", "Đã lưu người dùng thành công!");
        return "redirect:/admin/users";
    }

    @GetMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        userService.deleteById(id);
        redirectAttributes.addFlashAttribute("successMessage", "Đã xóa người dùng thành công!");
        return "redirect:/admin/users";
    }

    @GetMapping("/qrcodes")
    public String listQrCodes(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            Model model) {

        LocalDateTime startDateTime = (startDate != null) ? startDate.atStartOfDay() : null;
        LocalDateTime endDateTime = (endDate != null) ? endDate.atTime(LocalTime.MAX) : null;
        Pageable pageable = PageRequest.of(page, 8, Sort.by("locationName").ascending());

        Page<QrCode> qrCodePage = qrCodeService.search(keyword, startDateTime, endDateTime, pageable);

        System.out.println("=== DEBUG QR CODES PAGE ===");
        System.out.println("Has content: " + qrCodePage.hasContent());

        if (qrCodePage.hasContent()) {
            Object firstItem = qrCodePage.getContent().get(0);
            System.out.println("First item actual type: " + firstItem.getClass().getName());
            System.out.println("First item: " + firstItem);

            // Kiểm tra có phải là QrCode không
            if (firstItem instanceof QrCode) {
                System.out.println("✓ Object is QrCode");
                QrCode qr = (QrCode) firstItem;
                System.out.println("idCode: " + qr.getIdCode());
            } else if (firstItem instanceof PatrolLog) {
                System.out.println("✗ ERROR: Object is PatrolLog!");
                PatrolLog log = (PatrolLog) firstItem;
                System.out.println("idPatrolLog: " + log.getIdPatrolLog());
            } else {
                System.out.println("✗ ERROR: Unknown object type!");
            }
        }
        System.out.println("========================");

        model.addAttribute("qrCodePage", qrCodePage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);

        return "admin/qrcodes_list";
    }

    @GetMapping("/qrcodes/new")
    public String showNewQrCodeForm(Model model) {
        model.addAttribute("qrcode", new QrCode());
        model.addAttribute("pageTitle", "Tạo Mã QR Mới");
        return "admin/qrcode_form";
    }

    @GetMapping("/qrcodes/edit/{id}")
    public String showEditQrCodeForm(@PathVariable("id") UUID id, Model model) {
        model.addAttribute("qrcode", qrCodeService.findById(id));
        model.addAttribute("pageTitle", "Chỉnh Sửa Mã QR");
        return "admin/qrcode_form";
    }

    @PostMapping("/qrcodes/save")
    public String saveQrCode(@ModelAttribute QrCode qrCode, @AuthenticationPrincipal UserDetails userDetails, RedirectAttributes redirectAttributes) {
        qrCodeService.save(qrCode, userDetails.getUsername());
        redirectAttributes.addFlashAttribute("successMessage", "Đã lưu mã QR thành công!");
        return "redirect:/admin/qrcodes";
    }

    @GetMapping("/qrcodes/delete/{id}")
    public String deleteQrCode(@PathVariable("id") UUID id, RedirectAttributes redirectAttributes) {
        qrCodeService.deleteById(id);
        redirectAttributes.addFlashAttribute("successMessage", "Đã xóa mã QR thành công!");
        return "redirect:/admin/qrcodes";
    }

    @GetMapping("/qrcodes/image/{id}")
    @ResponseBody
    public ResponseEntity<byte[]> getQrCodeImage(@PathVariable("id") UUID id) throws IOException, WriterException {
        String qrContent = id.toString();
        byte[] image = qrCodeService.generateQrCodeImage(qrContent, 250, 250);
        return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(image);
    }

    @PostMapping("/qrcodes/download-zip")
    public ResponseEntity<byte[]> downloadSelectedQrCodes(@RequestBody List<String> idStrings) {
        try {
            List<UUID> ids = idStrings.stream().map(UUID::fromString).collect(Collectors.toList());
            if (ids.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            byte[] zipBytes = qrCodeService.createZipFromQrCodes(ids);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "qrcodes.zip");
            return new ResponseEntity<>(zipBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/qrcodes/download-all-zip")
    public ResponseEntity<byte[]> downloadAllMatchingQrCodes(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        try {
            LocalDateTime startDateTime = (startDate != null) ? startDate.atStartOfDay() : null;
            LocalDateTime endDateTime = (endDate != null) ? endDate.atTime(LocalTime.MAX) : null;
            List<UUID> allIds = qrCodeService.findAllIdsBySearchCriteria(keyword, startDateTime, endDateTime);
            if (allIds.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            byte[] zipBytes = qrCodeService.createZipFromQrCodes(allIds);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "all_qrcodes.zip");
            return new ResponseEntity<>(zipBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/logs")
    public String showPatrolLogs(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            Model model) {

        LocalDateTime startDateTime = (startDate != null) ? startDate.atStartOfDay() : null;
        LocalDateTime endDateTime = (endDate != null) ? endDate.atTime(LocalTime.MAX) : null;

        Pageable pageable = PageRequest.of(page, 8);

        Page<PatrolLog> logPage = patrolService.searchLogs(keyword, startDateTime, endDateTime, pageable);

        model.addAttribute("logPage", logPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);

        return "admin/logs_list";
    }

    @GetMapping("/logs/edit/{id}")
    public String showEditLogForm(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<PatrolLog> patrolLog = patrolService.findLogById(id);
        if (patrolLog.isPresent()) {
            model.addAttribute("patrolLog", patrolLog.get());
            model.addAttribute("pageTitle", "Chỉnh sửa Nhật ký");
            return "admin/log_form";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy nhật ký với ID: " + id);
            return "redirect:/admin/logs";
        }
    }

    @PostMapping("/logs/save")
    public String saveLog(@ModelAttribute("patrolLog") PatrolLog patrolLogFromForm,
                          RedirectAttributes redirectAttributes) {
        try {
            boolean isNewLog = patrolLogFromForm.getIdPatrolLog() == null;

            if (isNewLog) {
                if (patrolLogFromForm.getQrCodeId() == null || patrolLogFromForm.getUserId() == null) {
                    redirectAttributes.addFlashAttribute("errorMessage", "Vui lòng chọn cả Vị trí và Người quét.");
                    return "redirect:/admin/logs/new";
                }
                // Điền tên vị trí và người quét
                QrCode qrCode = qrCodeService.findById(patrolLogFromForm.getQrCodeId())
                        .orElseThrow(() -> new IllegalArgumentException("Invalid QR Code ID"));
                patrolLogFromForm.setLocationName(qrCode.getLocationName());

                User user = userService.findById(patrolLogFromForm.getUserId())
                        .orElseThrow(() -> new IllegalArgumentException("Invalid User ID"));
                patrolLogFromForm.setScannerName(user.getFullName());

                patrolService.saveLog(patrolLogFromForm);

            } else {
                PatrolLog existingLog = patrolService.findLogById(patrolLogFromForm.getIdPatrolLog())
                        .orElseThrow(() -> new IllegalArgumentException("Invalid Log ID For Update"));

                existingLog.setScannedAt(patrolLogFromForm.getScannedAt());

                patrolService.saveLog(existingLog);
            }

            String message = isNewLog ? "Thêm nhật ký thủ công thành công!" : "Cập nhật nhật ký thành công!";
            redirectAttributes.addFlashAttribute("successMessage", message);

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Đã có lỗi xảy ra: " + e.getMessage());
        }

        return "redirect:/admin/logs";
    }

    @GetMapping("/logs/delete/{id}")
    public String deleteLog(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            patrolService.deleteLogById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Đã xóa nhật ký thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi xóa nhật ký!");
        }
        return "redirect:/admin/logs";
    }

    @GetMapping("/logs/new")
    public String showAddLogForm(Model model) {
        PatrolLog newLog = new PatrolLog();
        newLog.setScannedAt(LocalDateTime.now());

        model.addAttribute("patrolLog", newLog);
        model.addAttribute("pageTitle", "Thêm Nhật ký Thủ công");

        model.addAttribute("allQrCodes", qrCodeService.findAll());
        model.addAttribute("allScanners", userService.findByRole("SCANNER"));

        return "admin/log_form";
    }

    @GetMapping("/violations")
    public String listViolations(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size,
            Model model) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("violationDate").descending());

        Page<VehicleViolation> violationPage = violationService.findAll(pageable);

        model.addAttribute("violationPage", violationPage);
        model.addAttribute("keyword", keyword);
        return "admin/violations_list";
    }

    @GetMapping("/violations/add")
    public String showAddForm(Model model) {
        model.addAttribute("violation", new VehicleViolation());
        model.addAttribute("pageTitle", "Thêm Vi phạm Mới");
        return "admin/violation_form";
    }

    @GetMapping("/violations/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<VehicleViolation> violationOpt = violationService.findById(id);
        if (violationOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy vi phạm với ID: " + id);
            return "redirect:/admin/violations";
        }
        model.addAttribute("violation", violationOpt.get());
        model.addAttribute("pageTitle", "Chỉnh sửa Vi phạm");
        return "admin/violation_form";
    }

    @PostMapping("/violations/save")
    public String saveViolation(@ModelAttribute("violation") VehicleViolation violation,
                                @RequestParam("imageFile") MultipartFile imageFile,
                                Authentication authentication,
                                RedirectAttributes redirectAttributes) {
        try {
            violationService.saveViolation(violation, imageFile, authentication.getName());
            redirectAttributes.addFlashAttribute("successMessage", "Lưu thông tin vi phạm thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Đã xảy ra lỗi: " + e.getMessage());
        }
        return "redirect:/admin/violations";
    }

    @GetMapping("/violations/delete/{id}")
    public String deleteViolation(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            violationService.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Xóa vi phạm thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: không thể xóa vi phạm.");
        }
        return "redirect:/admin/violations";
    }

    @GetMapping("/trials/active")
    public String listActiveTrials(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String daysLeft,
            @RequestParam(required = false) String companySize,
            @RequestParam(defaultValue = "false") boolean highActivity,
            @RequestParam(defaultValue = "false") boolean expiringSoon,
            @RequestParam(defaultValue = "0") int page,
            Model model) {

        Pageable pageable = PageRequest.of(page, 20, Sort.by("createdAt").descending());

        Page<TrialAccount> activeTrialsPage = trialAccountService.findActiveTrials(
                keyword, daysLeft, companySize, highActivity, expiringSoon, pageable
        );

        Map<String, Long> stats = trialAccountService.getActiveStats();

        model.addAllAttributes(stats);
        model.addAttribute("activeTrialsPage", activeTrialsPage);

        model.addAttribute("keyword", keyword);
        model.addAttribute("daysLeft", daysLeft);
        model.addAttribute("companySize", companySize);
        model.addAttribute("highActivity", highActivity);
        model.addAttribute("expiringSoon", expiringSoon);

        model.addAttribute("currentPage", "admin_trials");
        return "admin/trials_list";
    }
    @GetMapping("/trials/expired")
    public String listExpiredTrials(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            Model model) {

        Pageable pageable = PageRequest.of(page, 20, Sort.by("expiresAt").descending());

        Page<TrialAccount> expiredTrialsPage = trialAccountService.findInactiveTrials(keyword, status, pageable);
        Map<String, Long> stats = trialAccountService.getExpiredStats();

        model.addAttribute("expiredTrialsPage", expiredTrialsPage);
        model.addAllAttributes(stats);

        model.addAttribute("keyword", keyword);
        model.addAttribute("status", status); // Truyền status cho filter

        model.addAttribute("currentPage", "admin_trials_expired");
        return "admin/trials_expired";
    }

    @GetMapping("/trials/edit/{id}")
    public String showEditTrialForm(@PathVariable Long id, Model model, RedirectAttributes ra) {
        Optional<TrialAccount> trialOpt = trialAccountRepository.findById(id);
        if (trialOpt.isEmpty()){
            ra.addFlashAttribute("errorMessage", "Không tìm thấy tài khoản dùng thử ID: " + id);
            return "redirect:/admin/trials";
        }
        model.addAttribute("trial", trialOpt.get());
        model.addAttribute("pageTitle", "Chỉnh sửa tài khoản dùng thử");
        model.addAttribute("currentPage", "admin_trials");
        // Giả sử bạn có file form riêng tên là "trial_form.html"
        return "admin/trial_form";
    }

    @PostMapping("/trials/delete/{id}")
    public String deleteTrial(@PathVariable Long id, RedirectAttributes ra) {
        try {
            trialAccountService.deleteTrialAccountById(id);
            ra.addFlashAttribute("successMessage", "Xóa tài khoản thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Lỗi khi xóa tài khoản: " + e.getMessage());
        }
        return "redirect:/admin/trials";
    }

    @PostMapping("/trials/extend/{id}")
    public String extendTrial(@PathVariable Long id, @RequestParam int days, RedirectAttributes ra) {
        try {
            TrialAccount trial = trialAccountService.extendTrial(id, days);
            ra.addFlashAttribute("successMessage", "Gia hạn thành công " + days + " ngày cho tài khoản " + trial.getEmail());
        } catch (RuntimeException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/trials";
    }
}