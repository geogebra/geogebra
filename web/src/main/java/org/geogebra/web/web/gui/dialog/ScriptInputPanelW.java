/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */
package org.geogebra.web.web.gui.dialog;

import org.geogebra.common.gui.dialog.options.model.ScriptInputModel;
import org.geogebra.common.gui.dialog.options.model.ScriptInputModel.IScriptInputListener;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.plugin.ScriptType;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.gui.util.ScriptArea;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
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
	
	/**
	 * Input Dialog for a GeoButton object
	 * 
	 * @param app
	 *            application
	 * @param geo
	 *            element
	 * @param cols
	 *            number of columns
	 * @param rows
	 *            number of rows
	 * @param updateScript
	 *            whether this is for update script
	 * @param forceJavaScript
	 *            whether to only allow JS
	 * 
	 */
	public ScriptInputPanelW(AppW app, GeoElement geo, boolean updateScript,
			boolean forceJavaScript) {

		model = new ScriptInputModel(app, this, updateScript);

		inputPanel = new FlowPanel();
		textArea = new ScriptArea();
			textArea.addKeyUpHandler(new KeyUpHandler() {
				@Override
				public void onKeyUp(KeyUpEvent event) {
					applyScript();
				}
			});
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




		textArea.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
	            applyScript();
            }});
		
		languageSelector.addChangeHandler(new ChangeHandler(){

			@Override
			public void onChange(ChangeEvent event) {
				model.setScriptType(ScriptType.values()[languageSelector
				                						.getSelectedIndex()]);
            }});


		
		add(inputPanel);
		add(btPanel);
	}


	/**
	 * Returns the inputPanel and sets its preferred size from the given row and
	 * column value. Includes option to hide/show line numbering.
	 * 
	 * @param row
	 * @param column
	 * @param showLineNumbers
	 * @return
	 */
	public FlowPanel getInputPanel(int row, int column, boolean showLineNumbers) {
//
//		GDimensionW dim = inputPanel.getTextAreaComponent().getPreferredSizeFromRowColumn(row, column);
//		inputPanel.setPreferredSize(dim);
//		inputPanel.setShowLineNumbering(showLineNumbers);

		return inputPanel;

	}

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

	void applyScript() {
		processInput();
		model.setGeo(model.getGeo());
		
		
	}
	

	// private void setJSMode(boolean flag){
	// javaScript = flag;
	// ((GeoGebraEditorPane) inputPanel.getTextComponent()).setEditorKit(flag ?
	// "javascript":"geogebra");
	// }


	/**
	 * apply edit modifications
	 */
	public void applyModifications() {
		if (model.isEditOccurred()) {
			model.setEditOccurred(false);
			processInput();
		}
	}

//	public void changedUpdate(DocumentEvent e) {
//		// nothing to do
//
//	}
//
//	public void insertUpdate(DocumentEvent e) {
//		model.handleDocumentEvent();
//
//	}
//
//	public void removeUpdate(DocumentEvent e) {
//		model.handleDocumentEvent();
//
//	}

	
	public void updateFonts() {

//		Font font = app.getPlainFont();
//		languageSelector.setFont(font);
		

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
//		GeoGebraEditorPane editor = (GeoGebraEditorPane) inputPanel
//				.getTextComponent();
//		editor.getDocument().removeDocumentListener(this);
		languageSelector.setSelectedIndex(index);
//		editor.setEditorKit(name);
//		editor.getDocument().addDocumentListener(this);

	}

	public void setGeo(GeoElement button) {
		model.setGeo(button);
	}

	public void setGlobal() {
		model.setGlobal();
	}

	public FlowPanel getButtonPanel() {
	    return btPanel;
    }

	@Override
	public Object updatePanel(Object[] geos2) {
		return this;
	}

}
