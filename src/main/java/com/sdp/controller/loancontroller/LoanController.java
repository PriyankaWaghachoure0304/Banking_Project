package com.sdp.controller.loancontroller;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Div;
import org.zkoss.zul.Textbox;
/**
 * LoanController manages the selection of loan types on the UI
 * and sets the corresponding session attribute before redirecting
 * to the appropriate loan application page.
 */

public class LoanController extends SelectorComposer<Div> {

	private static final long serialVersionUID = 1L;
	@Wire
	private Textbox loanType;
	
	/**
	 * Handles click event on the Personal Loan div.
	 * Sets the session attribute "loanType" to "Personal Loan" 
	 * and redirects to the personal loan application page.
	 */
	
	@Listen("onClick=#personalDiv")
	public void setSessionPersonal()
	{
		Sessions.getCurrent().setAttribute("loanType","Personal Loan");
		Executions.sendRedirect("Loan/personalloan.zul");
	}
	/**
	 * Handles click event on the Home Loan div.
	 * Sets the session attribute "loanType" to "Home Loan" 
	 * and redirects to the home loan application page.
	 */
	@Listen("onClick=#homeDiv")
	public void setSessionHome()
	{
		Sessions.getCurrent().setAttribute("loanType","Home Loan");
		Executions.sendRedirect("Loan/homeloan.zul");
	}
}
