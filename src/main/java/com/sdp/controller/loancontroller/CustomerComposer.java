package com.sdp.controller.loancontroller;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.List;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Div;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;

import com.sdp.customer.Customer;
import com.sdp.dao.loandao.DataModel;
/**
 * CustomerComposer is a ZK controller class that manages customer records 
 * and displays them in a  Listbox.
 */
public class CustomerComposer extends SelectorComposer<Div> implements Serializable {

	private static final long serialVersionUID = 1L;

	@Wire
	private Listbox l1;

	private ListModelList<Customer> dataList = new ListModelList<>();
	private DataModel dm = new DataModel();

	@Override
	public void doAfterCompose(Div comp) throws Exception {
		super.doAfterCompose(comp);
		String type = (String) Sessions.getCurrent().getAttribute("statusType");

		if ("waiting".equalsIgnoreCase(type)) {
			fetchWaitingList();
		} else {
			fetchRecords();
		}

		l1.setModel(dataList);

	}

	private void fetchWaitingList() throws SQLException {
		List<Customer> data = dm.fetchWL();
		dataList.clear();
		dataList.addAll(data);
	}

	public void fetchRecords() {
		List<Customer> data = dm.fetchData();
		dataList.clear();
		dataList.addAll(data);
	}

	@Listen("onClick = #l1 button")
	public void onView(Event event) {
		Listitem li = (Listitem) event.getTarget().getParent().getParent();
		Customer attr = li.getValue();
		Sessions.getCurrent().setAttribute("AccountNo",attr.getAccountNumber());

		Executions.sendRedirect("/Admin/view.zul");
	}

}
