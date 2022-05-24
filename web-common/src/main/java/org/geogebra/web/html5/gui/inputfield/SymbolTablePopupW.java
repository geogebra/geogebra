package org.geogebra.web.html5.gui.inputfield;

import org.geogebra.common.gui.util.TableSymbols;
import org.geogebra.common.main.Localization;
import org.geogebra.web.html5.gui.GPopupPanel;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.HTMLTable.Cell;
import com.google.gwt.user.client.ui.Widget;

public class SymbolTablePopupW extends GPopupPanel implements ClickHandler {

	SymbolTableW symbolTable = null;
	private AppW appw;
	private AutoCompleteTextFieldW textField;

	/**
	 * @param app
	 *            application
	 * @param autoCompleteTextField
	 *            text input
	 * @param invoker
	 *            button opening this table
	 */
	public SymbolTablePopupW(AppW app,
			AutoCompleteTextFieldW autoCompleteTextField,
	        Widget invoker) {
		super(true, app.getPanel(), app);
		this.appw = app;
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

		Localization loc = appw.getLocalization();

		String[][] map = TableSymbols.basicSymbolsMap(loc);

		String[] icons = TableSymbols.basicSymbols(loc, map);

		symbolTable = new SymbolTableW(icons);
		add(symbolTable);
		symbolTable.addClickHandler(this);
	}

	@Override
	public void onClick(ClickEvent event) {
		// autoCompleteTextField should not loose focus
		this.textField.setFocus(true);
		Cell clickCell = ((HTMLTable) event.getSource()).getCellForEvent(event);
		textField.insertString(clickCell.getElement().getInnerText());
	}
}
