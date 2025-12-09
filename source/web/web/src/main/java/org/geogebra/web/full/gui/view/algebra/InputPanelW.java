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

package org.geogebra.web.full.gui.view.algebra;

import org.geogebra.common.main.App;
import org.geogebra.web.html5.gui.inputfield.AutoCompleteTextFieldW;
import org.gwtproject.event.dom.client.KeyUpHandler;
import org.gwtproject.user.client.ui.FlowPanel;

import elemental2.dom.EventListener;

/**
 * Creates an InputPanel for GeoGebraWeb
 */
public class InputPanelW extends FlowPanel {

	private final AutoCompleteTextFieldW textComponent;

	/**
	 * @param app
	 *            application
	 * @param columns
	 *            number of columns
	 * @param autoComplete
	 *            whether to allow autocomplete
	 */
	public InputPanelW(App app, int columns, boolean autoComplete) {
		super();
		addStyleName("InputPanel");

		textComponent = new AutoCompleteTextFieldW(columns, app);
		textComponent.setAutoComplete(autoComplete);
		add(textComponent);
		enableGGBKeyboard(app, false, textComponent);
	}

	public InputPanelW(String initText, App app,
			boolean showSymbolPopupIcon) {
		this(initText, app, -1, showSymbolPopupIcon);
	}

	/**
	 * @param initText
	 *            initial text
	 * @param app
	 *            application
	 * @param columns
	 *            number of columns
	 * @param showSymbolPopupIcon
	 *            whether to show symbol icon
	 */
	public InputPanelW(String initText, App app, int columns,
			boolean showSymbolPopupIcon) {
		// set up the text component:
		textComponent = new AutoCompleteTextFieldW(columns, app);
		textComponent.prepareShowSymbolButton(showSymbolPopupIcon);
		if (initText != null) {
			textComponent.setText(initText);
		}
		add(textComponent);
		textComponent.setAutoComplete(false);
		enableGGBKeyboard(app, showSymbolPopupIcon, textComponent);
	}

	private void enableGGBKeyboard(App app, boolean showKeyboardButton,
			AutoCompleteTextFieldW atf) {
		if (!app.isWhiteboardActive()) {
			atf.prepareShowSymbolButton(showKeyboardButton);
			atf.enableGGBKeyboard();
		}
	}

	/**
	 * @return single line editable field
	 */
	public AutoCompleteTextFieldW getTextComponent() {
		return textComponent;
	}

	/**
	 * sets focus into textfield and selects the content
	 */
	public void setFocusAndSelectAll() {
		getTextComponent().setFocus(true);
		getTextComponent().selectAll();
	}

	/**
	 * @return text
	 */
	public String getText() {
		return textComponent.getText();
	}

	/**
	 * adds KeyUpHandler to TextComponent
	 * @param keyUpHandler Handler
	 */
	public void addTextComponentKeyUpHandler(KeyUpHandler keyUpHandler) {
		getTextComponent().addKeyUpHandler(keyUpHandler);
	}

	/**
	 * Adds a ChangeHandler to the text component
	 * @param listener EventListener
	 */
	public void addTextComponentInputListener(EventListener listener) {
		getTextComponent().addInputListener(listener);
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (textComponent != null) {
			textComponent.setVisible(visible);
		}
	}
	
	/**
	 * Sets the input field enabled/disabled
	 * @param b true iff input field should be enabled
	 */
	public void setEnabled(boolean b) {
		textComponent.setEditable(b);
	}
	
	/**
	 * @param app
	 *            application
	 * @return new AutoCompleteTextField
	 */
	public static AutoCompleteTextFieldW newTextComponent(App app) {
		return new InputPanelW(app, -1, false).getTextComponent();
	}
}
