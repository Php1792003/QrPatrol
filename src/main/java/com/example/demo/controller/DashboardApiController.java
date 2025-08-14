package com.example.demo.controller;

import com.example.demo.Model.PatrolLog;
import com.example.demo.dto.DashboardStatsDTO;
import com.example.demo.dto.HourlyScanStatsDTO;
import com.example.demo.service.PatrolService;
import com.example.demo.service.QrCodeService;
import com.example.demo.service.impl.UserServiceImpl; // Import UserService
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List; // Import List

@RestController
@RequestMapping("/api/dashboard")
public class DashboardApiController {

    @Autowired
    private PatrolService patrolService;

    @Autowired
    private QrCodeService qrCodeService;

    @Autowired
    private UserServiceImpl userService; // Inject UserService

    @GetMapping("/stats")
    public DashboardStatsDTO getStats() {
        long totalLogsToday = patrolService.countLogsToday();
        long totalQrCodes = qrCodeService.countTotal();
        long totalScanners = userService.countTotalScanners();

        return new DashboardStatsDTO(totalLogsToday, totalQrCodes, totalScanners);
    }

    @GetMapping("/recent-logs")
    public List<PatrolLog> getRecentLogs() {
        return patrolService.findTop5RecentLogs();
    }

    @GetMapping("/scans-by-hour")
    public HourlyScanStatsDTO getScansByHour() {
        return patrolService.getHourlyScanStatsForToday();
    }
}