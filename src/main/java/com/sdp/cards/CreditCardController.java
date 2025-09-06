package com.sdp.cards;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Div;

import com.sdp.dao.CreditCardDao;

public class CreditCardController extends SelectorComposer<Div>{
	
	
	private static final long serialVersionUID = 1L;

	@Wire
	Button show_creditCard;
	@Wire
	Button credit;
	
	Boolean isLoggedIn;
	@Override
	public void doAfterCompose(Div comp) throws Exception {
		super.doAfterCompose(comp);
		
		isLoggedIn = (Boolean) Sessions.getCurrent().getAttribute("isLoggedIn");
		String	accnumber = (String) Sessions.getCurrent().getAttribute("accountnumber");
		
		if (isLoggedIn != null && isLoggedIn) {
			CreditCardDao cdao = new CreditCardDao();
			boolean yes = cdao.fetchCraditcardApply(accnumber);
			
			if(yes) {
				show_creditCard.setVisible(true);
				credit.setVisible(false);
			} 
			else {
				credit.setVisible(true);
				show_creditCard.setVisible(false);
			}
		}
		else {
			Executions.getCurrent().sendRedirect("/LoginPage.zul");
		}
		
		
	}
	


}
