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

package org.geogebra.web.full.gui.dialog;

import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.gui.dialog.options.model.ScriptInputModel;
import org.geogebra.common.gui.dialog.options.model.ScriptInputModel.IScriptInputListener;
import org.geogebra.common.plugin.ScriptType;
import org.geogebra.web.full.gui.util.ScriptArea;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.gui.util.CancelEventTimer;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.ListBox;

/**
 * Input dialog for GeoText objects with additional option to set a
 * "LaTeX formula" flag
 *
 * @author hohenwarter
 */
public class ScriptInputPanelW extends FlowPanel implements
		IScriptInputListener {
	private ScriptInputModel model;
	private ListBox languageSelector;
	private FlowPanel inputPanel;
	private ScriptArea textArea;
	private FlowPanel btPanel;
	private AppW app;

	/**
	 * Input Dialog for a GeoButton object
	 *
	 * @param app
	 *            application
	 * @param model
	 *            panel model
	 *
	 */
	public ScriptInputPanelW(AppW app, ScriptInputModel model) {
		this.app = app;
		this.model = model;

		inputPanel = new FlowPanel();
		textArea = new ScriptArea(app);

		textArea.addKeyUpHandler(event -> applyScript());
		textArea.addBlurHandler(event -> applyScript());

		if (Browser.isTabletBrowser()) {
			textArea.enableGGBKeyboard();
		}

		inputPanel.add(textArea);
		// init dialog using text

		btPanel = new FlowPanel();
		btPanel.setStyleName("optionsPanel");

		languageSelector = new ListBox();

		for (ScriptType type : app.getEventDispatcher().availableTypes()) {
			languageSelector
					.addItem(app.getLocalization().getMenu(type.getName()));
		}

		if (model.isForcedJs()) {
			languageSelector.setSelectedIndex(1);
			languageSelector.setEnabled(false);
		}

		btPanel.add(languageSelector);

		ClickStartHandler.init(textArea, new ClickStartHandler() {
			@Override
			public void onClickStart(int x, int y, PointerEventType type) {
				showKeyboard();
				applyScript();
			}
		});

		languageSelector.addChangeHandler(event -> applyScript());

		add(inputPanel);
		add(btPanel);
	}

	/**
	 * Shows the keyboard.
	 */
	protected void showKeyboard() {
		app.updateKeyboardField(textArea);
		((AppWFull) app).getAppletFrame()
				.showKeyboard(true, textArea, false);
		CancelEventTimer.keyboardSetVisible();
	}

	/**
	 * Returns the inputPanel
	 *
	 * @return input panel
	 */
	public FlowPanel getInputPanel() {
		return inputPanel;
	}

	/**
	 * Update script in model
	 */
	void applyScript() {
		String inputText = textArea.getText();
		int selectedIndex = languageSelector
				.getSelectedIndex();
		if (selectedIndex >= 0) {
			ScriptType type = ScriptType.values()[selectedIndex];
			model.processInput(inputText, type);
		}
	}

	@Override
	public void setInput(String text, ScriptType type) {
		textArea.setText(text);
		boolean jsEnabled = app.getScriptManager().isJsEnabled();
		languageSelector.setVisible(jsEnabled);
		languageSelector.setSelectedIndex(jsEnabled ? type.ordinal() : 0);
	}

	@Override
	public Object updatePanel(Object[] geos2) {
		return this;
	}

}
