package com.sdp.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.core.Logger;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Div;
import org.zkoss.zul.Include;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Popup;

import com.sdp.apachelog4j.LoggerExample;
import com.sdp.model.Notification;
import com.sdp.serialization.Deserialize;
import com.sdp.serialization.Serialize;

public class HomePage extends SelectorComposer<Div> {

	private static final long serialVersionUID = 1L;
	@Wire
	Include header_include;
	@Wire
	Include content;
	@Wire
	Div body_content;
	@Wire
	Div slider;
	@Wire
	Div ad_container;
	@Wire
	Div chat_div;
	@Wire
	Div hii_container;
	@Wire
	Div pop_con;

	@Wire
	Label userProfile;
	Boolean isLoggedIn;
	String accountnum;

	@Wire
	Popup notification_popup;

	@Wire
	Label msg_container;

	@Wire
	Popup notification_messages;

	@Wire
	Listbox notif_listbox;

	transient List<Notification> notification = new ArrayList<>();

	transient Deserialize deserialize = new Deserialize();
	transient Serialize serialize = new Serialize();

	Logger logger = LoggerExample.getLogger();
	static final String ACCOUNT="accountnumber";
	
	static final String MPIN="createMpin";
	
	static final String TMONEY="transferMoney";
	
	static final String THISTORY="transactionHistory";
	
	static final String NOTIFICATIONNO="notification_number";
	
	static final String LOGINMSG="Please Login!";
	
	static final String ONCLICK="onClick";
	@Override
	public void doAfterCompose(Div comp) throws Exception {
		super.doAfterCompose(comp);

		content.setSrc(null);
		slider.setVisible(true);
		ad_container.setVisible(true);

		logger.info("doaftercompose homepage");

		isLoggedIn = (Boolean) Sessions.getCurrent().getAttribute("isLoggedIn");
		accountnum = (String) Sessions.getCurrent().getAttribute(ACCOUNT);

		Label notificationCount = (Label) header_include.getFellow(NOTIFICATIONNO);

		notification = deserialize.loadMessage();
		List<Notification> l = new ArrayList<>();
		for (Notification n : notification) {
			if (n.getAccountNumber().equals(accountnum) && Boolean.TRUE.equals(Boolean.TRUE.equals(!n.getIsSeen()))) {
				l.add(n);
			}
		}
		notificationCount.setValue(l.size() + "");
		notif_listbox.setModel(new ListModelList<>(l));

		EventQueue<Event> q = EventQueues.lookup("notification", EventQueues.APPLICATION, true);

		q.subscribe(new EventListener<Event>() {

			@Override
			public void onEvent(Event e) throws Exception {
				logger.info("subscribe");
				if (isLoggedIn != null) {

					String msg = (String) e.getData();

					msg_container.setValue(msg);
					logger.info("new notif open");
					notification_popup.open(header_include, "after_center");
					pop_con.setSclass("show");
					notification = deserialize.loadMessage();

					List<Notification> l = new ArrayList<>();
					for (Notification n : notification) {
						if (n.getAccountNumber().equals(accountnum) && Boolean.TRUE.equals(!n.getIsSeen())) {
							l.add(n);

						}
					}
					Label notificationCount = (Label) header_include.getFellow(NOTIFICATIONNO);
					notificationCount.setValue(l.size() + "");
					notif_listbox.setModel(new ListModelList<>(l));

				}

			}
		});

		String username = (String) Sessions.getCurrent().getAttribute("username");
		if (username != null) {
			userProfile.setValue("Welcome : " + username + " " + ", Account Number Is : " + accountnum);

		}

		header_include.getFellow("tab1").addEventListener(ONCLICK, e -> {

			if (isLoggedIn == null) {

				openAccount();

				Executions.getCurrent().getDesktop().setBookmark("Account");

			} else if (Boolean.TRUE.equals(isLoggedIn)) {

				Messagebox.show("Already login!");

			}

		});

		header_include.getFellow("check").addEventListener(ONCLICK, e -> {

			if (isLoggedIn != null && isLoggedIn) {

				openCheckBalance();

				Executions.getCurrent().getDesktop().setBookmark("CheckBalance");

			}

			else {

				Messagebox.show(LOGINMSG);

			}

		});

		header_include.getFellow(THISTORY).addEventListener(ONCLICK, e -> {

			if (isLoggedIn != null && isLoggedIn) {

				openTransactionHistory();

				Executions.getCurrent().getDesktop().setBookmark(THISTORY);

			}

			else {

				Messagebox.show(LOGINMSG);

			}

		});

		header_include.getFellow(TMONEY).addEventListener(ONCLICK, e -> {

			if (isLoggedIn != null && isLoggedIn) {

				openMoneyTransefer();

				Executions.getCurrent().getDesktop().setBookmark(TMONEY);

			}

			else {

				Messagebox.show(LOGINMSG);

			}

		});

		header_include.getFellow(MPIN).addEventListener(ONCLICK, e -> {

			if (isLoggedIn != null && isLoggedIn) {

				openCreateMpin();

				Executions.getCurrent().getDesktop().setBookmark(MPIN);

			}

			else {

				Messagebox.show(LOGINMSG);

			}

		});

		header_include.getFellow("b2b").addEventListener(ONCLICK, e -> {

			if (isLoggedIn != null && isLoggedIn) {

				openB2BTransfer();

				Executions.getCurrent().getDesktop().setBookmark("B2B");

			}

			else {

				Messagebox.show(LOGINMSG);

			}

		});

		header_include.getFellow("createUPI").addEventListener(ONCLICK, e -> {

			if (isLoggedIn != null && isLoggedIn) {

				openCreateUpi();

				Executions.getCurrent().getDesktop().setBookmark("CreateUPI");

			}

			else {

				Messagebox.show(LOGINMSG);

			}

		});

		header_include.getFellow("myQR").addEventListener(ONCLICK, e -> {

			if (isLoggedIn != null && isLoggedIn) {

				openQR();

				Executions.getCurrent().getDesktop().setBookmark("QR");

			}

			else {

				Messagebox.show(LOGINMSG);

			}

		});

		header_include.getFellow("personalLoan").addEventListener(ONCLICK, e -> {

			openPersonalLoan();

			Executions.getCurrent().getDesktop().setBookmark("PersonalLoan");

		});

		header_include.getFellow("homeLoan").addEventListener(ONCLICK, e -> {

			openHomeLoan();

			Executions.getCurrent().getDesktop().setBookmark("HomeLoan");

		});

		header_include.getFellow("track").addEventListener(ONCLICK, e -> {

			if (isLoggedIn != null && isLoggedIn) {

				openTrackApplication();

				Executions.getCurrent().getDesktop().setBookmark("UserDashboard");

			}

			else {

				Messagebox.show(LOGINMSG);

			}

		});

		header_include.getFellow("notification_button").addEventListener(ONCLICK, e -> {

			notificationCount.setValue("0");

			openNotificationMessage();

		});

		header_include.getFellow("debit_Card").addEventListener(ONCLICK, e -> {

			if (isLoggedIn != null && isLoggedIn) {

				openDebitCard();

				Executions.getCurrent().getDesktop().setBookmark("Debit_Card");

			}

			else {

				Messagebox.show(LOGINMSG);

			}

		});

		header_include.getFellow("credit_Card").addEventListener(ONCLICK, e -> {

			if (isLoggedIn != null && isLoggedIn) {

				openCreditCard();

				Executions.getCurrent().getDesktop().setBookmark("Credit_Card");

			}

			else {

				Messagebox.show(LOGINMSG);

			}

		});

		header_include.getFellow("home").addEventListener(ONCLICK, e -> {
			content.setSrc(null);
			slider.setVisible(true);
			ad_container.setVisible(true);
			Executions.getCurrent().getDesktop().setBookmark("");
		});

	}

	@Listen("onClick=#scan")
	public void openScanner() {
		if (isLoggedIn != null && isLoggedIn) {

			body_content.setHeight(null);

			content.setSrc("/Qr/scanner.zul");

			slider.setVisible(false);

			ad_container.setVisible(false);
			
			content.getFellow("codescanner").addEventListener("onBarcodeScanned", new EventListener<Event>() {

				@Override
				public void onEvent(Event arg0) throws Exception {
					
					body_content.setHeight(null);

					content.setSrc("/Qr/paybyscanner.zul");

					slider.setVisible(false);

					ad_container.setVisible(false);
				}
			});
			
		} else {

			Messagebox.show(LOGINMSG);

		}

	}
	
	
	

	public void openDebitCard() {

		body_content.setHeight(null);

		content.setSrc("/cards/debitCard.zul");

		slider.setVisible(false);

		ad_container.setVisible(false);

		content.getFellow("debit").addEventListener(Events.ON_CLICK, new EventListener<Event>() {

			@Override

			public void onEvent(Event arg0) throws Exception {

				openManageDebitCard();

			}

		});

	}

	public void openManageDebitCard() {

		body_content.setHeight(null);

		content.setSrc("/cards/manage_debitcard.zul");

		slider.setVisible(false);

		ad_container.setVisible(false);

		content.getFellow("payCard").addEventListener(Events.ON_CLICK, new EventListener<Event>() {

			@Override

			public void onEvent(Event arg0) throws Exception {

				openPayWithCard();

			}

		});

		content.getFellow("pinset").addEventListener(Events.ON_CLICK, new EventListener<Event>() {

			@Override

			public void onEvent(Event arg0) throws Exception {

				openPinSet();

			}
		});
	}

	public void openPayWithCard() {

		body_content.setHeight(null);

		content.setSrc("/cards/payWithCard.zul");

		slider.setVisible(false);

		ad_container.setVisible(false);

	}

	public void openPinSet() {

		body_content.setHeight(null);

		content.setSrc("/cards/debit_card_pinset.zul");

		slider.setVisible(false);

		ad_container.setVisible(false);

	}

	public void openCreditCard() {

		body_content.setHeight(null);

		content.setSrc("/cards/creditCard.zul");

		slider.setVisible(false);

		ad_container.setVisible(false);

		content.getFellow("show_creditCard").addEventListener(Events.ON_CLICK, new EventListener<Event>() {

			@Override

			public void onEvent(Event arg0) throws Exception {

				openShowCreditCard();

			}

		});

		content.getFellow("credit").addEventListener(Events.ON_CLICK, new EventListener<Event>() {

			@Override

			public void onEvent(Event arg0) throws Exception {

				openApplyCreditCard();

			}

		});

	}

	private void openApplyCreditCard() {

		body_content.setHeight(null);

		content.setSrc("/cards/form_creditcard.zul");

		slider.setVisible(false);

		ad_container.setVisible(false);

	}

	private void openShowCreditCard() {

		Executions.sendRedirect("/zk/creditCard");

	}

	private void openNotificationMessage() {

		if (!fetchAllMessagesByAccountNumber().isEmpty()) {

			for (Notification n : notification) {
				if (n.getAccountNumber().equals(accountnum) && Boolean.TRUE.equals(!n.getIsSeen())) {
					n.setIsSeen(true);
					logger.info("seen");
				}
			}
			serialize.saveMessage(notification);

			notification_messages.open(header_include, "after_end");
			logger.info("notifi..  open");

		} else {
			deserialize.loadMessage();
			List<Notification> accMsg = fetchAllMessagesByAccountNumber();

			Label notificationCount = (Label) header_include.getFellow(NOTIFICATIONNO);
			notificationCount.setValue(accMsg.size() + "");
			notification_messages.close();

			logger.info("notif.. closed");
		}

	}

	public List<Notification> fetchAllMessagesByAccountNumber() {

		List<Notification> l1 = new ArrayList<>();
		for (Notification n : notification) {
			if (n.getAccountNumber().equals(accountnum) && Boolean.TRUE.equals(!n.getIsSeen())) {
				l1.add(n);
			}
		}
		return l1;

	}

	public void openAccount() {
		body_content.setHeight(null);
		content.setSrc("/Account_Opening_from.zul");
		slider.setVisible(false);
		ad_container.setVisible(false);

	}

	public void openPersonalLoan() {
		body_content.setHeight(null);
		content.setSrc("/Loan/personalloan.zul");
		slider.setVisible(false);
		ad_container.setVisible(false);
		Sessions.getCurrent().setAttribute("loanType", "Personal Loan");

	}

	public void openHomeLoan() {
		body_content.setHeight(null);
		content.setSrc("/Loan/homeloan.zul");
		slider.setVisible(false);
		ad_container.setVisible(false);
		Sessions.getCurrent().setAttribute("loanType", "Home Loan");

	}

	public void openTrackApplication() {
		body_content.setHeight(null);
		content.setSrc("/customer/dashboard.zul");
		slider.setVisible(false);
		ad_container.setVisible(false);
		Sessions.getCurrent().getAttribute(ACCOUNT);

	}

	private void openCheckBalance() {
		body_content.setHeight(null);
		content.setSrc("/CheckBalance/checkBalance.zul");
		slider.setVisible(false);
		ad_container.setVisible(false);
		Sessions.getCurrent().getAttribute(ACCOUNT);

	}

	private void openTransactionHistory() {
		body_content.setHeight(null);
		content.setSrc("/history/transaction_history.zul");
		slider.setVisible(false);
		ad_container.setVisible(false);
		Sessions.getCurrent().getAttribute(ACCOUNT);

	}

	private void openMoneyTransefer() {
		body_content.setHeight(null);
		content.setSrc("/transactions/transfer.zul");
		slider.setVisible(false);
		ad_container.setVisible(false);
		Sessions.getCurrent().getAttribute(ACCOUNT);

	}

	private void openCreateMpin() {
		body_content.setHeight(null);
		content.setSrc("/MPin/mPINcreate.zul");
		slider.setVisible(false);
		ad_container.setVisible(false);
		Sessions.getCurrent().getAttribute(ACCOUNT);

	}

	private void openB2BTransfer() {
		body_content.setHeight(null);
		content.setSrc("/bank-to-bank-Transfer/bank_transfer.zul");
		slider.setVisible(false);
		ad_container.setVisible(false);
		Sessions.getCurrent().getAttribute(ACCOUNT);

	}

	private void openCreateUpi() {
		body_content.setHeight(null);
		content.setSrc("/UPI/createUpi.zul");
		slider.setVisible(false);
		ad_container.setVisible(false);
		Sessions.getCurrent().getAttribute(ACCOUNT);

	}

	private void openQR() {
		body_content.setHeight("80vh");
		content.setSrc("/Qr/qrcode.zul");
		slider.setVisible(false);
		ad_container.setVisible(false);
		Sessions.getCurrent().getAttribute(ACCOUNT);

	}

	@Listen("onBookmarkChange = #home")

	public void book(Event e) {

		body_content.setHeight("130vh");

		String bookmark = Executions.getCurrent().getDesktop().getBookmark();

		switch (bookmark) {

		case "": {

			content.setSrc(null);

			slider.setVisible(true);

			ad_container.setVisible(true);

		}
			break;

		case "Account": {

			openAccount();

		}
			break;

		case "PersonalLoan": {

			openPersonalLoan();

			break;

		}

		case "HomeLoan": {

			openHomeLoan();

			break;

		}

		case "CheckBalance": {

			openCheckBalance();

			break;

		}

		case THISTORY: {

			openTransactionHistory();

			break;

		}

		case TMONEY: {

			openMoneyTransefer();

			break;

		}

		case MPIN: {

			openCreateMpin();

			break;

		}

		case "B2B": {

			openB2BTransfer();

			break;

		}

		case "CreateUPI": {

			openCreateUpi();

			break;

		}

		case "QR": {

			openQR();

			break;

		}

		case "Credit_Card": {

			openCreditCard();

			break;

		}

		case "Debit_Card": {

			openDebitCard();

			break;

		}

		case "UserDashboard": {

			openTrackApplication();

			break;

		}
		
		default:

		}

	}

	@Listen("onClick=#chatbot_container")
	public void openChat() {
		if (chat_div.isVisible()) {
			chat_div.setVisible(false);
		} else {
			chat_div.setVisible(true);
			hii_container.removeSclass();
			hii_container.setVisible(false);
		}

	}

	@Listen("onMouseOver=#chatbot_container")
	public void sayHii() {

		hii_container.setSclass("hii_container");
		hii_container.setVisible(true);

	}

	@Listen("onMouseOut=#chatbot_container")
	public void sayHiiRemove() {

		hii_container.removeSclass();
		hii_container.setVisible(false);

	}

	@Listen("onClick=#ok")
	public void load() {

		notification_popup.close();

	}

}
