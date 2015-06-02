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
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.gui.util.ScriptArea;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
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
	private AppW app;
	private FlowPanel btPanel;
	private Button btOk;
	private Button btCancel;
	private String inputText;
	/**
	 * Input Dialog for a GeoButton object
	 * 
	 * @param app
	 * @param title
	 * @param geo
	 * @param cols
	 * @param rows
	 * @param updateScript
	 * @param forceJavaScript
	 */
	public ScriptInputPanelW(AppW app, String title, GeoElement geo,
			int cols, int rows, boolean updateScript, boolean forceJavaScript) {

		this.app = app;
		model = new ScriptInputModel(app, this, updateScript, forceJavaScript);

		inputPanel = new FlowPanel();
		textArea = new ScriptArea();
		inputPanel.add(textArea);
		// init dialog using text

		btPanel = new FlowPanel();
		btPanel.setStyleName("optionsPanel");
		btOk = new Button();
		btCancel = new Button();
		
		FlowPanel centerPanel = new FlowPanel();

		languageSelector = new ListBox();
		for (ScriptType type : ScriptType.values()) {
			languageSelector.addItem(app.getPlain(type.getName()));
		}
		model.setGeo(geo);

		if (forceJavaScript) {
			languageSelector.setSelectedIndex(1);
			languageSelector.setEnabled(false);
			model.setScriptType(ScriptType.JAVASCRIPT);
		}

		btPanel.add(languageSelector);

		btOk.addClickHandler(new ClickHandler(){

			public void onClick(ClickEvent event) {
	            applyScript();
            }});

		btCancel.addClickHandler(new ClickHandler(){
	
			public void onClick(ClickEvent event) {
				model.setGeo(model.getGeo());
				
            }});
		
		textArea.addClickHandler(new ClickHandler(){

			public void onClick(ClickEvent event) {
	            applyScript();
            }});
		
		languageSelector.addChangeHandler(new ChangeHandler(){

			public void onChange(ChangeEvent event) {
				model.setScriptType(ScriptType.values()[languageSelector
				                						.getSelectedIndex()]);
            }});
		btPanel.add(btOk);
		btPanel.add(btCancel);
		
		add(inputPanel);
		add(btPanel);
	}

	public void setLabels(String ok, String cancel) {
		btOk.setText(ok);
		btCancel.setText(cancel);
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


	private boolean processInput() {
		inputText = textArea.getText();
		return model.processInput(inputText);
	}
	
	private void applyScript() {
		boolean finished = processInput();
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

	public void setInputText(String text) {
		textArea.setText(text);

	}

	public String getInputText() {
		return textArea.getText();
	}

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

	public Object update(Object[] geos2) {
		return this;
	}

}
