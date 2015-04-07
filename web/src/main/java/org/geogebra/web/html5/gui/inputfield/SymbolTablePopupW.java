package org.geogebra.web.html5.gui.inputfield;

import org.geogebra.common.gui.util.TableSymbols;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.HTMLTable.Cell;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

public class SymbolTablePopupW extends PopupPanel implements ClickHandler {

	SymbolTableW symbolTable = null;
	private AppW app;
	private AutoCompleteW textField;

	public SymbolTablePopupW(AppW app, AutoCompleteW autoCompleteTextField,
	        Widget invoker) {
		super(true);
		this.app = app;
		this.textField = autoCompleteTextField;
		createSymbolTable();

		this.addDomHandler(new MouseDownHandler() {

			@Override
			public void onMouseDown(MouseDownEvent event) {
				// used because autoCompleteTextField should not loose focus
				event.preventDefault();
			}
		}, MouseDownEvent.getType());

		// prevent autohide when clicking on the popup button
		addAutoHidePartner(invoker.getElement());
		addStyleName("SymbolTablePopup");
	}

	private void createSymbolTable() {
		String[] icons = TableSymbols.basicSymbols(app.getLocalization());
		String[] iconshelp = TableSymbols.basicSymbolsToolTips(app
		        .getLocalization());

		symbolTable = new SymbolTableW(icons, iconshelp);
		add(symbolTable);
		symbolTable.addClickHandler(this);
	}

	public void onClick(ClickEvent event) {
		// autoCompleteTextField should not loose focus
		this.textField.setFocus(true);
		Cell clickCell = ((HTMLTable) event.getSource()).getCellForEvent(event);
		textField.insertString(clickCell.getElement().getInnerText());
		hide();
	}

	/**
	 * Ensure the popup toggle button is updated after hiding
	 */
	@Override
	public void hide(boolean autoClosed) {
		super.hide(autoClosed);
		textField.toggleSymbolButton(false);
	}

}
