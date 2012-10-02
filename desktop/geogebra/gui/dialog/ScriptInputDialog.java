/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */
package geogebra.gui.dialog;

import geogebra.common.gui.InputHandler;
import geogebra.common.gui.view.algebra.DialogType;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.geos.GeoButton;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.App;
import geogebra.common.plugin.EventType;
import geogebra.common.plugin.ScriptType;
import geogebra.common.plugin.script.Script;
import geogebra.gui.editor.GeoGebraEditorPane;
import geogebra.main.AppD;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Input dialog for GeoText objects with additional option to set a
 * "LaTeX formula" flag
 * 
 * @author hohenwarter
 */
public class ScriptInputDialog extends InputDialogD implements DocumentListener {

	private GeoElement geo;
	private boolean global = false;
	// private boolean javaScript = false;
	private ScriptType scriptType = ScriptType.GGBSCRIPT;
	private boolean updateScript = false;
	private JComboBox languageSelector;

	/**
	 * Input Dialog for a GeoButton object
	 * 
	 * @param app
	 * @param title
	 * @param button
	 * @param cols
	 * @param rows
	 * @param updateScript
	 * @param forceJavaScript
	 */
	public ScriptInputDialog(AppD app, String title, GeoButton button,
			int cols, int rows, boolean updateScript, boolean forceJavaScript) {
		super(app.getFrame(), false);
		this.app = app;

		this.updateScript = updateScript;
		inputHandler = new TextInputHandler();

		createGUI(title, "", false, cols, rows, true, false, false, false,
				DialogType.GeoGebraEditor);

		// init dialog using text

		JPanel centerPanel = new JPanel(new BorderLayout());

		languageSelector = new JComboBox();
		for (ScriptType type : ScriptType.values()) {
			languageSelector.addItem(app.getPlain(type.getName()));
		}
		languageSelector.addActionListener(this);

		setGeo(button);

		if (forceJavaScript) {
			languageSelector.setSelectedIndex(1);
			languageSelector.setEnabled(false);
			setScriptType(ScriptType.JAVASCRIPT);
		}
		btPanel.add(languageSelector, 0);

		centerPanel.add(inputPanel, BorderLayout.CENTER);

		wrappedDialog.getContentPane().add(centerPanel, BorderLayout.CENTER);

		centerOnScreen();

		inputPanel.getTextComponent().getDocument().addDocumentListener(this);
	}

	public void setGeo(GeoElement geo) {

		handlingDocumentEventOff = true;

		if (global) {
			setGlobal();
			handlingDocumentEventOff = false;
			return;
		}
		this.geo = geo;

		if (geo != null) {
			Script script = geo.getScript(
					updateScript ? EventType.UPDATE : EventType.CLICK);
			// Default to an empty Ggb script
			if (script == null) {
				script = app.createScript(ScriptType.GGBSCRIPT, "", false);
			}
			// App.debug(script.getText());
			inputPanel.setText(script.getText());
			setScriptType(script.getType());
		}

		handlingDocumentEventOff = false;
	}

	/**
	 * edit global javascript
	 */
	public void setGlobal() {

		boolean currentHandlingDocumentEventOff = handlingDocumentEventOff;
		handlingDocumentEventOff = true;

		geo = null;
		global = true;

		inputPanel.setText(app.getKernel().getLibraryJavaScript());

		handlingDocumentEventOff = currentHandlingDocumentEventOff;
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
	public JPanel getInputPanel(int row, int column, boolean showLineNumbers) {

		Dimension dim = ((GeoGebraEditorPane) inputPanel.getTextComponent())
				.getPreferredSizeFromRowColumn(row, column);
		inputPanel.setPreferredSize(dim);
		inputPanel.setShowLineNumbering(showLineNumbers);
		// add a small margin
		inputPanel.getTextComponent().setBorder(
				BorderFactory.createEmptyBorder(4, 4, 4, 4));

		return inputPanel;

	}

	public JPanel getInputPanel() {
		return inputPanel;
	}

	public JButton getApplyButton() {
		return btApply;
	}

	private boolean processInput() {
		inputText = inputPanel.getText();
		return inputHandler.processInput(inputText);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();

		try {
			if (source == btOK || source == inputPanel.getTextComponent()) {

				boolean finished = processInput();
				if (wrappedDialog.isShowing()) {
					// text dialog window is used and open
					setVisible(!finished);
				} else {
					// text input field embedded in properties window
					setGeo(getGeo());
				}
			} else if (source == btCancel) {
				if (wrappedDialog.isShowing())
					setVisible(false);
				else {
					setGeo(getGeo());
				}
			} else if (source == languageSelector) {
				// setJSMode(languageSelector.getSelectedIndex()==1);
				setScriptType(ScriptType.values()[languageSelector
						.getSelectedIndex()]);
			}
		} catch (Exception ex) {
			// do nothing on uninitializedValue
			ex.printStackTrace();
		}
	}

	// private void setJSMode(boolean flag){
	// javaScript = flag;
	// ((GeoGebraEditorPane) inputPanel.getTextComponent()).setEditorKit(flag ?
	// "javascript":"geogebra");
	// }

	private void setScriptType(ScriptType scriptType) {
		this.scriptType = scriptType;
		String scriptStr;
		int index = scriptType.ordinal();
		switch (scriptType) {
		default:
		case GGBSCRIPT:
			scriptStr = "geogebra";
			break;

		case PYTHON:
			App.debug("TODO");
			scriptStr = "javascript";// python";
			break;

		case JAVASCRIPT:
			scriptStr = "javascript";
			break;

		}

		GeoGebraEditorPane editor = (GeoGebraEditorPane) inputPanel
				.getTextComponent();
		editor.getDocument().removeDocumentListener(this);
		languageSelector.setSelectedIndex(index);
		editor.setEditorKit(scriptStr);
		editor.getDocument().addDocumentListener(this);
	}

	/**
	 * Inserts geo into text and creates the string for a dynamic text, e.g.
	 * "Length of a = " + a + "cm"
	 * 
	 * @param geo
	 */
	@Override
	public void insertGeoElement(GeoElement geo) {
		App.debug("TODO: unimplemented");
	}

	/**
	 * @return the geo
	 */
	public GeoElement getGeo() {
		return geo;
	}

	private class TextInputHandler implements InputHandler {

		private Kernel kernel;

		private TextInputHandler() {
			kernel = app.getKernel();
		}

		public boolean processInput(String inputValue) {
			if (inputValue == null)
				return false;

			if (global) {
				app.getKernel().setLibraryJavaScript(inputValue);
				return true;
			}

			if (getGeo() == null) {
				setGeo(new GeoButton(kernel.getConstruction()));

			}

			// change existing script
			Script script = app.createScript(scriptType, inputValue, true);
			if (updateScript) {
				getGeo().setUpdateScript(script);
				// let's suppose fixing this script removed the reason why
				// scripts were blocked
				app.setBlockUpdateScripts(false);
			} else {
				getGeo().setClickScript(script);
			}
			return true;
		}
	}

	/**
	 * apply edit modifications
	 */
	public void applyModifications() {
		if (editOccurred) {
			editOccurred = false;
			processInput();
		}
	}

	public void changedUpdate(DocumentEvent e) {
		// nothing to do

	}

	public void insertUpdate(DocumentEvent e) {
		handleDocumentEvent();

	}

	public void removeUpdate(DocumentEvent e) {
		handleDocumentEvent();

	}

	/**
	 * used for update to avoid several updates
	 */
	private boolean handlingDocumentEventOff = false;

	/**
	 * false on init, become true when an edit occurs
	 */
	private boolean editOccurred = false;

	private void handleDocumentEvent() {

		if (handlingDocumentEventOff)
			return;

		editOccurred = true;

	}


	@Override
	public void updateFonts() {

		super.updateFonts();

		Font font = app.getPlainFont();
		languageSelector.setFont(font);
		
		
		
	}

}
