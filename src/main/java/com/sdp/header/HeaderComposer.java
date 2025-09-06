package com.sdp.header;

import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Locale;

import org.zkoss.web.Attributes;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.MouseEvent;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zkmax.zul.Drawer;
import org.zkoss.zul.Button;
import org.zkoss.zul.Div;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Popup;

import com.sdp.dao.LoginDao;
import com.sdp.model.User;

public class HeaderComposer extends SelectorComposer<Div> {

	private static final long serialVersionUID = 1L;
	@Wire
	Div accounts;
	@Wire
	Div n_count;
	@Wire
	Popup drop1;
	@Wire
	Popup drop2;
	@Wire
	Popup drop3;
	@Wire
	Popup drop4;
	@Wire
	Popup drop5;
	@Wire
	Button login;
	@Wire
	Button logout;
	@Wire
	Button profile_logo;
	@Wire
	Button logout2;

	@Wire
	Image profile_image;
	@Wire Image  drawer_img;

	@Wire
	Label user_name;
	@Wire
	Label user_accountNumber;
	@Wire
	Label user_email;
	@Wire
	Label user_mobile;
	@Wire
	Label userName;
	@Wire
	Label tooltip_name;

	@Wire
	Div tab1;
	private List<Popup> allPopups;

	transient Execution exec = org.zkoss.zk.ui.Executions.getCurrent();

	Boolean isLoggedIn;
	Boolean isSignedup;
	String accountnum;
	transient User user;
	transient LoginDao ldao = new LoginDao();
	public static final String STYLE="background-color:none; border:none";
	@Override
	public void doAfterCompose(Div comp) throws Exception {
		super.doAfterCompose(comp);

		allPopups = Arrays.asList(drop1, drop2, drop3, drop4, drop5);
		isLoggedIn = (Boolean) Sessions.getCurrent().getAttribute("isLoggedIn");
		accountnum = (String) Sessions.getCurrent().getAttribute("accountnumber");
		isSignedup = (Boolean) Sessions.getCurrent().getAttribute("isSignedup");

		user = (User) Sessions.getCurrent().getAttribute("userDetails");

		if ((isLoggedIn != null && isLoggedIn) || (isSignedup != null && isSignedup)) {
			byte[] image = ldao.fetchImage(accountnum);

			String base64Image = image != null ? Base64.getEncoder().encodeToString(image) : "";

			n_count.setVisible(true);
			login.setVisible(false);
			logout.setVisible(true);
			logout2.setVisible(true);
			profile_image.setSrc("data:image/jpeg;base64," + base64Image);
			profile_image.setVisible(true);
			profile_logo.setVisible(false);

			drawer_img.setSrc("data:image/jpeg;base64," + base64Image);

			tooltip_name.setValue(user.getFullName());
			userName.setValue(user.getFullName());
			user_name.setValue(" : " + user.getFullName());
			user_accountNumber.setValue(" : " + user.getAccountNumber());

			user_email.setValue(" : " + user.getEmail());

			user_mobile.setValue(" : " + user.getPhoneNumber());

		} else {
			login.setVisible(true);
			logout.setVisible(false);
			logout2.setVisible(false);
			profile_image.setVisible(false);
			profile_logo.setVisible(true);

		}
	}

	private void closeAll() {
		for (Popup p : allPopups) {
			p.close();
		}
	}

	private static final String NAVHOVER = "background-color:#004aad;color:white !important; border-radius:9px 7px 0 0;";
	static final String AFTER="after_start";
	@Listen("onMouseOver=#accounts")
	public void accountsOpen(MouseEvent e) {

		drop1.open(accounts, AFTER);
		Div d = (Div) e.getTarget();
		d.setStyle(NAVHOVER);

	}

	@Listen("onClick=#accounts")
	public void accountsOpenOnClick(MouseEvent e) {
		closeAll();
		drop1.open(accounts, AFTER);
		Div d = (Div) e.getTarget();
		d.setStyle(NAVHOVER);

	}

	@Listen("onMouseOut=#accounts")
	public void accountsOut(MouseEvent e) {
		drop1.close();
		Div d = (Div) e.getTarget();
		d.setStyle(STYLE);
	}

	@Listen("onClick=#tab1, #transactionHistory, #check, #personalLoan,#homeLoan, #debit_Card, #credit_Card, #transferMoney, #createMpin, #b2b, #track, #createUPI, #myQR")
	public void accountsOutOnTabClicked(MouseEvent e) {
		closeAll();
	}

	@Listen("onMouseOver=#cards")
	public void cardsOpen(MouseEvent e) {
		closeAll();
		drop2.open(e.getTarget(), AFTER);
		Div d = (Div) e.getTarget();
		d.setStyle(NAVHOVER);
	}

	@Listen("onClick=#cards")
	public void cardsOpenOnClick(MouseEvent e) {
		closeAll();
		drop2.open(e.getTarget(), AFTER);
		Div d = (Div) e.getTarget();
		d.setStyle(NAVHOVER);
	}

	@Listen("onMouseOut=#cards")
	public void cardsOut(MouseEvent e) {
		drop2.close();
		Div d = (Div) e.getTarget();
		d.setStyle(STYLE);

	}

	@Listen("onMouseOver=#payments")
	public void paymentsOpen(MouseEvent e) {
		closeAll();
		drop3.open(e.getTarget(), AFTER);
		Div d = (Div) e.getTarget();
		d.setStyle(NAVHOVER);
	}

	@Listen("onClick=#payments")
	public void paymentsOpenOnClick(MouseEvent e) {
		closeAll();
		drop3.open(e.getTarget(), AFTER);
		Div d = (Div) e.getTarget();
		d.setStyle(NAVHOVER);
	}

	@Listen("onMouseOut=#payments")
	public void paymentsOut(MouseEvent e) {
		drop3.close();
		Div d = (Div) e.getTarget();
		d.setStyle(STYLE);
	}

	@Listen("onMouseOver=#loans")
	public void loansOpen(MouseEvent e) {
		closeAll();
		drop4.open(e.getTarget(), AFTER);
		Div d = (Div) e.getTarget();
		d.setStyle(NAVHOVER);
	}

	@Listen("onClick=#loans")
	public void loansOpenOnClick(MouseEvent e) {
		closeAll();
		drop4.open(e.getTarget(), AFTER);
		Div d = (Div) e.getTarget();
		d.setStyle(NAVHOVER);
	}

	@Listen("onMouseOut=#loans")
	public void loansOut(MouseEvent e) {
		drop4.close();
		Div d = (Div) e.getTarget();
		d.setStyle(STYLE);
	}

	@Listen("onMouseOver=#learn")
	public void learnOpen(MouseEvent e) {
		closeAll();
		drop5.open(e.getTarget(), AFTER);
		Div d = (Div) e.getTarget();
		d.setStyle(NAVHOVER);
	}

	@Listen("onClick=#learn")
	public void learnOpenOnClick(MouseEvent e) {
		closeAll();
		drop5.open(e.getTarget(), AFTER);
		Div d = (Div) e.getTarget();
		d.setStyle(NAVHOVER);
	}

	@Listen("onMouseOut=#learn")
	public void learnOut(MouseEvent e) {
		drop5.close();
		Div d = (Div) e.getTarget();
		d.setStyle(STYLE);
	}

	@Listen("onClick=#login")
	public void onLogin() {
		Executions.sendRedirect("/LoginPage.zul");
	}

	@Listen("onClick=#signup")
	public void onSignup() {

		if (isLoggedIn != null && isLoggedIn) {
			Messagebox.show("You Are Allready LogedIn...");
		} else {

			Executions.sendRedirect("/SingUp.zul");
		}
	}

	@Listen("onClick = #logout")
	public void logoutUser() {
		Sessions.getCurrent().invalidate();

		Executions.getCurrent().sendRedirect("homePage.zul");
	}

	@Listen("onClick = #logout2")
	public void logoutUser2() {
		logoutUser();
	}

	@Wire
	Drawer fi;

	@Listen("onClick=#profile_image")
	public void profileDrawer() {
		fi.open();
	}

	@Listen("onClick=#hindi")
	public void hindiLang() {
		Session session = Sessions.getCurrent();
		session.setAttribute(Attributes.PREFERRED_LOCALE, Locale.of("hi", "IN"));
		Executions.sendRedirect("");
	}

	@Listen("onClick=#english")
	public void englishLang() {
		Session session = Sessions.getCurrent();
		session.setAttribute(Attributes.PREFERRED_LOCALE, Locale.of("en", "IN"));
		Executions.sendRedirect("");
	}
}
