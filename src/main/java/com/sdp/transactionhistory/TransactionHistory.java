package com.sdp.transactionhistory;

import java.util.Date;
import java.util.List;

import org.zkoss.util.media.AMedia;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;

import com.sdp.dao.TransactionHistoryDao;
import com.sdp.digitalsignpdf.DigitalSignetureWithPdf;
import com.sdp.digitalsignpdf.PdfPasswordProtector;
import com.sdp.model.TransferPojo;
import com.sdp.pdfclass.PdfGenerator;

public class TransactionHistory extends SelectorComposer<Div> {
	
    private static final long serialVersionUID = 1L;

	@Wire
    Datebox fromdate;
	@Wire Datebox  todate;

    @Wire
    Listbox lbhistory;
    @Wire
    Textbox searchbox;
    String accountnum;
static final String VALIDATION ="Validation Error";
    @Override
    public void doAfterCompose(Div comp) throws Exception {
        super.doAfterCompose(comp);
        accountnum = (String) Sessions.getCurrent().getAttribute("accountnumber");
    }

    @Listen("onClick=#showhistory")
    public void showHistory() {
        Date fdate = fromdate.getValue();
        Date tdate = todate.getValue();

        if (fdate == null || tdate == null) {
            Messagebox.show("Please select both From and To dates.",VALIDATION , Messagebox.OK, Messagebox.EXCLAMATION);
            return;
        }

        if (fdate.after(tdate)) {
            Messagebox.show("From date cannot be after To date.", VALIDATION, Messagebox.OK, Messagebox.ERROR);
            return;
        }

        TransactionHistoryDao thistory = new TransactionHistoryDao();
        List<TransferPojo> historyList = thistory.fetchHistory(accountnum, fdate, tdate);
       
        
        String searchText = searchbox.getValue();
        historyList = historyList.stream()
            .filter(p -> p.getRemark().toLowerCase().contains(searchText.toLowerCase()))
            .toList();

        
        if (historyList.isEmpty()) {
            Messagebox.show("No transactions found for selected date range.", "Info", Messagebox.OK, Messagebox.INFORMATION);
        }

        lbhistory.setModel(new ListModelList<>(historyList));
    }
    
    @Listen("onClick=#downloadpdf")
    public void downloadAsPdf() {
    	try {
    		Date fdate = fromdate.getValue();
    		Date tdate = todate.getValue();

    		if (fdate == null || tdate == null) {
    			Messagebox.show("Please select both From and To dates.", VALIDATION, Messagebox.OK, Messagebox.EXCLAMATION);
    			return;
    		}

    		TransactionHistoryDao thistory = new TransactionHistoryDao();
    		List<TransferPojo> historyList = thistory.fetchHistory(accountnum, fdate, tdate);

    		if (historyList.isEmpty()) {
    			Messagebox.show("No data to export.");
    			return;
    		}

    		byte[] pdfData = PdfGenerator.generateTransactionHistoryPdf(historyList);
    		
            pdfData = DigitalSignetureWithPdf.signPdf(pdfData);

    		
            pdfData = PdfPasswordProtector.protectPdf(pdfData, accountnum);
            
    		AMedia amedia = new AMedia("Transaction_History_"+accountnum+".pdf", "pdf", "application/pdf", pdfData);
    		Filedownload.save(amedia);
    	} catch (Exception e) {
    		e.printStackTrace();
    		Messagebox.show("Failed to generate PDF.");
    	}
    }

    
}
