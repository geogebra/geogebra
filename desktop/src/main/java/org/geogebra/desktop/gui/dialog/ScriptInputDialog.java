/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */
package org.geogebra.desktop.gui.dialog;

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

import org.geogebra.common.gui.dialog.options.model.ScriptInputModel;
import org.geogebra.common.gui.dialog.options.model.ScriptInputModel.IScriptInputListener;
import org.geogebra.common.gui.view.algebra.DialogType;
import org.geogebra.common.kernel.geos.GeoButton;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.plugin.ScriptType;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.common.util.debug.Log;
import org.geogebra.desktop.gui.editor.GeoGebraEditorPane;
import org.geogebra.desktop.gui.editor.JavaScriptBeautifier;
import org.geogebra.desktop.main.AppD;

/**
 * Input dialog for GeoText objects with additional option to set a
 * "LaTeX formula" flag
 * 
 * @author hohenwarter
 */
public class ScriptInputDialog extends InputDialogD
		implements IScriptInputListener, DocumentListener {
	private ScriptInputModel model;
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
	public ScriptInputDialog(AppD app, String title, GeoButton button, int cols,
			int rows, boolean updateScript, boolean forceJavaScript) {
		super(app.getFrame(), false, app.getLocalization());
		this.app = app;
		model = new ScriptInputModel(app, this, updateScript);

		createGUI(title, "", false, cols, rows, true, false, false, false,
				DialogType.GeoGebraEditor);

		// init dialog using text

		JPanel centerPanel = new JPanel(new BorderLayout());

		languageSelector = new JComboBox();
		for (ScriptType type : ScriptType.values()) {
			languageSelector.addItem(loc.getMenu(type.getName()));
		}
		languageSelector.addActionListener(this);

		model.setGeo(button);

		if (forceJavaScript) {
			languageSelector.setSelectedIndex(1);
			languageSelector.setEnabled(false);
			model.setScriptType(ScriptType.JAVASCRIPT);
		}
		btPanel.add(languageSelector, 0);

		centerPanel.add(inputPanel, BorderLayout.CENTER);

		wrappedDialog.getContentPane().add(centerPanel, BorderLayout.CENTER);

		centerOnScreen();

		inputPanel.getTextComponent().getDocument().addDocumentListener(this);
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
		inputPanel.getTextComponent()
				.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

		return inputPanel;

	}

	public JPanel getInputPanel() {
		return inputPanel;
	}

	public JButton getApplyButton() {
		return btApply;
	}

	private void processInput(AsyncOperation<Boolean> callback) {
		model.processInput(inputPanel.getText(), callback);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();

		try {
			if (source == btOK || source == inputPanel.getTextComponent()) {

				processInput(new AsyncOperation<Boolean>() {

					@Override
					public void callback(Boolean finished) {
						if (wrappedDialog.isShowing()) {
							// text dialog window is used and open
							setVisible(!finished);
						} else {
							// text input field embedded in properties window
							model.setGeo(model.getGeo());
						}

					}
				});

			} else if (source == btCancel) {
				if (wrappedDialog.isShowing()) {
					setVisible(false);
				} else {
					model.setGeo(model.getGeo());
				}
			} else if (source == languageSelector) {
				// setJSMode(languageSelector.getSelectedIndex()==1);
				model.setScriptType(ScriptType.values()[languageSelector
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

	/**
	 * Inserts geo into text and creates the string for a dynamic text, e.g.
	 * "Length of a = " + a + "cm"
	 * 
	 * @param geo
	 */
	@Override
	public void insertGeoElement(GeoElement geo) {
		Log.debug("TODO: unimplemented");
	}

	/**
	 * apply edit modifications
	 */
	public void applyModifications() {
		if (model.isEditOccurred()) {
			model.setEditOccurred(false);
			processInput(new AsyncOperation<Boolean>() {

				@Override
				public void callback(Boolean obj) {
					// TODO Auto-generated method stub

				}
			});
		}
	}

	@Override
	public void changedUpdate(DocumentEvent e) {
		// nothing to do

	}

	@Override
	public void insertUpdate(DocumentEvent e) {
		model.handleDocumentEvent();

	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		model.handleDocumentEvent();

	}

	@Override
	public void updateFonts() {

		super.updateFonts();

		Font font = app.getPlainFont();
		languageSelector.setFont(font);

	}

	@Override
	public void setInputText(String text0) {
		String text = text0;

		if (model.getScriptType() == ScriptType.JAVASCRIPT) {
			text = JavaScriptBeautifier.format(text);
		}
		inputPanel.getTextComponent().setText(text);

	}

	@Override
	public String getInputText() {
		return inputPanel.getTextComponent().getText();
	}

	@Override
	public void setLanguageIndex(int index, String name) {
		GeoGebraEditorPane editor = (GeoGebraEditorPane) inputPanel
				.getTextComponent();
		editor.getDocument().removeDocumentListener(this);
		languageSelector.setSelectedIndex(index);
		editor.setEditorKit(name);
		editor.getDocument().addDocumentListener(this);

	}

	public void setGeo(GeoElement button) {
		model.setGeo(button);
	}

	public void setGlobal() {
		model.setGlobal();
	}

	@Override
	public Object updatePanel(Object[] geos2) {
		return this;
	}

}
