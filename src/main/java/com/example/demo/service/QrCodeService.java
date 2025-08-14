package com.example.demo.service;

import com.example.demo.Model.QrCode;
import com.example.demo.Repository.QrCodeRepository;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class QrCodeService {

    @Autowired
    private QrCodeRepository qrCodeRepository;

    public List<QrCode> findAll() {
        return qrCodeRepository.findAll();
    }

    public void save(QrCode qrCode, String creatorUsername) {
        if (qrCode.getIdCode() == null) { // Thêm mới
            ZonedDateTime vietnamTime = ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));
            qrCode.setCreatedAt(vietnamTime.toLocalDateTime());
            qrCode.setCreatedBy(creatorUsername);
        }
        qrCodeRepository.save(qrCode);
    }

    public Optional<QrCode> findById(UUID id) {
        return qrCodeRepository.findById(id);
    }

    public void deleteById(UUID id) {
        qrCodeRepository.deleteById(id);
    }

    public Page<QrCode> search(String keyword, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        if (keyword != null && keyword.trim().isEmpty()) {
            keyword = null;
        }

        if (endDate != null) {
            endDate = endDate.with(java.time.LocalTime.MAX);
        }

        return qrCodeRepository.search(keyword, startDate, endDate, pageable);
    }

    public byte[] generateQrCodeImage(String text, int width, int height) throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);
        ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
        return pngOutputStream.toByteArray();
    }

    public long countTotal() {
        return qrCodeRepository.count();
    }

    public List<UUID> findAllIdsBySearchCriteria(String keyword, LocalDateTime startDate, LocalDateTime endDate) {
        return qrCodeRepository.findIdsBySearchCriteria(keyword, startDate, endDate);
    }

    public byte[] createZipFromQrCodes(List<UUID> ids) throws IOException, WriterException {
        // Sử dụng ByteArrayOutputStream để tạo file ZIP trong bộ nhớ
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(baos)) {

            for (UUID id : ids) {
                Optional<QrCode> qrCodeOpt = qrCodeRepository.findById(id);
                if (qrCodeOpt.isPresent()) {
                    QrCode qrCode = qrCodeOpt.get();

                    // Tạo ảnh QR
                    byte[] qrImageBytes = generateQrCodeImage(qrCode.getIdCode().toString(), 300, 300);

                    String safeFileName = qrCode.getLocationName()
                            .replaceAll("[^a-zA-Z0-9\\.\\-]", "_")
                            .replaceAll(" ", "_")
                            + ".png";

                    ZipEntry zipEntry = new ZipEntry(safeFileName);
                    zos.putNextEntry(zipEntry);

                    zos.write(qrImageBytes);
                    zos.closeEntry();
                }
            }
        }
        return baos.toByteArray();
    }
}