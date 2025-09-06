package com.sdp.controller;

import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.sdp.dao.MPinDao;
import com.sdp.transaction.TransferDao;

public class MPinController extends SelectorComposer<Window> {

	private static final long serialVersionUID = 1L;

	@Wire
	private Textbox mpin;
	@Wire
	private Textbox confirmMpin;

	@Wire
	private Label errorLbl;

	transient MPinDao dao = new MPinDao();
	
	static final String PASSWORD="password";

	String accountNumber = (String) Sessions.getCurrent().getAttribute("accountnumber");

	@Override
	public void doAfterCompose(Window comp) throws Exception {
		super.doAfterCompose(comp);

		if (TransferDao.getTransferDao().isExistMpin(accountNumber) != 0) {
			showError("Pin already set!!");
			
		}
	}

	@Listen("onClick=#setBtn")
	public void onSetMpin() {
		if (TransferDao.getTransferDao().isExistMpin(accountNumber) != 0) {
			Messagebox.show("Pin already set!");
			showError("Pin already set!");
			
			return;
		}
		String mpinVal = mpin.getValue();
		String confirmVal = confirmMpin.getValue();

		if (!mpinVal.matches("\\d{4}")) {
			showError("MPIN must be 4 digits only");
			return;
		}

		if (!mpinVal.equals(confirmVal)) {
			showError("MPINs do not match");
			return;
		}

		if (dao.setMPin(Integer.parseInt(mpinVal), accountNumber) > 0) {

			showError("MPIN Set Successfully", false);
		} else {
			showError("Something went wrong!", true);
		}
	}

	public void showError(String msg) {
		showError(msg, true);
	}

	public void showError(String msg, boolean error) {
		errorLbl.setValue(msg);
		errorLbl.setStyle(error ? "color:red;" : "color:green;");
		errorLbl.setVisible(true);
	}

	@Listen("onClick=#showPassword")
	public void onShowPassword() {
		if (mpin.getType().equals(PASSWORD)) {
			mpin.setType("text");
		} else {
			mpin.setType(PASSWORD);
		}
	}

	@Listen("onClick=#showPassword2")
	public void onShowPassword2() {
		if (confirmMpin.getType().equals(PASSWORD)) {
			confirmMpin.setType("text");
		} else {
			confirmMpin.setType(PASSWORD);
		}
	}

}
