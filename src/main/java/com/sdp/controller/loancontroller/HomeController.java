package com.sdp.controller.loancontroller;

import org.zkoss.zk.ui.event.MouseEvent;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Menupopup;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Window;
/**
 * HomeController manages UI interactions on the home page, 
 * specifically the behavior of the login tab hover menu.
 */
public class HomeController extends SelectorComposer<Window> {
	private static final long serialVersionUID = 1L;

	@Wire
	private Tab loginhover;

	@Wire
	private Menupopup tabPopup;

	@Listen("onMouseOver = #loginhover")
	public void showPopup(MouseEvent event) {
		tabPopup.open(loginhover, "after_start");
	}
	@Listen("onMouseOut = #loginhover")
	public void hidePopup(MouseEvent event) {
	    tabPopup.close();
	}

}
