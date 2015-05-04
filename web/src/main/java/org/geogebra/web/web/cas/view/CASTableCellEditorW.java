package org.geogebra.web.web.cas.view;

import org.geogebra.common.cas.view.CASTableCellEditor;
import org.geogebra.web.html5.event.KeyListenerW;
import org.geogebra.web.html5.gui.inputfield.AutoCompleteTextFieldW;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;

public class CASTableCellEditorW implements CASTableCellEditor, CASEditorW {

	private AutoCompleteTextFieldW textField;
	private CASTableW table;
	private AppW app;

	public CASTableCellEditorW(CASTableW table, AppW app,
	        final CASTableControllerW ml) {
		this.app = app;
		this.table = table;
		textField = new AutoCompleteTextFieldW(0, app, true, null, true);
		textField.setCASInput(true);
		textField.setAutoComplete(true);
		textField.requestToShowSymbolButton();
		textField.showPopupSymbolButton(true);
		textField.addKeyPressHandler(new KeyPressHandler() {

			@Override
			public void onKeyPress(KeyPressEvent event) {
				if (!textField.isSuggestionJustHappened()) {
					new KeyListenerW(ml).onKeyPress(event);
				}
				if (event.getCharCode() == 10 || event.getCharCode() == 13) {
					event.preventDefault();
				}
				textField.setIsSuggestionJustHappened(false);
			}
		});

		textField.addBlurHandler(ml);
		// FIXME experimental fix for CAS in other languages, broken in r27612
		// This will update the CAS commands also
		app.updateCommandDictionary();
	}

	public int getInputSelectionEnd() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getInputSelectionStart() {
		// TODO Auto-generated method stub
		return 0;
	}

	public String getInputSelectedText() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getInput() {
		return textField.getText();
	}

	public void setInputSelectionStart(int selStart) {
		// TODO Auto-generated method stub

	}

	public void setInputSelectionEnd(int selEnd) {
		// TODO Auto-generated method stub

	}

	public AutoCompleteTextFieldW getWidget() {
		return textField;
	}

	public void setLabels() {
		textField.setDictionary(true);
	}

	public void setInput(String input) {
		this.textField.setText(input);
	}

	public void clearInputText() {
		// TODO Auto-generated method stub
	}

	public void resetInput() {
		// TODO Auto-generated method stub

	}
}
