package com.sdp.admin;

import java.util.List;

import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Div;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;

import com.sdp.customer.Customer;
import com.sdp.dao.loandao.DataModel;

public class LoanStatus extends SelectorComposer<Div> {

    private static final long serialVersionUID = 1L;

    @Wire
    private Listbox l1;

    @Wire
    private Combobox filterBox;

    private DataModel dm = new DataModel();
    private ListModelList<Customer> dataList = new ListModelList<>();

    @Override
    public void doAfterCompose(Div comp) throws Exception {
        super.doAfterCompose(comp);

        String type = (String) Sessions.getCurrent().getAttribute("statusType");
        List<Customer> records;
        if ("approved".equalsIgnoreCase(type)) {
            records = dm.fetchAL();
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
        if (records != null && !records.isEmpty()) {
            l1.setItemRenderer(new com.sdp.controller.loancontroller.DisbursedListRenderer());
            dataList.addAll(records);
            l1.setModel(dataList);
        }

        filterBox.setSelectedIndex(0);
    }

    @Listen("onSelect=#filterBox")
    public void onFilterChange() {
        Comboitem selected = filterBox.getSelectedItem();
        if (selected != null) {
            loadData(selected.getValue());
        }
    }

    private void loadData(String filter) {
        List<Customer> records;

        switch (filter.toUpperCase()) {
            case "HOME LOAN": 
                records = dm.fetchByPurpose("Home Loan");
                break;
            case "PERSONAL LOAN":
                records = dm.fetchByPurpose("Personal Loan");
                break;
            case "ALL":
            default:
                records = dm.fetchData();
                break;
        }

        dataList.clear();
        if (records != null && !records.isEmpty()) {
            dataList.addAll(records);
        }
        l1.setModel(dataList);
    }

    @Listen("onClick=#download")
    public void download() {
        Comboitem selected = filterBox.getSelectedItem();
        String filter = (selected != null) ? selected.getValue() : "ALL";

        CustomerData c = new CustomerData();
        c.downloadData(filter);
    }
}
