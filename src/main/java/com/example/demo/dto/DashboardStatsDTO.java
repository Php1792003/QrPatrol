package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DashboardStatsDTO {
    private long totalLogsToday;    // Tổng lượt quét trong ngày
    private long totalQrCodes;      // Tổng số vị trí (mã QR)
    private long totalScanners;     // Tổng số nhân viên quét
}