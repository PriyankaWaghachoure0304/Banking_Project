package com.sdp.cards;


import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zul.Div;
import org.zkoss.zul.Messagebox;


public class DebitCardController extends SelectorComposer<Div>{
	
	
	private static final long serialVersionUID = 1L;

	
	Boolean isLoggedIn;
	@Override
	public void doAfterCompose(Div comp) throws Exception {
		super.doAfterCompose(comp);
		
		 isLoggedIn = (Boolean) Sessions.getCurrent().getAttribute("isLoggedIn");
		 Sessions.getCurrent().getAttribute("accountnumber");
		
		
		
		
	}
	
	@Listen("onClick=#debit")
	public void debitcard() {
		if (isLoggedIn != null && isLoggedIn) {
				Executions.getCurrent().sendRedirect("/cards/manage_debitcard.zul");
			}
		else {
			Messagebox.show("Login first!");
			
		}
	
	
}
}
