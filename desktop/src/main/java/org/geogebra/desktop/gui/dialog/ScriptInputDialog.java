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
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;

import javax.swing.BorderFactory;
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
	 * @param app application
	 * @param title title
	 * @param button element with script
	 * @param cols number of columns
	 * @param rows number of rows
	 * @param updateScript whether it's for update script
	 * @param forceJavaScript whether to restrict language chooser to JS
	 */
	public ScriptInputDialog(AppD app, String title, GeoButton button, int cols,
			int rows, boolean updateScript, boolean forceJavaScript) {
		super(app.getFrame(), false, app.getLocalization());
		this.app = app;
		model = new ScriptInputModel(app, this, updateScript);

		createGUI(title, "", false, cols, rows, true, false, false, false,
				DialogType.GeoGebraEditor);

		// init dialog using text
		languageSelector = new JComboBox();
		for (ScriptType type : ScriptType.values()) {
			languageSelector.addItem(loc.getMenu(type.getName()));
		}
		languageSelector.addActionListener(this);

		model.setGeo(button);

		if (forceJavaScript) {
			languageSelector.setSelectedIndex(1);
			languageSelector.setEnabled(false);
		}
		btPanel.removeAll();
		btPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		btPanel.add(languageSelector, 0);

		JPanel centerPanel = new JPanel(new BorderLayout());
		centerPanel.add(inputPanel, BorderLayout.CENTER);

		wrappedDialog.getContentPane().add(centerPanel, BorderLayout.CENTER);

		centerOnScreen();

		inputPanel.getTextComponent().getDocument().addDocumentListener(this);
	}

	/**
	 * Returns the inputPanel and sets its preferred size from the given row and
	 * column value. Includes option to hide/show line numbering.
	 * 
	 * @param row number of rows
	 * @param column number of columns
	 * @return input panel
	 */
	public JPanel getInputPanel(int row, int column) {
		Dimension dim = ((GeoGebraEditorPane) inputPanel.getTextComponent())
				.getPreferredSizeFromRowColumn(row, column);
		inputPanel.setPreferredSize(dim);
		inputPanel.setShowLineNumbering(true);
		// add a small margin
		inputPanel.getTextComponent()
				.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
		return inputPanel;
	}

	private void processInput(AsyncOperation<Boolean> callback) {
		ScriptType type = ScriptType.values()[languageSelector
				.getSelectedIndex()];
		model.processInput(inputPanel.getText(), type, callback);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		try {
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
		} catch (Exception ex) {
			// do nothing on uninitializedValue
			ex.printStackTrace();
		}
	}

	/**
	 * Inserts geo into text and creates the string for a dynamic text, e.g.
	 * "Length of a = " + a + "cm"
	 * 
	 * @param geo element
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
	public void setInput(String text0, ScriptType type) {
		String text = text0;

		if (type == ScriptType.JAVASCRIPT) {
			text = JavaScriptBeautifier.format(text);
		}
		inputPanel.getTextComponent().setText(text);

		GeoGebraEditorPane editor = (GeoGebraEditorPane) inputPanel
				.getTextComponent();
		editor.getDocument().removeDocumentListener(this);
		languageSelector.removeActionListener(this);
		languageSelector.setSelectedIndex(type.ordinal());
		languageSelector.addActionListener(this);
		editor.setEditorKit(type.getXMLName());
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
