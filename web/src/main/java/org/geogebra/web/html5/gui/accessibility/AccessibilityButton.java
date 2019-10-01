package org.geogebra.web.html5.gui.accessibility;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.web.html5.gui.TabHandler;
import org.geogebra.web.html5.gui.util.GCustomButton;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;

/**
 * Provides accessibility features (tab handling) for a button
 *
 */
public class AccessibilityButton implements AccessibilityInterface {
	private GCustomButton button;
	private List<TabHandler> tabHandlers;
	private boolean ignoreTab = false;
	
	/**
	 * @param button
	 *            wrapped button
	 */
	public AccessibilityButton(GCustomButton button) {
		this.button = button;
		tabHandlers = new ArrayList<>();
	}
	
	@Override
	public void addTabHandler(TabHandler handler) {
		tabHandlers.add(handler);
	}

	/**
	 * @param event
	 *            browser event
	 * @return whether event was handled
	 */
	public boolean handleBrowserEvent(Event event) {
		int eventGetType = DOM.eventGetType(event);
		if (eventGetType == Event.ONKEYDOWN) {
			char keyCode = (char) event.getKeyCode();
			if (keyCode == '\t') {
				onTabPressed(event);
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
	public void setIgnoreTab() {
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

}
