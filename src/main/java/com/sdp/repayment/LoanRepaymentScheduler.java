package com.sdp.repayment;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class LoanRepaymentScheduler {

    private String accountNo;
    public LoanRepaymentScheduler(String accountNo) {
        this.accountNo = accountNo;
    }
   
        public void start() {
       try(    ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();){

            scheduler.scheduleAtFixedRate(() -> {
                try {
                    if (accountNo != null) {
                        LoanRepaymentDAO dao = new LoanRepaymentDAO();
                        dao.processEMI(accountNo);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }, 0, 1, TimeUnit.MINUTES); 
       }
    }
}
