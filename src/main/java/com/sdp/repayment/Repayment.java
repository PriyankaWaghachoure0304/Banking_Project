package com.sdp.repayment;

import java.time.LocalDate;

class Repayment {
    int installmentNo;
    LocalDate dueDate;
    double emi;
    double principal;
    double interest;
    double balance;
    boolean paid = false;

    public Repayment(int installmentNo, LocalDate dueDate, double emi, double principal, double interest, double balance) {
        this.installmentNo = installmentNo;
        this.dueDate = dueDate;
        this.emi = emi;
        this.principal = principal;
        this.interest = interest;
        this.balance = balance;
    }

    @Override
    public String toString() {
        return String.format("Installment %d | Due: %s | EMI: %.2f | Principal: %.2f | Interest: %.2f | Balance: %.2f | Paid: %s",
                installmentNo, dueDate, emi, principal, interest, balance, paid);
    }
}