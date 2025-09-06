package com.bank.admin;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.CategoryModel;
import org.zkoss.zul.Chart;
import org.zkoss.zul.Div;
import org.zkoss.zul.Label;
import org.zkoss.zul.SimpleCategoryModel;
import com.bank.management.Database;

/**
 * Dashboard controller for the Admin panel.
 * Loading and displaying loan application statistics
 * Populating charts (monthly trend, status distribution, weekly comparison)
 * Handling navigation to other pages like customer list and status-based views

 */
public class Dashboard extends SelectorComposer<Div> implements Serializable {
	private static final long serialVersionUID = 1L;

	@Wire
	private Label aprovedpolicy;

	@Wire
	private Label disaprovedpolicy;

	@Wire
	private Label totalloanuser;

	@Wire
	private Label waitinglist;

	@Wire
	private Label disbursedloans;

	@Wire
	private Chart barchart3d;
	@Wire
	private Chart statusChart;
	@Wire
	private Chart loanChart;
	@Override
	public void doAfterCompose(Div comp) throws Exception {
		super.doAfterCompose(comp);
		loadDashboardData();
	}

	private void loadDashboardData() {
		try (Connection conn = Database.getConnection();
				Statement stmt1 = conn.createStatement();
				Statement stmt2 = conn.createStatement();
				Statement stmt3 = conn.createStatement();) {
			
			SimpleCategoryModel monthModel = new SimpleCategoryModel();
			SimpleCategoryModel statusModel = new SimpleCategoryModel();

			int approvedPolicies = getCount(conn, "SELECT COUNT(*) FROM loan_application WHERE status = 'approved'");
			int disapprovedPolicies = getCount(conn, "SELECT COUNT(*) FROM loan_application WHERE status = 'Rejected'");
			int totalUsers = getCount(conn, "SELECT COUNT(DISTINCT application_id) FROM loan_application");
			int waitingList = getCount(conn, "SELECT COUNT(*) FROM loan_application WHERE status = 'waiting'");
			int disbursedList = getCount(conn, "SELECT COUNT(*) FROM loan_application WHERE status='Disbursed'");
			aprovedpolicy.setValue("Approved Loans " + approvedPolicies);
			disaprovedpolicy.setValue("Disapproved Loans " + disapprovedPolicies);
			totalloanuser.setValue("Total Loan Users  " + totalUsers);
			waitinglist.setValue("Waiting Users " + waitingList);
			disbursedloans.setValue("Loan Disbursed " + disbursedList);
		
			/**
			 * Build monthly application trend (barchart3d)
			 * Monthly applications chart
			 */
			ResultSet rs1 = stmt1
					.executeQuery("SELECT DATE_FORMAT(created_at, '%Y-%m') AS month, COUNT(*) AS total_applications "
							+ "FROM loan_application " + "GROUP BY DATE_FORMAT(created_at, '%Y-%m') "
							+ "ORDER BY month");
			while (rs1.next()) {
				String month = rs1.getString("month");
				int count = rs1.getInt("total_applications");
				monthModel.setValue("Applications", month, count);
			}
			rs1.close();
			/**
			 * Build loan status distribution (statusChart)
			 * Loan status distribution chart
			 */
			ResultSet rs2 = stmt2
					.executeQuery("SELECT status, COUNT(*) AS count FROM loan_application GROUP BY status");
			while (rs2.next()) {
				String status = rs2.getString("status");
				int count = rs2.getInt("count");
				statusModel.setValue("Loan Applications", status, count);
			}
			rs2.close();
			statusChart.setTitle("Loan Application Status");
			statusChart.setTitle("Count");
			statusChart.setTitle("Status");
			
			barchart3d.setModel(monthModel);
			statusChart.setModel(statusModel);

			/**
			 * Weekly Approved vs Disbursed Loans chart
			 */
			CategoryModel model = new SimpleCategoryModel();

			String[] days = { "Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun" };
			for (String d : days) {
				model.setValue("Approved Loans", d, 0);
				model.setValue("Disbursed Loans", d, 0);
			}

			String sql = "SELECT DATE_FORMAT(created_at, '%a') AS day, status, COUNT(*) AS count "
					+ "FROM loan_application " + "WHERE YEARWEEK(created_at, 1) = YEARWEEK(CURDATE(), 1) "
					+ "AND status IN ('approved','Disbursed') " + "GROUP BY day, status";

			ResultSet rs3 = stmt3.executeQuery(sql);

			while (rs3.next()) {
				String day = rs3.getString("day");
				String status = rs3.getString("status");
				int count = rs3.getInt("count");

				if ("approved".equalsIgnoreCase(status)) {
					model.setValue("Approved Loans", day, count);
				} else if ("Disbursed".equalsIgnoreCase(status)) {
					model.setValue("Disbursed Loans", day, count);
				}
			}
			
			
			
			loanChart.setModel(model);
			loanChart.setTitle("Approved vs Disbursed Loans (This Week)");

			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	

	private int getCount(Connection conn, String query) throws SQLException {
		try (PreparedStatement stmt = conn.prepareStatement(query); ResultSet rs = stmt.executeQuery()) {
			if (rs.next()) {
				return rs.getInt(1);
			}
		}
		return 0;
	}
/**
 * Navigation Handlers
 */
	@Listen("onClick=#Lcustomer")
	public void customerList() {
		Executions.sendRedirect("/customer/customer.zul");
	}

	static final String STYPE = "statusType";

	@Listen("onClick = #wl")
	public void onWaitingClick() {
		Sessions.getCurrent().setAttribute(STYPE, "waiting");
		Executions.sendRedirect("/customer/customer.zul");
	}

	@Listen("onClick = #al")
	public void onApprovedClick() {
		Sessions.getCurrent().setAttribute(STYPE, "Approved");
		Executions.sendRedirect("/Admin/status_list.zul");
	}

	@Listen("onClick=#tUsers")
	public void loanCustomerList() {
		Sessions.getCurrent().setAttribute(STYPE, "Alldata");
		Executions.sendRedirect("/Admin/loan_customer.zul");
	}

	@Listen("onClick=#dList")
	public void loanDisbursedList() {
		Sessions.getCurrent().setAttribute(STYPE, "Disbursed");
		Executions.sendRedirect("/Admin/loan_customer.zul");
	}
}
