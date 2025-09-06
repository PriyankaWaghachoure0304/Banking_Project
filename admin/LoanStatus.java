package com.bank.admin;

import java.util.List;

import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Div;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;

import com.bank.customer.Customer;
import com.bank.dao.DataModel;

/**
 * LoanStatus controller for displaying loan applications based on their status
 * type (Approved, Waiting, Disbursed, or All).
 */
public class LoanStatus extends SelectorComposer<Div> {

	private static final long serialVersionUID = 1L;

	@Wire
	Listbox l1;
	DataModel dm = new DataModel();
	ListModelList<Customer> dataList = new ListModelList<>();

	@Override
	public void doAfterCompose(Div comp) throws Exception {
		super.doAfterCompose(comp);
		String type = (String) Sessions.getCurrent().getAttribute("statusType");
		/**
		 * Fetch records based on status type
		 */
		List<Customer> records;
		if ("approved".equalsIgnoreCase(type)) {
			records = dm.fetchAL();
			l1.setModel(new ListModelList<>(records));
			records = null;
		} else if ("waiting".equalsIgnoreCase(type)) {
			records = dm.fetchWL();
		} else if ("disbursed".equalsIgnoreCase(type)) {
			records = dm.fetchDL();
		} else if ("Alldata".equalsIgnoreCase(type)) {
			records = dm.fetchData();
		} else {
			records = null;
		}
		dataList.clear();
		/**
		 * Apply renderer & set model if records found
		 */
		if (records != null && !records.isEmpty()) {
			l1.setItemRenderer(new com.bank.controller.DisbursedListRenderer());
			l1.setModel(new ListModelList<>(records));
		}
	}
}
