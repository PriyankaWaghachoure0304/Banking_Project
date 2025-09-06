package com.sdp.qr;

import java.util.Map;
import java.util.Map.Entry;

import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zul.Div;

public class Scanner extends SelectorComposer<Div> {
	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unchecked")
	@Listen("onDetect=#codescanner")
	public void codescanner(Event event) {
		String upi;
		Map<String, String> map =  (Map<String, String>) event.getData();
		for(Entry<String, String> s : map.entrySet()) {
			if(s.getKey().equals("result")) {
				upi=s.getValue();
				String mainUpi = upi.substring(9);
				
				Sessions.getCurrent().setAttribute("upiId", mainUpi);
				
				 Events.postEvent("onBarcodeScanned", event.getTarget(), mainUpi);
				
				
			}
		}
		
	}
}
