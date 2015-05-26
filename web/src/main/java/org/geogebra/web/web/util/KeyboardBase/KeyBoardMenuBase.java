package org.geogebra.web.web.util.KeyboardBase;

import org.geogebra.web.web.util.keyboard.KeyboardMode;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlexTable;

public class KeyBoardMenuBase extends FlexTable implements ClickHandler {

	private OnScreenKeyBoardBase keyboard;

	public KeyBoardMenuBase(OnScreenKeyBoardBase keyboard) {
		this.keyboard = keyboard;
		addStyleName("SymbolTable");
		setUpTable();
		addEventHandlers();
	}

	private void setUpTable() {
		KeyboardMode[] modes = KeyboardMode.values();
		for (int i = 0; i < modes.length; i++) {
			setText(0, i, modes[i].getInternalName());
		}
	}

	private void addEventHandlers() {
		addClickHandler(this);
	}

	public void onClick(ClickEvent event) {
		int cellIndex = getCellForEvent(event).getCellIndex();
		keyboard.setKeyboardMode(KeyboardMode.values()[cellIndex]);
	}

}
