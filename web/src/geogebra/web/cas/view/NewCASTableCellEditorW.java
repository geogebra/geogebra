package geogebra.web.cas.view;

import geogebra.common.cas.view.CASTableCellEditor;
import geogebra.html5.gui.inputfield.AutoCompleteW;
import geogebra.html5.main.AppW;
import geogebra.html5.main.DrawEquationWeb;
import geogebra.web.gui.view.algebra.EquationEditor;
import geogebra.web.gui.view.algebra.EquationEditorListener;
import geogebra.web.gui.view.algebra.ScrollableSuggestionDisplay;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class NewCASTableCellEditorW extends Label implements
        CASTableCellEditor, CASEditorW,
        EquationEditorListener {

	// private AutoCompleteTextFieldW textField;
	private CASTableW table;
	private AppW app;
	private EquationEditor editor;
	private String input;
	private final SpanElement seMayLaTex;

	public NewCASTableCellEditorW(CASTableW table, AppW app,
	        final CASTableControllerW ml) {
		this.app = app;
		this.table = table;
		this.editor = new EquationEditor(app, this);
		this.seMayLaTex = DOM.createSpan().cast();
		DrawEquationWeb.drawEquationAlgebraView(seMayLaTex, "", true);
		EquationEditor.updateNewStatic(seMayLaTex);

		this.getElement().appendChild(seMayLaTex);

		/*
		 * textField = new AutoCompleteTextFieldW(0, app, true, null, true);
		 * textField.setCASInput(true); textField.setAutoComplete(true);
		 * textField.requestToShowSymbolButton();
		 * textField.showPopupSymbolButton(true);
		 * textField.addKeyPressHandler(new KeyPressHandler() {
		 * 
		 * @Override public void onKeyPress(KeyPressEvent event) { if
		 * (!textField.isSuggestionJustHappened()) { new
		 * KeyListenerW(ml).onKeyPress(event); } if (event.getCharCode() == 10
		 * || event.getCharCode() == 13) { event.preventDefault(); }
		 * textField.setIsSuggestionJustHappened(false); } });
		 * 
		 * textField.addBlurHandler(ml);
		 */
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
		return this.input;
	}

	public String getInput() {
		// TODO Auto-generated method stub
		return this.input;
	}

	public void setInputSelectionStart(int selStart) {
		// TODO Auto-generated method stub

	}

	public void setInputSelectionEnd(int selEnd) {
		// TODO Auto-generated method stub

	}

	public AutoCompleteW getWidget() {
		return this;
	}

	public void setLabels() {
		// TODO
	}

	public void setInput(String input) {
		editor.setText(input);
	}

	public void clearInputText() {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean getAutoComplete() {
		return true;
	}

	@Override
	public List<String> resetCompletions() {
		return editor.resetCompletions();
	}

	@Override
	public List<String> getCompletions() {
		return editor.getCompletions();
	}

	@Override
	public void setFocus(boolean b) {
		// TODO Auto-generated method stub

	}

	@Override
	public void insertString(String text) {
		// TODO Auto-generated method stub

	}

	@Override
	public void toggleSymbolButton(boolean toggled) {
		// TODO Auto-generated method stub

	}

	@Override
	public ArrayList<String> getHistory() {
		// TODO Auto-generated method stub
		return editor.getHistory();
	}

	@Override
	public String getText() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setText(String s) {
		editor.setText(s);

	}

	@Override
	public boolean isSuggesting() {
		return editor.getSug().isSuggestionListShowing();
	}

	@Override
	public void requestFocus() {
		// TODO Auto-generated method stub

	}

	@Override
	public Widget asWidget() {
		return this;
	}

	@Override
	public SpanElement getLaTeXSpan() {
		return this.seMayLaTex;
	}

	@Override
	public void updatePosition(ScrollableSuggestionDisplay sug) {
		sug.setPositionRelativeTo(this);

	}
}
