package com.sdp.admin;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.*;

import com.sdp.controller.loancontroller.StatusController;
import com.sdp.customer.AccountOpening;
import com.sdp.customer.Customer;
/**
 * View controller for displaying detailed information of a loan application.
 */
public class View extends SelectorComposer<Div> {
	private static final long serialVersionUID = 1L;
	@Wire
	private Label appIdLbl;
	@Wire
	private Label nameLbl;
	@Wire
	private Label dobLbl;
	@Wire
	private Label genderLbl;
	@Wire
	private Label maritalLbl;
	@Wire
	private Label mobileLbl;
	@Wire
	private Label emailLbl;
	@Wire
	private Label occupationLbl;
	@Wire
	private Label employerLbl;
	@Wire
	private Label grossIncomeLbl;
	@Wire
	private Label netIncomeLbl;
	@Wire
	private Label loanAmtLbl;
	@Wire
	private Label tenureLbl;
	@Wire
	private Label purposeLbl;
	@Wire
	private Label repaymentLbl;
	@Wire
	private Label propertyTypeLbl;
	@Wire
	private Label propertyValLbl;
	@Wire
	private Label propertyAddrLbl;
	@Wire
	private Label emiLbl;
	@Wire
	private Label accountNoLbl;
	@Wire
	private Label statusLbl;

	@Override
	public void doAfterCompose(Div comp) throws Exception {
		super.doAfterCompose(comp);

		String appId = (String) Sessions.getCurrent().getAttribute("AccountNo");
		if (appId != null) {
			AccountOpening ao = new AccountOpening();
			Customer c = ao.getLoanApplicationByAc(appId);

			if (c != null) {
				appIdLbl.setValue(String.valueOf(c.getApplicationId()));
				nameLbl.setValue(c.getName());
				dobLbl.setValue(String.valueOf(c.getDob()));
				genderLbl.setValue(c.getGender());
				maritalLbl.setValue(c.getMaritalStatus());
				mobileLbl.setValue(c.getMobile());
				emailLbl.setValue(c.getEmail());
				occupationLbl.setValue(c.getOccupation());
				employerLbl.setValue(c.getEmployerName());
				grossIncomeLbl.setValue(String.valueOf(c.getGrossIncome()));
				netIncomeLbl.setValue(String.valueOf(c.getNetIncome()));
				loanAmtLbl.setValue(String.valueOf(c.getLoanAmount()));
				tenureLbl.setValue(String.valueOf(c.getTenureMonths()));
				purposeLbl.setValue(c.getPurpose());
				repaymentLbl.setValue(c.getRepaymentMode());
				propertyTypeLbl.setValue(c.getPropertyType());
				propertyValLbl.setValue(String.valueOf(c.getPropertyValue()));
				propertyAddrLbl.setValue(c.getPropertyAddress());
				emiLbl.setValue(String.valueOf(c.getEmi()));
				accountNoLbl.setValue(c.getAccountNo());
				statusLbl.setValue(c.getStatus());

			}
		}
	}

	@Listen("onClick=#bApprove")
	public void approveLoan()
	{
		
		StatusController s=new StatusController();
		s.approve();
		Executions.sendRedirect("/Admin/dashboard.zul");
	}
	@Listen("onClick=#bReject")
	public void rejectLoan()
	{
		StatusController s=new StatusController();
		s.reject();
	}
}