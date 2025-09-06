package com.sdp.cards;


import java.io.Serializable;
import java.sql.SQLException;
import java.util.List;

import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Div;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;

import com.sdp.dao.CraditCardGenarateDao;
import com.sdp.dao.CreditCardDao;
import com.sdp.model.CreditCard_apply;
/**
 * CustomerComposer is a ZK controller class that manages customer records 
 * and displays them in a  Listbox.
 */
public class CreditUser extends SelectorComposer<Div> implements Serializable {

	private static final long serialVersionUID = 1L;

	@Wire
	private Listbox ll1;

	private transient ListModelList<CreditCard_apply> dataList = new ListModelList<>();
	private transient CreditCardDao dm = new CreditCardDao();

	transient CreditCard_apply cpojo  = new CreditCard_apply();
	@Override
	public void doAfterCompose(Div comp) throws Exception {
		super.doAfterCompose(comp);
			fetchWaitingList();

			
			
		ll1.setModel(dataList);

	}

	private void fetchWaitingList() throws SQLException {
		List<CreditCard_apply> data = dm.fetchWL();
		dataList.clear();
		dataList.addAll(data);
	}


	@Listen("onClick = button")
	public void onApprove(Event event) throws SQLException {
		Button b = (Button) event.getTarget();
		if(b.getLabel().equals("Approve")) {
		Listitem li = (Listitem) event.getTarget().getParent().getParent().getParent();
		CreditCard_apply attr = li.getValue();
		String ac = attr.getAccountNumber();
		dm.updateApprove(ac);
		CraditCardGenarateDao cgdao = new CraditCardGenarateDao();
		double cardlimit = (double) Sessions.getCurrent().getAttribute("card_limit");
		cgdao.insertCreditCard(ac, cgdao.genCreditCardNumber(), cgdao.getFullNameUser(ac),cardlimit);
		Messagebox.show("Credit Card Genarated Successfully....");
		List<CreditCard_apply> data = dm.fetchWL();
		dataList.clear();
		dataList.addAll(data);
		
		}
		else if(b.getLabel().equals("Reject")) {
			Listitem li = (Listitem) event.getTarget().getParent().getParent().getParent();
			CreditCard_apply attr = li.getValue();
			String ac = attr.getAccountNumber();
			
			dm.updateReject(ac);
			List<CreditCard_apply> data = dm.fetchWL();
			dataList.clear();
			dataList.addAll(data);
		}
		
		
		
	}

}

