package com.sdp.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.LinkedHashMap;
import java.util.Map;

import org.zkoss.zul.Messagebox;

import com.sdp.connection.DbConnection;

public class DepositDao {
    DbConnection db = new DbConnection();

    private static final String FETCH_USER = "SELECT balance FROM accounts WHERE account_number=?";
    private static final String UPDATE_USER = "UPDATE accounts SET balance=? WHERE account_number=?";
    
    private static final String FETCH_MASTER = "SELECT balance_after FROM bank_account WHERE account_number=?";
    private static final String UPDATE_MASTER = "UPDATE bank_account SET balance_after=? WHERE account_number=?";
    
    // Your fixed Master Bank Account Number
    private static final String MASTER_ACC_NO = "SDP00001";

    public void deposit(String accNum, Double amount) {
        try (Connection con = db.getConnection();
             PreparedStatement psUserFetch = con.prepareStatement(FETCH_USER);
             PreparedStatement psUserUpdate = con.prepareStatement(UPDATE_USER);
             PreparedStatement psMasterFetch = con.prepareStatement(FETCH_MASTER);
             PreparedStatement psMasterUpdate = con.prepareStatement(UPDATE_MASTER)) {

            con.setAutoCommit(false); // ✅ Start transaction

            // 1️⃣ Check User Account Balance
            psUserFetch.setString(1, accNum);
            ResultSet rsUser = psUserFetch.executeQuery();

            if (!rsUser.next()) {
                Messagebox.show("User Account doesn't exist!");
                return;
            }
            double userBalance = rsUser.getDouble("balance");

            // 2️⃣ Check Master Account Balance
            psMasterFetch.setString(1, MASTER_ACC_NO);
            ResultSet rsMaster = psMasterFetch.executeQuery();
            if (!rsMaster.next()) {
                Messagebox.show("Master Account not found!");
                return;
            }
            double masterBalance = rsMaster.getDouble("balance_after");

            if (masterBalance < amount) {
                Messagebox.show("Master Account has insufficient balance!");
                return;
            }

            // 3️⃣ Update User Account Balance
            double newUserBalance = userBalance + amount;
            psUserUpdate.setDouble(1, newUserBalance);
            psUserUpdate.setString(2, accNum);
            psUserUpdate.executeUpdate();

            // 4️⃣ Update Master Account Balance
            double newMasterBalance = masterBalance - amount;
            psMasterUpdate.setDouble(1, newMasterBalance);
            psMasterUpdate.setString(2, MASTER_ACC_NO);
            psMasterUpdate.executeUpdate();

            con.commit(); // ✅ Commit Transaction

            Messagebox.show("Deposit Successful!\nDeposited: " + amount +
                            "\nNew User Balance: " + newUserBalance +
                            "\nMaster Balance: " + newMasterBalance);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public Map<String, String> getAllAccounts() {
        Map<String, String> accounts = new LinkedHashMap<>();
        String query = "SELECT u.account_number, u.full_name " +
                       "FROM users u INNER JOIN accounts a ON u.account_number = a.account_number";
        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                accounts.put(rs.getString("account_number"), rs.getString("full_name"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return accounts;
    }


}
