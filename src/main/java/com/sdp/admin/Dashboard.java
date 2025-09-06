package com.sdp.admin;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.CategoryModel;
import org.zkoss.zul.Chart;
import org.zkoss.zul.Div;
import org.zkoss.zul.Include;
import org.zkoss.zul.Label;
import org.zkoss.zul.SimpleCategoryModel;

import com.sdp.connection.DbConnection;
import com.sdp.model.Notification;
import com.sdp.serialization.Deserialize;
import com.sdp.serialization.Serialize;

/**
 * Dashboard controller for the Admin panel.
 * Loading and displaying loan application statistics
 * Populating charts (monthly trend, status distribution, weekly comparison)
 * Handling navigation to other pages like customer list and status-based views
 *
 */
public class Dashboard extends SelectorComposer<Div> implements Serializable {
    private static final long serialVersionUID = 1L;

    @Wire
    private Label aplabel;
    @Wire
    private Label totallabel;
    @Wire
    private Label wllabel;
    @Wire
    private Label dllabel;
    @Wire
    private Chart statusChart;
    @Wire
    private Chart loanChart;

    @Wire
    private Div dashboardContent;
    @Wire
    private Div depositContent;
    @Wire
    private Div creditCardContent;

    @Wire
    private Include includeDeposit;
    @Wire private Include  includeCreditApply;

    @Wire
    private Div dashboardMenu;
    @Wire private Div  depositMenu;
    @Wire private Div  creditCardMenu;
    @Wire private Div  loanMenu;
    @Wire private Div  customerListMenu;

    static final String TIMEFORMAT="HH:mm:ss";
    static final String SIDEBAR="sidebar-item selected";
    @Override
    public void doAfterCompose(Div comp) throws Exception {
        super.doAfterCompose(comp);
        
        Boolean isAdminLoggedIn = (Boolean) Sessions.getCurrent().getAttribute("adminIslogged");
        if(isAdminLoggedIn!=null && isAdminLoggedIn) {
        loadDashboardData();
        showContent(dashboardContent);
        }
        else {
			Executions.sendRedirect("/LoginPage.zul");
		}
    }

    DbConnection db = new DbConnection();

    private void loadDashboardData() {
        try (Connection conn = db.getConnection();
             Statement stmt1 = conn.createStatement();
             Statement stmt2 = conn.createStatement();
             Statement stmt3 = conn.createStatement();) {
        	

            SimpleCategoryModel monthModel = new SimpleCategoryModel();
            SimpleCategoryModel statusModel = new SimpleCategoryModel();

            int approvedPolicies = getCount(conn, "SELECT COUNT(*) FROM loan_application WHERE status = 'approved'");
            int totalUsers = getCount(conn, "SELECT COUNT(DISTINCT application_id) FROM loan_application");
            int waitingList = getCount(conn, "SELECT COUNT(*) FROM loan_application WHERE status = 'waiting'");
            int disbursedList = getCount(conn, "SELECT COUNT(*) FROM loan_application WHERE status='Disbursed'");
            
            
            aplabel.setValue(String.valueOf(approvedPolicies));
            totallabel.setValue(String.valueOf(totalUsers));
            wllabel.setValue(String.valueOf(waitingList));
            dllabel.setValue(String.valueOf(disbursedList));

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
            statusChart.setModel(statusModel);

            /**
             * Weekly Approved vs Disbursed Loans chart
             */
            CategoryModel model = new SimpleCategoryModel();

            String[] days = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
            for (String d : days) {
                model.setValue("Approved Loans", d, 0);
                model.setValue("Disbursed Loans", d, 0);
                
            }

            String sql = "SELECT DATE_FORMAT(created_at, '%a') AS day,    status,COUNT(*) AS count FROM loan_application WHERE created_at >= CURDATE() - INTERVAL 7 DAY AND status IN ('Approved','Disbursed') GROUP BY DATE_FORMAT(created_at, '%a'), status ORDER BY DAYOFWEEK(MIN(created_at)), status";

            ResultSet rs3 = stmt3.executeQuery(sql);

            while (rs3.next()) {
                String day = rs3.getString("day");
                String status = rs3.getString("status");
                int count = rs3.getInt("count");

                if ("approved".equalsIgnoreCase(status)) {
                	model.setValue("Approved Loans", day, Integer.valueOf(count));
                } else if ("Disbursed".equalsIgnoreCase(status)) {
                	model.setValue("Disbursed Loans", day, Integer.valueOf(count));                }
            }

            loanChart.setModel(model);
            loanChart.setTitle("Approved vs Disbursed Loans (This Week)");


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    
    @Listen("onClick=#adminLogout")
    public void logout()
    {
    	Sessions.getCurrent().removeAttribute("adminIslogged");
    	Executions.sendRedirect("/LoginPage.zul");
    }

    private int getCount(Connection conn, String query) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    private void showContent(Div contentDiv) {
        dashboardContent.setVisible(false);
        depositContent.setVisible(false);
        creditCardContent.setVisible(false);
        contentDiv.setVisible(true);

        dashboardMenu.setSclass(SIDEBAR);
        depositMenu.setSclass(SIDEBAR);
        creditCardMenu.setSclass(SIDEBAR);
        loanMenu.setSclass(SIDEBAR);
        customerListMenu.setSclass(SIDEBAR);

        if (contentDiv.equals(dashboardContent)) dashboardMenu.setSclass(SIDEBAR);
        if (contentDiv.equals(depositContent)) depositMenu.setSclass(SIDEBAR);
        if (contentDiv.equals(creditCardContent)) creditCardMenu.setSclass(SIDEBAR);
    }

    /**
     * Navigation Handlers
     */

    @Listen("onClick=#dashboardMenu")
    public void showDashboard() {
        showContent(dashboardContent);
    }

    @Listen("onClick=#depositMenu")
    public void showDeposit() {
        showContent(depositContent);
        includeDeposit.setSrc("/DipositAmount/depositAmount.zul");
    }

    @Listen("onClick=#creditCardMenu")
    public void showCreditCard() {
        showContent(creditCardContent);
        includeCreditApply.setSrc("/Admin/credit_User.zul");
    }

    @Listen("onClick=#loanMenu")
    public void showLoan() {
        showContent(dashboardContent); // Show the main dashboard view for loan stats
        dashboardMenu.removeSclass("selected");
        loanMenu.setSclass(SIDEBAR);
        
    }

    @Listen("onClick=#customerListMenu")
    public void customerList() {
        customerListMenu.setSclass(SIDEBAR);
        Executions.sendRedirect("/customer/customer.zul");
    }

    static final String STYPE = "statusType";

    @Listen("onClick = #wldiv")
    public void onWaitingClick() {
        Sessions.getCurrent().setAttribute(STYPE, "waiting");
        Executions.sendRedirect("/customer/customer.zul");
    }

    @Listen("onClick = #aldiv")
    public void onApprovedClick() {
        Sessions.getCurrent().setAttribute(STYPE, "Approved");
        Executions.sendRedirect("/Admin/status_list.zul");
    }

    @Listen("onClick=#totaldiv")
    public void loanCustomerList() {
        Sessions.getCurrent().setAttribute(STYPE, "Alldata");
        Executions.sendRedirect("/Admin/loan_customer.zul");
    }

    @Listen("onClick=#dldiv")
    public void loanDisbursedList() {
        Sessions.getCurrent().setAttribute(STYPE, "Disbursed");
        Executions.sendRedirect("/Admin/loan_customer.zul");
    }


    @Listen("onClick=#sendNotification")
    public void sendNotification() {
        String message = "Happy Birthday!";

        EventQueue<Event> q = EventQueues.lookup("notification", EventQueues.APPLICATION, true);
        Event e1 = new Event("msg", null, "Notification Alert!");
        q.publish(e1);


        List<Notification> list = new Deserialize().loadMessage();

        Notification notification = new Notification();
        notification.setAccountNumber("258789461001");
        notification.setMessage(message);
        notification.setIsSeen(false);
        notification.setDateTime(LocalDateTime.now().toLocalDate() + " at " + LocalDateTime.now().format(DateTimeFormatter.ofPattern(TIMEFORMAT)));

        Notification notification2 = new Notification();
        notification2.setAccountNumber("258789461000");
        notification2.setMessage(message);
        notification2.setIsSeen(false);
        notification2.setDateTime(LocalDateTime.now().toLocalDate() + " at " + LocalDateTime.now().format(DateTimeFormatter.ofPattern(TIMEFORMAT)));

        Notification notification3 = new Notification();
        notification3.setAccountNumber("258789461003");
        notification3.setMessage(message);
        notification3.setIsSeen(false);
        notification3.setDateTime(LocalDateTime.now().toLocalDate() + " at " + LocalDateTime.now().format(DateTimeFormatter.ofPattern(TIMEFORMAT)));

        list.add(notification);
        list.add(notification2);
        list.add(notification3);

        Serialize serialize = new Serialize();
        serialize.saveMessage(list);


    }


}