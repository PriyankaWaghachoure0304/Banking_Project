<%@page import="com.sdp.transaction.TransferDao"%>
<%@page import="org.zkforge.json.simple.JSONObject"%>
<%@ page import="java.sql.*" %>
<%@ page import="java.util.UUID" %>
<%
    response.setContentType("application/json");
    JSONObject jsonResponse = new JSONObject();

    String toAccount   = request.getParameter("toAccount"); // receiver in your bank
    String amountStr   = request.getParameter("amount");
    String fromAccount = request.getParameter("fromAccount"); // sender (external bank)
    String remark      = request.getParameter("remark");

    if(toAccount == null || amountStr == null || fromAccount == null){
        jsonResponse.put("status", "error");
        jsonResponse.put("message", "Missing parameters");
        out.print(jsonResponse.toString());
        return;
    }

    double amount = Double.parseDouble(amountStr);

    Connection conn = null;
    PreparedStatement pstmt = null;
    ResultSet rs = null;

    try {
        Class.forName("com.mysql.cj.jdbc.Driver");
        conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/bank", "root", "Imageinfo@123");
		
        conn.setAutoCommit(false); // transaction start

        // 1. Add money to receiver’s account
        pstmt = conn.prepareStatement("UPDATE accounts SET balance = balance + ? WHERE account_number = ?");
        pstmt.setDouble(1, amount);
        pstmt.setString(2, toAccount);
        int receiverUpdate = pstmt.executeUpdate();
        pstmt.close();

        if(receiverUpdate == 0){
            conn.rollback();
            jsonResponse.put("status", "error");
            jsonResponse.put("message", "Receiver account not found");
            out.print(jsonResponse.toString());
            return;
        }

        // 2. Get receiver’s updated balance
        pstmt = conn.prepareStatement("SELECT balance FROM accounts WHERE account_number = ?");
        pstmt.setString(1, toAccount);
        rs = pstmt.executeQuery();
        double newBalance = 0.0;
        if(rs.next()){
            newBalance = rs.getDouble("balance");
        }
        pstmt.close();

        // 3. Insert CREDIT transaction
        TransferDao tdao = TransferDao.getTransferDao();
        String transactionId = tdao.getTransactionId();
        String insertSql = "INSERT INTO transactions " +
                "(transaction_id, from_account, to_account, amount, transaction_type, tax, remark, available_balance, from_direction, to_direction) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        pstmt = conn.prepareStatement(insertSql);
        pstmt.setString(1, transactionId);
        pstmt.setString(2, fromAccount); // external sender info (just reference)
        pstmt.setString(3, toAccount);
        pstmt.setDouble(4, amount);
        pstmt.setString(5, "API");   // CREDIT to receiver
        pstmt.setDouble(6, 0.00);       // tax default
        pstmt.setString(7, remark != null ? remark : "Money Received");
        pstmt.setDouble(8, newBalance);
        pstmt.setString(9, "EXTERNAL");
        pstmt.setString(10, "CREDIT");
        pstmt.executeUpdate();

        conn.commit();

        jsonResponse.put("status", "success");
        jsonResponse.put("transactionId", transactionId);
        jsonResponse.put("toAccount", toAccount);
        jsonResponse.put("fromAccount", fromAccount);
        jsonResponse.put("amount", amount);
        jsonResponse.put("availableBalance", newBalance);

    } catch(Exception e){
        if(conn != null) conn.rollback();
        jsonResponse.put("status", "error");
        jsonResponse.put("message", e.getMessage());
    } finally {
        try { if(rs != null) rs.close(); } catch(Exception ex){}
        try { if(pstmt != null) pstmt.close(); } catch(Exception ex){}
        try { if(conn != null) conn.close(); } catch(Exception ex){}
    }

    out.print(jsonResponse.toString());
%>
