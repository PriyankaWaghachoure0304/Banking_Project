
package com.sdp.admin;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.zkoss.zul.Filedownload;

import com.sdp.connection.DbConnection;


public class CustomerData  {

    DbConnection db=new DbConnection();
    public void downloadData(String filter) {
        String query = "SELECT account_number, name, mobile, purpose, status FROM loan_application";
        if ("Home Loan".equalsIgnoreCase(filter)) {
            query += " WHERE purpose='Home Loan'";
        } else if ("Personal Loan".equalsIgnoreCase(filter)) {
            query += " WHERE purpose='Personal Loan'";
        } 
        
        try (Connection connection = db.getConnection();
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(query);
             XSSFWorkbook workbook = new XSSFWorkbook()) {

            Sheet sheet = workbook.createSheet("Customers");
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Account Number");
            headerRow.createCell(1).setCellValue("Name");
            headerRow.createCell(2).setCellValue("Mobile");
            headerRow.createCell(3).setCellValue("Purpose");
            headerRow.createCell(4).setCellValue("Status");

            int rowCount = 1;
            while (rs.next()) {
                Row row = sheet.createRow(rowCount++);
                row.createCell(0).setCellValue(rs.getString("account_number"));
                row.createCell(1).setCellValue(rs.getString("name"));
                row.createCell(2).setCellValue(rs.getString("mobile"));
                row.createCell(3).setCellValue(rs.getString("purpose"));
                row.createCell(4).setCellValue(rs.getString("status"));
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            workbook.write(baos);

            Filedownload.save(baos.toByteArray(),
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                    "customers.xlsx");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
