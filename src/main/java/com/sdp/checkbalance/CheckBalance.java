package com.sdp.checkbalance;

import org.apache.logging.log4j.core.Logger;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Div;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;

import com.sdp.apachelog4j.LoggerExample;
import com.sdp.dao.CheckBalanceDao;
import com.sdp.dao.MPinDao;

public class CheckBalance extends SelectorComposer<Div> {

	private static final long serialVersionUID = 1L;

	@Wire
	Label cAccount;
	@Wire
	Label cName;
	@Wire
	Label cBalance;

	@Wire
	Div detail;

	@Wire
	Textbox accountBox;
	@Wire
	Textbox password;

	@Wire
	Button check;

	@Wire
	Div checkpin_w;
	Logger logger = LoggerExample.getLogger();
	
	@Listen("onClick=#check")
	public void check() {
		checkpin_w.setVisible(true);
		password.setFocus(true);

	}

	Textbox t;
	Button sub;

	transient MPinDao dao = new MPinDao();

	String accountNumber;

	@Override
	public void doAfterCompose(Div comp) throws Exception {
		super.doAfterCompose(comp);
		accountNumber = (String) Sessions.getCurrent().getAttribute("accountnumber");

		if (accountNumber == null) {
			Messagebox.show("Please Login first!");
			check.setDisabled(true);
		}
		if (accountBox != null) {
			accountBox.setValue(accountNumber);
			accountBox.setDisabled(true);
		}

	}

	@Listen("onClick=#submit")
	public void onSubmit() {

		if (!password.getValue().equals("")) {
			checkpin_w.setVisible(false);

			int pass = Integer.parseInt(password.getValue());
			if (dao.checkMPin(pass, accountNumber)) {

				CheckBalanceDao cb = new CheckBalanceDao();

				String[] data = cb.checkBalance(accountNumber);

				detail.setVisible(true);
				cAccount.setValue(data[0]);
				cName.setValue(data[1]);
				cBalance.setValue("₹" + data[2]);

				password.setValue("");
				check.setDisabled(true);

			} else {
				Messagebox.show("Incorrect password!");
				logger.warn("Incorrect password");
			}

		} else {
			Messagebox.show("Invalid Input");
		}
	}

}