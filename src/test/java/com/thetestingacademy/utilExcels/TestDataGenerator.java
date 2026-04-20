package com.thetestingacademy.utilExcels;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Utility class to generate test data Excel file for Data-Driven Testing.
 * Run the main method to create/update the TestData.xlsx file.
 */
public class TestDataGenerator {

    private static final String FILE_PATH = System.getProperty("user.dir") + "/src/test/resources/TestData.xlsx";

    public static void main(String[] args) {
        generateBookingTestData();
        System.out.println("Test data generated successfully at: " + FILE_PATH);
    }

    public static void generateBookingTestData() {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Booking");

            // Create header row with styling
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            String[] headers = {"firstname", "lastname", "totalprice", "depositpaid", "checkin", "checkout", "additionalneeds"};
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Test data rows
            Object[][] testData = {
                    {"John", "Doe", "150", "true", "2024-01-01", "2024-01-05", "Breakfast"},
                    {"Jane", "Smith", "200", "false", "2024-02-10", "2024-02-15", "Lunch"},
                    {"Robert", "Johnson", "175", "true", "2024-03-20", "2024-03-25", "Dinner"},
                    {"Emily", "Williams", "250", "true", "2024-04-05", "2024-04-10", "Breakfast and Parking"},
                    {"Michael", "Brown", "300", "false", "2024-05-15", "2024-05-20", "None"},
                    {"Sarah", "Davis", "125", "true", "2024-06-01", "2024-06-03", "Airport Shuttle"},
                    {"David", "Miller", "180", "true", "2024-07-10", "2024-07-15", "Late Checkout"},
                    {"Jennifer", "Wilson", "220", "false", "2024-08-20", "2024-08-25", "Early Checkin"},
                    {"James", "Taylor", "190", "true", "2024-09-05", "2024-09-10", "Breakfast"},
                    {"Lisa", "Anderson", "275", "true", "2024-10-15", "2024-10-20", "Spa Package"}
            };

            // Populate data rows
            for (int i = 0; i < testData.length; i++) {
                Row row = sheet.createRow(i + 1);
                for (int j = 0; j < testData[i].length; j++) {
                    row.createCell(j).setCellValue(String.valueOf(testData[i][j]));
                }
            }

            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Write to file
            try (FileOutputStream fileOut = new FileOutputStream(FILE_PATH)) {
                workbook.write(fileOut);
            }

        } catch (IOException e) {
            throw new RuntimeException("Failed to generate test data file", e);
        }
    }
}
