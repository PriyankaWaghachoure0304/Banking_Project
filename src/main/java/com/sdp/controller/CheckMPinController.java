package com.sdp.controller;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Div;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;

import com.sdp.dao.MPinDao;
import com.sdp.model.TransferPojo;
import com.sdp.transaction.TransferDao;

public class CheckMPinController extends SelectorComposer<Div> {

	private static final long serialVersionUID = 1L;

	@Wire
	Textbox password;

	@Wire
	Div r_window;
	@Wire
	Div main_check_container;

	transient TransferDao tDao = TransferDao.getTransferDao();
 transient	MPinDao dao = new MPinDao();
static final String TRANSFER="transferDetail";
	@Override
	public void doAfterCompose(Div comp) throws Exception {
		super.doAfterCompose(comp);

		if (Sessions.getCurrent().getAttribute(TRANSFER) == null) {
			Executions.sendRedirect("/transactions/transfer.zul");
		}
	}

	@Listen("onClick=#submit")
	public void onSubmit() {
		if (Sessions.getCurrent().getAttribute(TRANSFER) == null) {
			Executions.sendRedirect("/transactions/transfer.zul");
		}
		int pass = Integer.parseInt(password.getValue());
		String accountNumber = (String) Sessions.getCurrent().getAttribute("accountnumber");

		if (dao.checkMPin(pass, accountNumber)) {
			TransferPojo tPojo = (TransferPojo) Sessions.getCurrent().getAttribute(TRANSFER);

			tDao.transferAmount(tPojo.getFromAccount(), tPojo.getToAccount(), tPojo.getAmount(),
					tPojo.getTransferType(), tPojo.getCharges(), tPojo.getRemark());
			Sessions.getCurrent().removeAttribute(TRANSFER);
			main_check_container.setVisible(false);
			r_window.setVisible(true);
		}

		else {
			Messagebox.show("Incorrect Password!");

		}

	}

	@Listen("onClick=#close")
	public void close() {
		Executions.sendRedirect("/homePage.zul");
	}

}
