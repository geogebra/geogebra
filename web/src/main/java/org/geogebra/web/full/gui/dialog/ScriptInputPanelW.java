/*
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it
under the terms of the GNU General Public License as published by
the Free Software Foundation.

 */

package org.geogebra.web.full.gui.dialog;

import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.gui.dialog.options.model.ScriptInputModel;
import org.geogebra.common.gui.dialog.options.model.ScriptInputModel.IScriptInputListener;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.plugin.ScriptType;
import org.geogebra.web.full.gui.util.ScriptArea;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.gui.util.CancelEventTimer;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ListBox;

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
	 * @param geo
	 *            element
	 * @param updateScript
	 *            whether this is for update script
	 * @param forceJavaScript
	 *            whether to only allow JS
	 *
	 */
	public ScriptInputPanelW(AppW app, GeoElement geo, boolean updateScript,
			boolean forceJavaScript) {
		this.app = app;
		model = new ScriptInputModel(app, this, updateScript);

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
		for (ScriptType type : ScriptType.values()) {
			languageSelector
					.addItem(app.getLocalization().getMenu(type.getName()));
		}
		model.setGeo(geo);

		if (forceJavaScript) {
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
		app.updateKeyBoardField(textArea);
		((AppWFull) app).getAppletFrame()
				.showKeyBoard(true, textArea, false);
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

	private void processInput() {
		String inputText = textArea.getText();
		ScriptType type = ScriptType.values()[languageSelector
				.getSelectedIndex()];
		model.processInput(inputText, type, obj -> {});
	}

	/**
	 * Update script in model
	 */
	void applyScript() {
		processInput();
		model.setGeo(model.getGeo());
	}

	@Override
	public void setInput(String text, ScriptType type) {
		textArea.setText(text);
		boolean jsEnabled = app.getScriptManager().isJsEnabled();
		languageSelector.setVisible(jsEnabled);
		languageSelector.setSelectedIndex(jsEnabled ? type.ordinal() : 0);
	}

	/**
	 * @param button
	 *            construction element
	 */
	public void setGeo(GeoElement button) {
		model.setGeo(button);
	}

	/**
	 * Load global script for editing
	 */
	public void setGlobal() {
		model.setGlobal();
	}

	/**
	 * @return button panel
	 */
	public FlowPanel getButtonPanel() {
	    return btPanel;
    }

	@Override
	public Object updatePanel(Object[] geos2) {
		return this;
	}

}
