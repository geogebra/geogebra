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
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.web.full.gui.util.ScriptArea;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.gui.util.CancelEventTimer;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ListBox;

/**
 * Input dialog for GeoText objects with additional option to set a
 * "LaTeX formula" flag
 *
 * @author hohenwarter
 */
public class ScriptInputPanelW extends FlowPanel implements
		IScriptInputListener /*,DocumentListener*/ {
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

		textArea.addKeyUpHandler(new KeyUpHandler() {
				@Override
				public void onKeyUp(KeyUpEvent event) {
					applyScript();
				}
			});

		textArea.addBlurHandler(new BlurHandler() {
			@Override
			public void onBlur(BlurEvent event) {
				applyScript();
			}
		});
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
			model.setScriptType(ScriptType.JAVASCRIPT);
		}

		btPanel.add(languageSelector);

		ClickStartHandler.init(textArea, new ClickStartHandler() {
			@Override
			public void onClickStart(int x, int y, PointerEventType type) {
				showKeyboard();
				applyScript();
			}
		});

		//
		// textArea.addClickHandler(new ClickHandler(){
		//
		// @Override
		// public void onClick(ClickEvent event) {
		// showKeyboard();
		// applyScript();
		// }});
		//
		languageSelector.addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {
				updateLanguage();
			}
		});

		add(inputPanel);
		add(btPanel);
	}

	/**
	 * Update model language from dropdown
	 */
	protected void updateLanguage() {
		model.setScriptType(
				ScriptType.values()[languageSelector.getSelectedIndex()]);
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
		model.processInput(inputText, new AsyncOperation<Boolean>() {

			@Override
			public void callback(Boolean obj) {
				// TODO Auto-generated method stub

			}
		});
	}

	/**
	 * Update script in model
	 */
	void applyScript() {
		processInput();
		model.setGeo(model.getGeo());
	}

	/**
	 * apply edit modifications
	 */
	public void applyModifications() {
		if (model.isEditOccurred()) {
			model.setEditOccurred(false);
			processInput();
		}
	}

	@Override
	public void setInputText(String text) {
		textArea.setText(text);
	}

	@Override
	public String getInputText() {
		return textArea.getText();
	}

	@Override
	public void setLanguageIndex(int index, String name) {
		languageSelector.setSelectedIndex(index);
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
