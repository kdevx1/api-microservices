package com.devx.auth.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.devx.auth.service.ReportService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    // =====================
    // EXCEL
    // =====================
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/users/excel")
    public ResponseEntity<byte[]> usersExcel() throws Exception {
        byte[] data = reportService.exportUsersExcel();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=usuarios.xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(data);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/services/excel")
    public ResponseEntity<byte[]> servicesExcel() throws Exception {
        byte[] data = reportService.exportServicesExcel();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=servicos.xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(data);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/orders/excel")
    public ResponseEntity<byte[]> ordersExcel() throws Exception {
        byte[] data = reportService.exportOrdersExcel();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=ordens.xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(data);
    }

    // =====================
    // PDF
    // =====================
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/users/pdf")
    public ResponseEntity<byte[]> usersPdf() throws Exception {
        byte[] data = reportService.exportUsersPdf();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=usuarios.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(data);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/services/pdf")
    public ResponseEntity<byte[]> servicesPdf() throws Exception {
        byte[] data = reportService.exportServicesPdf();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=servicos.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(data);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/orders/pdf")
    public ResponseEntity<byte[]> ordersPdf() throws Exception {
        byte[] data = reportService.exportOrdersPdf();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=ordens.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(data);
    }
}