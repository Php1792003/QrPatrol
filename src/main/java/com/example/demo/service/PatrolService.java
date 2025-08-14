package com.example.demo.service;

import com.example.demo.Model.PatrolLog;
import com.example.demo.Model.QrCode;
import com.example.demo.Model.User;
import com.example.demo.Repository.PatrolLogRepository;
import com.example.demo.Repository.QrCodeRepository;
import com.example.demo.dto.HourlyScanStatsDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class PatrolService {
    @Autowired
    private PatrolLogRepository patrolLogRepository;
    @Autowired
    private QrCodeRepository qrCodeRepository;

    public Optional<QrCode> recordScan(String qrCodeIdStr, User scanner) {
        UUID qrCodeId;
        try {
            qrCodeId = UUID.fromString(qrCodeIdStr);
        } catch (IllegalArgumentException e) {
            return Optional.empty(); // ID không hợp lệ
        }

        Optional<QrCode> qrCodeOpt = qrCodeRepository.findById(qrCodeId);
        if (qrCodeOpt.isPresent()) {
            QrCode qrCode = qrCodeOpt.get();
            PatrolLog log = new PatrolLog();
            log.setQrCodeId(qrCode.getIdCode());
            log.setUserId(scanner.getIdUser());
            log.setScannerName(scanner.getFullName());
            log.setLocationName(qrCode.getLocationName());

            ZonedDateTime vietnamTime = ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));
            log.setScannedAt(vietnamTime.toLocalDateTime());
            patrolLogRepository.save(log);
        }
        return qrCodeOpt;
    }

    public List<PatrolLog> findAllLogs() {
        return patrolLogRepository.findAll(Sort.by(Sort.Direction.DESC, "scannedAt"));
    }

    public Page<PatrolLog> searchLogs(String keyword, LocalDateTime startDateTime, LocalDateTime endDateTime, Pageable pageable) {
        if (keyword != null && keyword.trim().isEmpty()) {
            keyword = null;
        }
        return patrolLogRepository.search(keyword, startDateTime, endDateTime, pageable );
    }

    public Optional<PatrolLog> findLogById(Long id) {
        return patrolLogRepository.findById(id);
    }

    public void saveLog(PatrolLog patrolLog) {
        patrolLogRepository.save(patrolLog);
    }

    public void deleteLogById(Long id) {
        patrolLogRepository.deleteById(id);
    }

    public long countLogsToday() {
        LocalDateTime startOfDay = LocalDateTime.now().with(LocalTime.MIN); // 00:00:00 hôm nay
        LocalDateTime endOfDay = LocalDateTime.now().with(LocalTime.MAX);   // 23:59:59 hôm nay
        return patrolLogRepository.countByScannedAtBetween(startOfDay, endOfDay);
    }

    public List<PatrolLog> findTop5RecentLogs() {
        return patrolLogRepository.findTop5ByOrderByScannedAtDesc();
    }

    public HourlyScanStatsDTO getHourlyScanStatsForToday() {
        List<Map<String, Object>> results = patrolLogRepository.countScansByHourToday();

        Map<Integer, Long> scansByHourMap = results.stream()
                .collect(Collectors.toMap(
                        row -> (Integer) row.get("hour_of_day"),
                        row -> ((Number) row.get("scan_count")).longValue()
                ));

        List<String> labels = IntStream.range(0, 24)
                .mapToObj(hour -> hour + "h")
                .collect(Collectors.toList());

        List<Long> data = IntStream.range(0, 24)
                .mapToLong(hour -> scansByHourMap.getOrDefault(hour, 0L))
                .boxed()
                .collect(Collectors.toList());

        return new HourlyScanStatsDTO(labels, data);
    }
}