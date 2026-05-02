package com.devx.auth.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import com.devx.auth.domain.ServiceOrder;
import com.devx.auth.domain.User;
import com.devx.auth.repository.ServiceOrderRepository;
import com.devx.auth.repository.ServiceRepository;
import com.devx.auth.repository.UserRepository;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final UserRepository userRepository;
    private final ServiceRepository serviceRepository;
    private final ServiceOrderRepository serviceOrderRepository;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // =====================
    // EXCEL
    // =====================
    public byte[] exportUsersExcel() throws IOException {
        List<User> users = userRepository.findAll();
        try (Workbook wb = new XSSFWorkbook()) {
            Sheet sheet = wb.createSheet("Usuários");
            String[] headers = { "ID", "Nome", "E-mail", "Perfil", "Status", "Criado em", "Último Login" };
            createExcelSheet(wb, sheet, headers);

            int rowNum = 1;
            for (User u : users) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(u.getId());
                row.createCell(1).setCellValue(u.getName());
                row.createCell(2).setCellValue(u.getEmail());
                row.createCell(3).setCellValue(u.getRole().name());
                row.createCell(4).setCellValue(Boolean.TRUE.equals(u.getActive()) ? "Ativo" : "Inativo");
                row.createCell(5).setCellValue(u.getCreatedAt() != null ? u.getCreatedAt().format(FMT) : "");
                row.createCell(6).setCellValue(u.getLastLoginAt() != null ? u.getLastLoginAt().format(FMT) : "—");
            }

            for (int i = 0; i < headers.length; i++) sheet.autoSizeColumn(i);
            return toBytes(wb);
        }
    }

    public byte[] exportServicesExcel() throws IOException {
        List<com.devx.auth.domain.Service> services = serviceRepository.findAll();
        try (Workbook wb = new XSSFWorkbook()) {
            Sheet sheet = wb.createSheet("Serviços");
            String[] headers = { "ID", "Nome", "Categoria", "Preço", "Duração (min)", "Tipo", "Status", "Criado em" };
            createExcelSheet(wb, sheet, headers);

            int rowNum = 1;
            for (com.devx.auth.domain.Service s : services) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(s.getId());
                row.createCell(1).setCellValue(s.getName());
                row.createCell(2).setCellValue(s.getCategory().getName());
                row.createCell(3).setCellValue(s.getPrice().doubleValue());
                row.createCell(4).setCellValue(s.getDurationMinutes() != null ? s.getDurationMinutes() : 0);
                row.createCell(5).setCellValue(s.getType().name());
                row.createCell(6).setCellValue(Boolean.TRUE.equals(s.getActive()) ? "Ativo" : "Inativo");
                row.createCell(7).setCellValue(s.getCreatedAt() != null ? s.getCreatedAt().format(FMT) : "");
            }

            for (int i = 0; i < headers.length; i++) sheet.autoSizeColumn(i);
            return toBytes(wb);
        }
    }

    public byte[] exportOrdersExcel() throws IOException {
        List<ServiceOrder> orders = serviceOrderRepository.findAll();
        try (Workbook wb = new XSSFWorkbook()) {
            Sheet sheet = wb.createSheet("Ordens");
            String[] headers = { "ID", "Serviço", "Categoria", "Cliente", "E-mail Cliente", "Status", "Agendado em", "Criado em", "Observações" };
            createExcelSheet(wb, sheet, headers);

            int rowNum = 1;
            for (ServiceOrder o : orders) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(o.getId());
                row.createCell(1).setCellValue(o.getService().getName());
                row.createCell(2).setCellValue(o.getService().getCategory().getName());
                row.createCell(3).setCellValue(o.getClient().getName());
                row.createCell(4).setCellValue(o.getClient().getEmail());
                row.createCell(5).setCellValue(o.getStatus().name());
                row.createCell(6).setCellValue(o.getScheduledAt() != null ? o.getScheduledAt().format(FMT) : "—");
                row.createCell(7).setCellValue(o.getCreatedAt() != null ? o.getCreatedAt().format(FMT) : "");
                row.createCell(8).setCellValue(o.getNotes() != null ? o.getNotes() : "");
            }

            for (int i = 0; i < headers.length; i++) sheet.autoSizeColumn(i);
            return toBytes(wb);
        }
    }

    // =====================
    // PDF
    // =====================
    public byte[] exportUsersPdf() throws Exception {
        List<User> users = userRepository.findAll();
        Document doc = new Document(PageSize.A4.rotate());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter.getInstance(doc, out);
        doc.open();

        addPdfTitle(doc, "Relatório de Usuários");
        addPdfSubtitle(doc, "Total: " + users.size() + " usuários");

        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        table.setWidths(new float[]{ 1f, 3f, 4f, 2f, 1.5f, 2.5f });

        String[] headers = { "ID", "Nome", "E-mail", "Perfil", "Status", "Criado em" };
        addPdfHeaders(table, headers);

        for (User u : users) {
            addPdfCell(table, String.valueOf(u.getId()), false);
            addPdfCell(table, u.getName(), false);
            addPdfCell(table, u.getEmail(), false);
            addPdfCell(table, u.getRole().name(), false);
            addPdfCell(table, Boolean.TRUE.equals(u.getActive()) ? "Ativo" : "Inativo", false);
            addPdfCell(table, u.getCreatedAt() != null ? u.getCreatedAt().format(DATE_FMT) : "", false);
        }

        doc.add(table);
        doc.close();
        return out.toByteArray();
    }

    public byte[] exportServicesPdf() throws Exception {
        List<com.devx.auth.domain.Service> services = serviceRepository.findAll();
        Document doc = new Document(PageSize.A4.rotate());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter.getInstance(doc, out);
        doc.open();

        addPdfTitle(doc, "Relatório de Serviços");
        addPdfSubtitle(doc, "Total: " + services.size() + " serviços");

        PdfPTable table = new PdfPTable(7);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        table.setWidths(new float[]{ 1f, 3f, 2f, 2f, 1.5f, 1.5f, 1.5f });

        String[] headers = { "ID", "Nome", "Categoria", "Preço", "Duração", "Tipo", "Status" };
        addPdfHeaders(table, headers);

        for (com.devx.auth.domain.Service s : services) {
            addPdfCell(table, String.valueOf(s.getId()), false);
            addPdfCell(table, s.getName(), false);
            addPdfCell(table, s.getCategory().getName(), false);
            addPdfCell(table, "R$ " + s.getPrice(), false);
            addPdfCell(table, s.getDurationMinutes() != null ? s.getDurationMinutes() + " min" : "—", false);
            addPdfCell(table, s.getType().name(), false);
            addPdfCell(table, Boolean.TRUE.equals(s.getActive()) ? "Ativo" : "Inativo", false);
        }

        doc.add(table);
        doc.close();
        return out.toByteArray();
    }

    public byte[] exportOrdersPdf() throws Exception {
        List<ServiceOrder> orders = serviceOrderRepository.findAll();
        Document doc = new Document(PageSize.A4.rotate());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter.getInstance(doc, out);
        doc.open();

        addPdfTitle(doc, "Relatório de Ordens de Serviço");
        addPdfSubtitle(doc, "Total: " + orders.size() + " ordens");

        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        table.setWidths(new float[]{ 1f, 3f, 2f, 3f, 2f, 2f });

        String[] headers = { "ID", "Serviço", "Categoria", "Cliente", "Status", "Criado em" };
        addPdfHeaders(table, headers);

        for (ServiceOrder o : orders) {
            addPdfCell(table, String.valueOf(o.getId()), false);
            addPdfCell(table, o.getService().getName(), false);
            addPdfCell(table, o.getService().getCategory().getName(), false);
            addPdfCell(table, o.getClient().getName(), false);
            addPdfCell(table, o.getStatus().name(), false);
            addPdfCell(table, o.getCreatedAt() != null ? o.getCreatedAt().format(DATE_FMT) : "", false);
        }

        doc.add(table);
        doc.close();
        return out.toByteArray();
    }

    // =====================
    // HELPERS EXCEL
    // =====================
    private void createExcelSheet(Workbook wb, Sheet sheet, String[] headers) {
        CellStyle headerStyle = wb.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setBorderBottom(BorderStyle.THIN);

        Font headerFont = wb.createFont();
        headerFont.setColor(IndexedColors.WHITE.getIndex());
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 11);
        headerStyle.setFont(headerFont);

        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
    }

    private byte[] toBytes(Workbook wb) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        wb.write(out);
        return out.toByteArray();
    }

    // =====================
    // HELPERS PDF
    // =====================
    private void addPdfTitle(Document doc, String title) throws Exception {
        com.itextpdf.text.Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, new BaseColor(38, 33, 92));
        Paragraph p = new Paragraph(title, font);
        p.setAlignment(Element.ALIGN_CENTER);
        p.setSpacingAfter(5f);
        doc.add(p);
    }

    private void addPdfSubtitle(Document doc, String subtitle) throws Exception {
        com.itextpdf.text.Font font = FontFactory.getFont(FontFactory.HELVETICA, 11, new BaseColor(136, 135, 128));
        Paragraph p = new Paragraph(subtitle, font);
        p.setAlignment(Element.ALIGN_CENTER);
        p.setSpacingAfter(10f);
        doc.add(p);
    }

    private void addPdfHeaders(PdfPTable table, String[] headers) {
        BaseColor headerColor = new BaseColor(38, 33, 92);
        com.itextpdf.text.Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.WHITE);
        for (String h : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(h, font));
            cell.setBackgroundColor(headerColor);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(6f);
            table.addCell(cell);
        }
    }

    private void addPdfCell(PdfPTable table, String value, boolean bold) {
        com.itextpdf.text.Font font = bold
                ? FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9)
                : FontFactory.getFont(FontFactory.HELVETICA, 9);
        PdfPCell cell = new PdfPCell(new Phrase(value, font));
        cell.setPadding(5f);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(cell);
    }
}