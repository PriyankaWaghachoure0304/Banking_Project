package com.sdp.customer;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zul.Div;
import org.zkoss.zul.Label;
import org.zkoss.zul.Window;

import com.sdp.connection.DbConnection;
import com.sdp.model.User;

import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
/**
 * Dashboard Controller for Customer Loan Overview and Report Generation.
 *
 * <p>
 * This class manages the customer dashboard in the banking application.
 * It displays loan details, repayment information, and provides functionality
 * to download sanction and agreement letters as PDF reports using JasperReports.
 * </p>
 *
 * <p>Features:</p>
 * <ul>
 *     <li>Display customer loan details: amount, tenure, EMI, status, next due, total paid, balance.</li>
 *     <li>Generate and download sanction letters as PDF.</li>
 *     <li>Generate and download agreement letters as PDF.</li>
 * </ul>
 *
 * <p>Dependencies:</p>
 * <ul>
 *     <li>ZK Framework for UI components and event handling.</li>
 *     <li>JasperReports for PDF report generation.</li>
 *     <li>Database connection for fetching customer and loan details.</li>
 * </ul>
 */
public class Dashboard extends SelectorComposer<Div> {
	private static final long serialVersionUID = 1L;
	@Wire
	Label l2;
	@Wire
	Label loanStatus;
	@Wire
	Label lblAmount;
	@Wire
	Label lblTenure;
	@Wire
	Label lblEMI;
	@Wire
	Label lblStatus;
	@Wire
	Label lblNextDue;
	@Wire
	Label lblTotalPaid;
	@Wire
	Label lblBalance;
	@Wire
	Label welcome;
	@Wire
	Label toname;

	String sql = "SELECT la.loan_amount, la.tenure_months, la.emi, la.status, la.account_number, " +
            "lr.total_paid, lr.remaining_balance, lr.next_due_date " +
            "FROM loan_application la " +
            "LEFT JOIN loan_repayment lr ON la.account_number = lr.account_number " +
            "WHERE la.account_number = ? " +
            "ORDER BY lr.updated_at DESC LIMIT 1";



	transient Session SessionEx = Sessions.getCurrent();
	String noLoan="No Loan Found";
	static final String STATUS="status";
	static final String FILELOC="/files/";
	static final String APPLICATION="accountnumber";

	String accountNumber;
	transient User user;
	DbConnection db=new DbConnection();
	@Override
	public void doAfterCompose(Div comp) throws Exception {
		super.doAfterCompose(comp);


		accountNumber = (String) Sessions.getCurrent().getAttribute(APPLICATION);
		
		user = (User) Sessions.getCurrent().getAttribute("userDetails");
	
	    loadData();
	}

	public void loadData()
	{
		if(user!=null ) {
			welcome.setValue("Welcome");
			toname.setValue( "🎊 "+user.getFullName()+" 🎊");
			
			
		try (Connection conn = db.getConnection();
	             PreparedStatement ps = conn.prepareStatement(sql)) {

	            ps.setString(1, accountNumber);
	            ResultSet rs = ps.executeQuery();

	            if (rs.next()) {
	            	
	                lblAmount.setValue("₹" + rs.getDouble("loan_amount"));
	                lblTenure.setValue(String.valueOf(rs.getInt("tenure_months")));
	                lblEMI.setValue("₹" + rs.getDouble("emi"));
	                lblStatus.setValue(rs.getString(STATUS));
	                loanStatus.setValue(rs.getString(STATUS));
	                
	                
	                double totalPaid = rs.getDouble("total_paid");
	                if (rs.wasNull()) totalPaid = 0; 
	                lblTotalPaid.setValue("₹" + totalPaid);

	                double remainingBalance = rs.getDouble("remaining_balance");
	                if (rs.wasNull()) remainingBalance = 0;
	                lblBalance.setValue("₹" + remainingBalance);

	                Timestamp dueTs = rs.getTimestamp("next_due_date");
	                if (dueTs != null) {
	                    lblNextDue.setValue(new java.text.SimpleDateFormat("dd-MM-yyyy").format(dueTs));
	                } else {
	                    lblNextDue.setValue("N/A");
	                }
	            }
	             else {
	                lblAmount.setValue("-");
	                lblTenure.setValue("-");
	                lblEMI.setValue("-");
	                lblStatus.setValue(noLoan);
	                loanStatus.setValue(noLoan);
	                lblTotalPaid.setValue("₹0");
	                lblBalance.setValue("₹0");
	                lblNextDue.setValue("-");
	            }

	        } catch (Exception e) {
	            e.printStackTrace();
	        }
		
		}
	}
	
	@Wire
	private Window win;

	@Listen("onClick = #btnDownloadSanction")
	public void downloadSanctionLetter() {
		try {
			String accNo = (String) SessionEx.getAttribute(APPLICATION);
			if (accNo == null) {
				alert("No loan application found.");
				return;
			}

			/** Fetch customer details from DB*/
			AccountOpening dao = new AccountOpening();
			Customer c = dao.getLoanApplicationByAc(accNo);
			
			File jrxmlFile = new File(
					Sessions.getCurrent().getWebApp().getRealPath("/WEB-INF/resource/sanction.jrxml"));
			String jasperPath = jrxmlFile.getParent() + "/sanction.jasper";
			JasperCompileManager.compileReportToFile(jrxmlFile.getAbsolutePath(), jasperPath);
			String imgPath= Executions.getCurrent().getDesktop().getWebApp().getRealPath("/Images/1000064228.png");
			Map<String, Object> params = new HashMap<>();
			params.put("date", new java.util.Date());
			params.put("name", c.getName());
			params.put("propertyAddress", c.getPropertyAddress());
			params.put("loanAmount", c.getLoanAmount());
			params.put("tenureMonths", c.getTenureMonths());
			params.put("emi", c.getEmi());
			params.put("accountNo", c.getAccountNo());
			 params.put("logoPath",imgPath );
			ArrayList<Customer> list = new ArrayList<>();
			list.add(c);
			JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(list);
			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperPath, params, dataSource);

			String pdfFileName = "sanction_" + accNo + ".pdf";
			String pdfPath = Sessions.getCurrent().getWebApp().getRealPath(FILELOC) + File.separator + pdfFileName;
			JasperExportManager.exportReportToPdfFile(jasperPrint, pdfPath);

			Executions.getCurrent().sendRedirect(FILELOC + pdfFileName, "_blank");

		} catch (Exception e) {
			e.printStackTrace();
			alert("Error generating report: " + e.getMessage());
		}
	}

	@Listen("onClick=#btnDownloadAgreement")
	public void downloadAgreementLetter() {
		try {
			String accNo = (String) SessionEx.getAttribute(APPLICATION);
			if (accNo == null) {
				alert("No loan application found.");
				return;
			}

			AccountOpening dao = new AccountOpening();
			Customer c = dao.getLoanApplicationByAc(accNo);

			File jrxmlFile = new File(
					Sessions.getCurrent().getWebApp().getRealPath("/WEB-INF/resource/Agreement.jrxml"));
			String jasperPath = jrxmlFile.getParent() + "/Agreement.jasper";
			JasperCompileManager.compileReportToFile(jrxmlFile.getAbsolutePath(), jasperPath);

			String imgPath= Executions.getCurrent().getDesktop().getWebApp().getRealPath("/Images/1000064228.png");

			Map<String, Object> params = new HashMap<>();
			params.put("date", new java.util.Date());
			params.put("name", c.getName());
			 params.put("logoPath",imgPath );

			ArrayList<Customer> list = new ArrayList<>();
			list.add(c);
			JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(list);
			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperPath, params, dataSource);

			String pdfFileName = "agreement_" + accNo + ".pdf";
			String pdfPath = Sessions.getCurrent().getWebApp().getRealPath(FILELOC) + File.separator + pdfFileName;
			JasperExportManager.exportReportToPdfFile(jasperPrint, pdfPath);

			Executions.getCurrent().sendRedirect(FILELOC + pdfFileName, "_blank");

		} catch (Exception e) {
			e.printStackTrace();
			alert("Error generating report: " + e.getMessage());
		}
	}
}
