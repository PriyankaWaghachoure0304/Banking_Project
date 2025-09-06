package com.sdp.controller.loancontroller;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Div;
import org.zkoss.zul.Label;
public class HomeLoanController extends SelectorComposer<Div>{
	private static final long serialVersionUID = 1L;

	@Wire
	Label create;
	@Wire
	Button applyBtn;
	@Override
	public void doAfterCompose(Div comp) throws Exception {
		super.doAfterCompose(comp);
		 Sessions.getCurrent().getAttribute("loanType");
		if( Sessions.getCurrent().getAttribute("isLoggedIn")==null)
		{
		 
		 
			 create.setValue("First Create Your Account!!!");
		 }
		 else {
			 applyBtn.setVisible(true);
			 applyBtn.setLabel("Apply Now");
			 create.setValue("");
		 }
		}
	
	
	@Listen("onClick=#applyBtn")
	public void applyLoan()
	{
		Executions.sendRedirect("/Loan/account_opening.zul");
	}
}
