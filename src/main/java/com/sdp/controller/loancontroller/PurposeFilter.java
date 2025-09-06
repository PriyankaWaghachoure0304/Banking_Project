package com.sdp.controller.loancontroller;

import java.sql.SQLException;

import javax.sql.RowSet;
import javax.sql.rowset.Predicate;

public class PurposeFilter implements Predicate {

    private String purpose;

    public PurposeFilter(String purpose) {
        this.purpose = purpose;
    }

    @Override
    public boolean evaluate(Object value, String columnName) throws SQLException {
        if ("purpose".equalsIgnoreCase(columnName)) {
            return purpose.equalsIgnoreCase((String) value);
        }
        return true;
    }

    @Override
    public boolean evaluate(RowSet rs) {
        try {
            return purpose.equalsIgnoreCase(rs.getString("purpose"));
        } catch (SQLException e) {
            return false;
        }
    }

    @Override
    public boolean evaluate(Object value, int columnIndex) throws SQLException {
        return true;
    }
}
