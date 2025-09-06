package com.bank.admin;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zul.Div;
import org.zkoss.zul.Filedownload;

import com.bank.management.Database;

public class CustomerData extends SelectorComposer<Div>{
	
	private static final long serialVersionUID = 1L;
	/**
	 * CustomerData class is a ZK Composer that handles 
	 * exporting customer loan application data into an Excel file.
	 *
	 * When the "download" button is clicked, this class retrieves
	 * data from the loan_application table and generates an Excel 
	 * file (.xlsx) which is offered for download.
	 */
	@Listen("onClick=#download")
	public void downloadData() {
		try (Connection connection = Database.getConnection();
				Statement statement = connection.createStatement();
				ResultSet rs = statement
						.executeQuery("Select loan_AccountNo,name,mobile,purpose,status from loan_application");
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
				row.createCell(0).setCellValue(rs.getString("loan_AccountNo"));
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
