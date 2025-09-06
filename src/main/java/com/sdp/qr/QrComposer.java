package com.sdp.qr;



import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zkmax.zul.Barcode;
import org.zkoss.zul.Div;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;

import com.sdp.dao.UpiDao;

public class QrComposer extends SelectorComposer<Div> {
	
	private static final long serialVersionUID = 1L;
	@Wire
	Barcode qrcode;
	@Wire
	Label upiLab;
	
	@Wire
	Div qrcodeContainer;
	
	String accountNumber;
	
	transient UpiDao udao = new UpiDao();
	
	@Override
	public void doAfterCompose(Div comp) throws Exception {
		super.doAfterCompose(comp);
		accountNumber = (String) Sessions.getCurrent().getAttribute("accountnumber");
		
		
	}
	
	@Listen("onClick=#qrbtn")
	public void showQr() {
		if(accountNumber!=null) {
			
		
		
		String upi = udao.fetchUpiId(accountNumber);
		if(upi==null) {
			Messagebox.show("Create UPI Id First...");
		}
		else {
			
			qrcode.setValue("upi://pa="+upi);
			
			upiLab.setValue(upi);
			qrcodeContainer.setVisible(true);
			
		}
		}
	}
	

	@Listen("onClick = #copyBtn")
	public void onCopy() {
	    String upi = upiLab.getValue();
	    Clients.evalJavaScript("navigator.clipboard.writeText('" + upi + "')");
	    Clients.showNotification("UPI ID copied!", "info", null, "middle_center", 2000);
	}

	

}
