package com.sdp.upi;

import javax.mail.MessagingException;

import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Div;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;

import com.sdp.dao.UpiDao;

public class GenerateUpi extends SelectorComposer<Div>{
	private static final long serialVersionUID = 1L;
	@Wire
	Textbox cardnum;
	@Wire
	Intbox cardpin;
	@Wire
	Label lid;
	
	String accountnum;
	
	@Override
	public void doAfterCompose(Div comp) throws Exception {
		
		super.doAfterCompose(comp);
		accountnum = (String) Sessions.getCurrent().getAttribute("accountnumber");
		
	}
	String upiid;
	
	@Listen("onClick=#generate")
	public void generateUPI() {
		String cardnumber = cardnum.getValue();
		int cpin = cardpin.getValue();
		
		UpiDao udao = new UpiDao();
		if(udao.validatecard(accountnum, cardnumber, cpin))
		{
			upiid = udao.generateUpi(accountnum);
			if(upiid!=null) {
				Messagebox.show("Your UPI Id Is : "+upiid);
				String toemail = udao.getMail(accountnum);
				try {
					udao.sendEmail(toemail, upiid);
					cardnum.setValue("");
					cardpin.setValue(null);
				} catch (MessagingException e) {
					e.printStackTrace();
				}
				lid.setValue(upiid);
			}
			else {
				Messagebox.show("UPI Id Generated Failed...");
			}
		}
		else {
			Messagebox.show("Please Enter Valid Details!");
		}
	}
	


}
