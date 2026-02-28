package com.expensetracker.controller;

import com.expensetracker.entity.Transaction;
import com.expensetracker.entity.User;
import com.expensetracker.security.CustomUserDetails;
import com.expensetracker.service.TransactionService;
import com.expensetracker.service.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/export")
@RequiredArgsConstructor
public class ExportController {

    private final TransactionService transactionService;
    private final UserService userService;

    @GetMapping("/transactions/excel")
    public ResponseEntity<byte[]> exportToExcel(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year) throws Exception {

        User user = userService.findByUsername(userDetails.getUsername());

        LocalDate now = LocalDate.now();
        int selectedMonth = (month != null) ? month : now.getMonthValue();
        int selectedYear = (year != null) ? year : now.getYear();

        List<Transaction> transactions = transactionService.getTransactionsByMonthYear(user, selectedMonth, selectedYear);

        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Transactions");

            // ── Styles ──────────────────────────────────────────────
            CellStyle titleStyle = workbook.createCellStyle();
            Font titleFont = workbook.createFont();
            titleFont.setBold(true);
            titleFont.setFontHeightInPoints((short) 14);
            titleStyle.setFont(titleFont);
            titleStyle.setAlignment(HorizontalAlignment.CENTER);

            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.DARK_GREEN.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);

            CellStyle incomeStyle = workbook.createCellStyle();
            Font incomeFont = workbook.createFont();
            incomeFont.setColor(IndexedColors.DARK_GREEN.getIndex());
            incomeFont.setBold(true);
            incomeStyle.setFont(incomeFont);
            incomeStyle.setBorderBottom(BorderStyle.THIN);
            incomeStyle.setBorderLeft(BorderStyle.THIN);
            incomeStyle.setBorderRight(BorderStyle.THIN);

            CellStyle expenseStyle = workbook.createCellStyle();
            Font expenseFont = workbook.createFont();
            expenseFont.setColor(IndexedColors.RED.getIndex());
            expenseFont.setBold(true);
            expenseStyle.setFont(expenseFont);
            expenseStyle.setBorderBottom(BorderStyle.THIN);
            expenseStyle.setBorderLeft(BorderStyle.THIN);
            expenseStyle.setBorderRight(BorderStyle.THIN);

            CellStyle normalStyle = workbook.createCellStyle();
            normalStyle.setBorderBottom(BorderStyle.THIN);
            normalStyle.setBorderLeft(BorderStyle.THIN);
            normalStyle.setBorderRight(BorderStyle.THIN);

            CellStyle amountStyle = workbook.createCellStyle();
            DataFormat format = workbook.createDataFormat();
            amountStyle.setDataFormat(format.getFormat("#,##0.00"));
            amountStyle.setBorderBottom(BorderStyle.THIN);
            amountStyle.setBorderLeft(BorderStyle.THIN);
            amountStyle.setBorderRight(BorderStyle.THIN);

            CellStyle summaryLabelStyle = workbook.createCellStyle();
            Font summaryFont = workbook.createFont();
            summaryFont.setBold(true);
            summaryLabelStyle.setFont(summaryFont);
            summaryLabelStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            summaryLabelStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            summaryLabelStyle.setBorderBottom(BorderStyle.THIN);
            summaryLabelStyle.setBorderTop(BorderStyle.THIN);
            summaryLabelStyle.setBorderLeft(BorderStyle.THIN);
            summaryLabelStyle.setBorderRight(BorderStyle.THIN);

            // ── Title Row ────────────────────────────────────────────
            String monthName = now.withMonth(selectedMonth).getMonth()
                    .getDisplayName(java.time.format.TextStyle.FULL, java.util.Locale.ENGLISH);
            Row titleRow = sheet.createRow(0);
            titleRow.setHeightInPoints(24);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("Expense Tracker — " + monthName + " " + selectedYear);
            titleCell.setCellStyle(titleStyle);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 5));

            // ── Sub-title: username ───────────────────────────────────
            Row subRow = sheet.createRow(1);
            Cell subCell = subRow.createCell(0);
            subCell.setCellValue("Account: " + user.getUsername()
                    + "   |   Exported on: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy")));
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 5));

            // ── Blank row ─────────────────────────────────────────────
            sheet.createRow(2);

            // ── Header Row ────────────────────────────────────────────
            String[] headers = {"#", "Date", "Description", "Category", "Type", "Amount (₹)"};
            Row headerRow = sheet.createRow(3);
            headerRow.setHeightInPoints(18);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // ── Data Rows ─────────────────────────────────────────────
            int rowNum = 4;
            double totalIncome = 0;
            double totalExpense = 0;

            for (int i = 0; i < transactions.size(); i++) {
                Transaction t = transactions.get(i);
                Row row = sheet.createRow(rowNum++);

                boolean isIncome = t.getType().name().equals("INCOME");
                double amount = t.getAmount().doubleValue();

                if (isIncome) totalIncome += amount;
                else totalExpense += amount;

                Cell c0 = row.createCell(0);
                c0.setCellValue(i + 1);
                c0.setCellStyle(normalStyle);

                Cell c1 = row.createCell(1);
                c1.setCellValue(t.getDate().format(DateTimeFormatter.ofPattern("dd MMM yyyy")));
                c1.setCellStyle(normalStyle);

                Cell c2 = row.createCell(2);
                c2.setCellValue(t.getDescription());
                c2.setCellStyle(normalStyle);

                Cell c3 = row.createCell(3);
                c3.setCellValue(t.getCategory().getName());
                c3.setCellStyle(normalStyle);

                Cell c4 = row.createCell(4);
                c4.setCellValue(t.getType().name());
                c4.setCellStyle(isIncome ? incomeStyle : expenseStyle);

                Cell c5 = row.createCell(5);
                c5.setCellValue(isIncome ? amount : -amount);
                c5.setCellStyle(amountStyle);
            }

            // ── Blank row before summary ──────────────────────────────
            sheet.createRow(rowNum++);

            // ── Summary Section ───────────────────────────────────────
            String[][] summary = {
                    {"Total Income",  String.format("%.2f", totalIncome)},
                    {"Total Expense", String.format("%.2f", totalExpense)},
                    {"Net Balance",   String.format("%.2f", totalIncome - totalExpense)}
            };

            for (String[] s : summary) {
                Row row = sheet.createRow(rowNum++);
                Cell label = row.createCell(4);
                label.setCellValue(s[0]);
                label.setCellStyle(summaryLabelStyle);

                Cell value = row.createCell(5);
                value.setCellValue(Double.parseDouble(s[1]));
                value.setCellStyle(amountStyle);
            }

            // ── Column Widths ─────────────────────────────────────────
            sheet.setColumnWidth(0, 8 * 256);    // #
            sheet.setColumnWidth(1, 18 * 256);   // Date
            sheet.setColumnWidth(2, 35 * 256);   // Description
            sheet.setColumnWidth(3, 22 * 256);   // Category
            sheet.setColumnWidth(4, 14 * 256);   // Type
            sheet.setColumnWidth(5, 18 * 256);   // Amount

            // ── Write to bytes ────────────────────────────────────────
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);

            String filename = "transactions_" + monthName + "_" + selectedYear + ".xlsx";

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(out.toByteArray());
        }
    }
}