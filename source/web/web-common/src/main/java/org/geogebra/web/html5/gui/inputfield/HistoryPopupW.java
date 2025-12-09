/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.web.html5.gui.inputfield;

import java.util.ArrayList;

import org.geogebra.editor.share.util.GWTKeycodes;
import org.geogebra.web.html5.gui.GPopupPanel;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.event.dom.client.ChangeEvent;
import org.gwtproject.event.dom.client.ChangeHandler;
import org.gwtproject.event.dom.client.ClickEvent;
import org.gwtproject.event.dom.client.ClickHandler;
import org.gwtproject.event.dom.client.KeyUpEvent;
import org.gwtproject.event.dom.client.KeyUpHandler;
import org.gwtproject.user.client.ui.ListBox;
import org.gwtproject.user.client.ui.Panel;

/**
 * Popup for history of inputs
 */
public class HistoryPopupW extends GPopupPanel implements ClickHandler,
        KeyUpHandler, ChangeHandler, UpDownArrowHandler {

	private AutoCompleteTextFieldW textField;
	private boolean downPopup;
	private final ListBox historyList;
	private String originalTextEditorContent;
	private int historyIndex;
	private final ArrayList<String> history;

	/**
	 * @param app
	 *            app
	 * @param root
	 *            root for the popup
	 */
	public HistoryPopupW(AppW app, Panel root) {
		super(root, app);
		historyIndex = 0;
		history = new ArrayList<>(50);
		historyList = new ListBox();
		historyList.addChangeHandler(this);
		historyList.addKeyUpHandler(this);
		historyList.addClickHandler(this);
		historyList.addStyleName("historyList");

		add(historyList);
		addStyleName("GeoGebraPopup");
		setAutoHideEnabled(true);
	}

	/**
	 * @param isDownPopup
	 *            whether popup should be below the field
	 */
	public void setDownPopup(boolean isDownPopup) {
		this.downPopup = isDownPopup;
	}

	/**
	 * Show history popup
	 */
	public void showPopup() {
		ArrayList<String> list = history;
		if (list.isEmpty()) {
			return;
		}

		originalTextEditorContent = textField.getText();
		historyList.clear();
		historyList.setVisibleItemCount(Math.min(Math.max(list.size(), 2), 10));

		for (String link : list) {
			historyList.addItem(link);
		}

		show();
		setPopupPosition(textField.getAbsoluteLeft(),
				textField.getAbsoluteTop() - getOffsetHeight());

		historyList.setSelectedIndex(list.size() - 1);

		// focus one extra time in case the setText method would freeze
		// e.g. due to bad formula string
		historyList.setFocus(true);

		textField.setText(historyList.getItemText(historyList.getSelectedIndex()));

		historyList.setFocus(true);
	}

	/**
	 * @return whether popup is below the field
	 */
	public boolean isDownPopup() {
		return downPopup;
	}

	@Override
	public void onChange(ChangeEvent event) {
		textField.setText(historyList.getItemText(historyList.getSelectedIndex()));
	}

	@Override
	public void onKeyUp(KeyUpEvent event) {
		int charCode = event.getNativeKeyCode();
		switch (charCode) {
		default:
			// do nothing
			break;
		case GWTKeycodes.KEY_ESCAPE:
			hide();
			textField.setText(originalTextEditorContent);
			textField.setFocus(true);
			break;
		case GWTKeycodes.KEY_ENTER:
			hide();
			textField.setFocus(true);
			break;
		case GWTKeycodes.KEY_UP:
			handleUpArrow();
			historyList.setSelectedIndex(historyIndex);
			break;
		case GWTKeycodes.KEY_DOWN:
			handleDownArrow();
			historyList.setSelectedIndex(historyIndex);
			break;
		}
		event.stopPropagation();
	}

	@Override
	public void onClick(ClickEvent event) {
		hide();
	}

	public void setTextField(AutoCompleteTextFieldW textField) {
		this.textField = textField;
	}

	/**
	 * Add input to hinput history.
	 *
	 * @param str
	 *            input
	 */
	public void addToHistory(String str) {
		// exit if the new string is the same as the last entered string
		if (!history.isEmpty() && str.equals(history.get(history.size() - 1))) {
			return;
		}

		history.add(str);
		historyIndex = history.size();
	}

	@Override
	public void handleUpArrow() {
		if (!isShowing() && !isDownPopup()) {
			showPopup();
		} else {
			// Fix for Ticket #463
			String previousInput = getPreviousInput();
			if (previousInput != null) {
				textField.setText(previousInput);
			}
		}
	}

	@Override
	public void handleDownArrow() {
		if (!isShowing() && isDownPopup()) {
			showPopup();
		} else {
			// Fix for Ticket #463
			String nextInput = getNextInput();
			if (nextInput != null) {
				textField.setText(nextInput);
			}
		}
	}

	/**
	 * @return previous input from input textfield's history
	 */
	private String getPreviousInput() {
		if (history.isEmpty()) {
			return null;
		}
		if (historyIndex > 0) {
			--historyIndex;
		}
		return history.get(historyIndex);
	}

	/**
	 * @return next input from input textfield's history
	 */
	private String getNextInput() {
		if (historyIndex < history.size()) {
			++historyIndex;
		}
		if (historyIndex == history.size()) {
			return null;
		}

		return history.get(historyIndex);
	}
}
