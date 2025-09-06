package com.sdp.sendmoney;

import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Doublebox;
import org.zkoss.zul.Messagebox;

import com.sdp.transaction.TransferDao;
import com.ximpleware.VTDGen;

public class SendMoneytoAnother extends SelectorComposer<Div>{

	private static final long serialVersionUID = 1L;
	@Wire
	Combobox fromAccount;
	@Wire Combobox  toAccount;
	@Wire
	Doublebox amountBox;
	
	String accountnum;
	
	@Override
	public void doAfterCompose(Div comp) throws Exception {
		super.doAfterCompose(comp);
		accountnum = (String) Sessions.getCurrent().getAttribute("accountnumber");
		fromAccount.setValue(accountnum);
		fromAccount.setDisabled(true);
	}
	
	
	@Listen("onClick=#submitBtn1")
	public void sendmoney() {
		
		String fAccount = fromAccount.getValue();
		
		String tAccount = toAccount.getValue();
		double moneyAmount = amountBox.getValue();
		
		VTDGen vg = new VTDGen();
		String url = "http://192.168.5.118:8080/icici/transaction.jsp?accountNo="+tAccount+"&newBalance="+moneyAmount;
		boolean ss = vg.parseHttpUrl(url, false);
		toAccount.setValue("");
		amountBox.setValue(0.00);
		
		if(ss) {
		TransferDao tdao = TransferDao.getTransferDao();
		tdao.b2btransferAmount(fAccount, tAccount, moneyAmount, null, 0, null);
		}else {
			Messagebox.show("Transfer Failed Because Sever Down");
		}
		
	}
}
