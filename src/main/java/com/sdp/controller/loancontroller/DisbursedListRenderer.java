package com.sdp.controller.loancontroller;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.sdp.customer.Customer;
/**
 * Custom renderer for displaying Customer objects in a ZK
 */
public class DisbursedListRenderer implements ListitemRenderer<Customer> {
    @Override
    public void render(Listitem item, Customer data, int index) throws Exception {
    	if (index % 2 == 0) {
            item.setSclass("evenRow");
        } else {
            item.setSclass("oddRow");
        }
    	
    	
    	item.appendChild(new Listcell(String.valueOf(data.getAccountNumber())));
        item.appendChild(new Listcell(data.getName()));
        item.appendChild(new Listcell(data.getMobile()));
        item.appendChild(new Listcell(data.getPurpose()));
        item.appendChild(new Listcell(data.getStatus()));
    }

}
