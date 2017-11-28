package org.geogebra.web.html5.gui.accessibility;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.web.html5.gui.TabHandler;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.CustomButton;

public class AccessibilityButton implements AccessibilityInterface {
	private CustomButton button;
	private List<TabHandler> tabHandlers;
	private boolean ignoreTab=false;
	
	public AccessibilityButton(CustomButton button) {
		this.button = button;
		tabHandlers = new ArrayList<TabHandler>();
	}
	
	@Override
	public void addTabHandler(TabHandler handler) {
		tabHandlers.add(handler);
	}

	public boolean handleBrowserEvent(Event event) {
		int eventGetType = DOM.eventGetType(event);
		if (eventGetType == Event.ONKEYDOWN) {
			char keyCode = (char) event.getKeyCode();
			if (keyCode == '\t') {
				onTabPressed(event);
				return true;
			}
			if (keyCode == 'X' && event.getAltKey() == true
					&& event.getCtrlKey() == true) {
				focusInput();
				event.preventDefault();
				return true;
			}
		}
		return false;
	}

	private void onTabPressed(Event event) {
		for (TabHandler h : tabHandlers) {
			if (h.onTab(button, event.getShiftKey())) {
				event.stopPropagation();
				event.preventDefault();
			}
		}
	}

	@Override
	public void ignoreTab() {
		ignoreTab = true;
	}

	@Override
	public void setAltText(String text) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Call it at the end of button's onAttach()
	 * to prevent -1 turn automatically to 0.
	 */
	public void correctTabIndex() {
		if (ignoreTab) {
			button.getElement().setTabIndex(-1);
		}
	}

	public void focusInput() {
		if (button instanceof AccessibilityInterface) {
			((AccessibilityInterface) button).focusInput();
		}

	}
}
