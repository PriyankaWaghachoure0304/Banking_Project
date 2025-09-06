package com.sdp;

import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Div;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;

import com.sdp.connection.DbConnection;
import com.sdp.dao.AdminLoginDao;
import com.sdp.dao.LoginDao;

public class ValidateLogin extends SelectorComposer<Div> {
	private static final long serialVersionUID = 1L;
	@Wire
	Intbox cid;
	@Wire
	Textbox pass;

	@Wire
	Textbox te1;
	@Wire
	private Label idLabel;

	@Wire
	private Button sing;
	@Wire
	private Div roleSwitch;

	@Wire
	private Div userDiv;
	@Wire
	private Div adminDiv;

	private boolean isAdmin = false;

	@Wire
	private Button toggleEye;

	transient Execution exec = org.zkoss.zk.ui.Executions.getCurrent();

	DbConnection db = new DbConnection();

	@Listen("onOK=#cid")
	public void jumpToPass() {
		pass.setFocus(true);
	}

	@Listen("onOK=#pass")
	public void jumpToLogin() {
		login();
	}

	@Listen("onClick = #log1")
	public void login1() {
		exec.sendRedirect("LoginPage.zul");

	}

transient	LoginDao ldao = new LoginDao();

	public void signUpLogin(int custId, String password) {

		int s1 = custId;
		String s2 = password;

		if (ldao.validateLogin(s1, s2)) {
			exec.sendRedirect("homePage.zul");
		} else {
			Messagebox.show("Please Enter Valid Details!");
		}

	}

transient	AdminLoginDao adminDao = new AdminLoginDao();
	ValidateSignup vSignup = new ValidateSignup();

	@Listen("onClick = #login")
	public void login() {

		if (isAdmin) {
			Integer adminId = cid.getValue();
			String password = pass.getValue();

			if (adminId == null || password == null || password.isEmpty()) {
				Messagebox.show("Please enter Admin Id and Password");
				return;
			}

			if (adminDao.validateAdmin(adminId, password)) {
				exec.sendRedirect("/Admin/dashboard.zul");
				Sessions.getCurrent().setAttribute("adminIslogged", true);
			} else {
				Messagebox.show("Invalid Admin credentials!");
			}
		} else {

			int s1 = cid.getValue();
			String s2 = vSignup.digestPassword(pass.getValue());

			if (ldao.validateLogin(s1, s2)) {
				exec.sendRedirect("homePage.zul");
			} else {
				Messagebox.show("Please Enter Valid Details!");
			}
		}

	}

	@Listen("onClick = #sing")
	public void singUp() {
		exec.sendRedirect("SingUp.zul");
	}

	@Override
	public void doAfterCompose(Div comp) throws Exception {
		super.doAfterCompose(comp);
		roleSwitch.addEventListener(Events.ON_CLICK, e -> toggleRole(!isAdmin));

		userDiv.addEventListener(Events.ON_CLICK, e -> toggleRole(false));

		adminDiv.addEventListener(Events.ON_CLICK, e -> toggleRole(true));
	}

	private void toggleRole(boolean adminMode) {
		isAdmin = adminMode;

		roleSwitch.setSclass(isAdmin ? "toggle-switch active" : "toggle-switch");

		userDiv.setSclass(isAdmin ? "usertoggle" : "usertoggle active");
		adminDiv.setSclass(isAdmin ? "admintoggle active" : "admintoggle");

		if (isAdmin) {
			idLabel.setValue("Admin Id :");
			cid.setPlaceholder("Enter Admin Id");
			sing.setDisabled(true);

		} else {
			idLabel.setValue("Customer Id :");
			cid.setPlaceholder("Enter Customer Id");
			sing.setDisabled(false);
		}
	}
}
