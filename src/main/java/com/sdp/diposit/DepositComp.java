package com.sdp.diposit;

import java.util.Map;

import org.zkoss.zk.ui.event.InputEvent;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Notification;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Doublebox;
import org.zkoss.zul.Window;

import com.sdp.dao.DepositDao;

public class DepositComp extends SelectorComposer<Window> {

    private static final long serialVersionUID = 1L;

	@Wire
    Combobox accountCombo;

    @Wire
    Doublebox amountBox;

    private Map<String, String> accountMap; 

    @Override
    public void doAfterCompose(Window comp) throws Exception {
        super.doAfterCompose(comp);

        DepositDao dDao = new DepositDao();
        accountMap = dDao.getAllAccounts(); 
        loadAccounts(accountMap);
    }

    private void loadAccounts(Map<String, String> data) {
        accountCombo.getItems().clear();
        for (Map.Entry<String, String> entry : data.entrySet()) {
            Comboitem item = new Comboitem(entry.getValue() + " (" + entry.getKey() + ")");
            item.setValue(entry.getKey());
            accountCombo.appendChild(item);
        }
    }

    @Listen("onChanging=#accountCombo")
    public void onAccountTyping(InputEvent event) {
        String typedText = event.getValue().toLowerCase();

        accountCombo.getItems().clear();
        for (Map.Entry<String, String> entry : accountMap.entrySet()) {
            if (entry.getValue().toLowerCase().contains(typedText) ||
                entry.getKey().toLowerCase().contains(typedText)) {
                Comboitem item = new Comboitem(entry.getValue() + " (" + entry.getKey() + ")");
                item.setValue(entry.getKey());
                accountCombo.appendChild(item);
            }
        }
        accountCombo.open();
    }

    @Listen("onClick=#deposit")
    public void onDeposit() {
        String accNum = null;

        if (accountCombo.getSelectedItem() != null) {
            accNum = accountCombo.getSelectedItem().getValue();
        } else if (accountCombo.getValue() != null && !accountCombo.getValue().isEmpty()) {
            String typed = accountCombo.getValue().toLowerCase();
            for (Map.Entry<String, String> entry : accountMap.entrySet()) {
                if (entry.getValue().toLowerCase().contains(typed) || entry.getKey().equals(typed)) {
                    accNum = entry.getKey();
                    break;
                }
            }
        }

        if (accNum == null || amountBox.getValue() == null) {
            Notification.show("Please select account and enter amount");
            return;
        }

        Double amount = amountBox.getValue();

        DepositDao dDao = new DepositDao();
        dDao.deposit(accNum, amount);

        accountCombo.setValue("");
        amountBox.setValue(0.00);
    }
}
