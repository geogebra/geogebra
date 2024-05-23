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
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.gui.UpdateFonts;
import org.geogebra.common.gui.dialog.options.model.ScriptInputModel;
import org.geogebra.common.gui.dialog.options.model.ScriptInputModel.IScriptInputListener;
import org.geogebra.common.gui.view.algebra.DialogType;
import org.geogebra.common.plugin.ScriptType;
import org.geogebra.common.util.debug.Log;
import org.geogebra.desktop.gui.editor.GeoGebraEditorPane;
import org.geogebra.desktop.gui.editor.JavaScriptBeautifier;
import org.geogebra.desktop.gui.view.algebra.InputPanelD;
import org.geogebra.desktop.main.AppD;

/**
 * Input dialog for GeoText objects with additional option to set a
 * "LaTeX formula" flag
 * 
 * @author hohenwarter
 */
public class ScriptInputDialog extends JPanel
		implements IScriptInputListener, DocumentListener, UpdateFonts, ActionListener, SetLabels {
	private final AppD app;
	private final ScriptInputModel model;
	private final JComboBox<String> languageSelector;
	private final InputPanelD inputPanel;

	/**
	 * Input Dialog for a GeoButton object
	 * 
	 * @param app application
	 * @param cols number of columns
	 * @param rows number of rows
	 */
	public ScriptInputDialog(AppD app, int cols,
			int rows, ScriptInputModel model) {
		super(new BorderLayout(0, 0));
		this.app = app;
		this.model = model;
		model.setListener(this);

		inputPanel = initInputPanel(rows, cols);
		// init dialog using text
		languageSelector = new JComboBox<>();
		JPanel btPanel = new JPanel();
		btPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		btPanel.add(languageSelector, 0);
		fillLanguageSelector();
		languageSelector.addActionListener(this);
		JPanel centerPanel = new JPanel(new BorderLayout());
		centerPanel.add(inputPanel, BorderLayout.CENTER);
		add(centerPanel, BorderLayout.CENTER);
		add(btPanel, BorderLayout.SOUTH);
		initInputPanel(rows, cols);
	}

	private void fillLanguageSelector() {
		for (ScriptType type : ScriptType.values()) {
			languageSelector.addItem(app.getLocalization().getMenu(type.getName()));
		}
		if (model.isForcedJs()) {
			languageSelector.setSelectedIndex(1);
			languageSelector.setEnabled(false);
		}
	}

	private InputPanelD initInputPanel(int rows, int cols) {
		InputPanelD scriptInputPanel = new InputPanelD("", app, rows, cols,
				false, DialogType.GeoGebraEditor);
		scriptInputPanel.getTextComponent().getDocument().addDocumentListener(this);
		scriptInputPanel.setShowLineNumbering(true);
		// add a small margin
		scriptInputPanel.getTextComponent()
				.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
		return scriptInputPanel;
	}

	private void processInput() {
		ScriptType type = getScriptType();
		model.processInput(inputPanel.getText(), type);
	}

	private ScriptType getScriptType() {
		return ScriptType.values()[languageSelector
				.getSelectedIndex()];
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			processInput();
			GeoGebraEditorPane editor = (GeoGebraEditorPane) inputPanel
					.getTextComponent();
			editor.setEditorKit(getScriptType());
		} catch (Exception ex) {
			// do nothing on uninitializedValue
			Log.debug(ex);
		}
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
		Font font = app.getPlainFont();
		languageSelector.setFont(font);
		inputPanel.updateFonts();
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
		editor.setEditorKit(type);
		editor.getDocument().addDocumentListener(this);
	}

	@Override
	public Object updatePanel(Object[] geos2) {
		return this;
	}

	@Override
	public void setLabels() {
		languageSelector.removeActionListener(this);
		languageSelector.removeAllItems();
		fillLanguageSelector();
		languageSelector.addActionListener(this);
	}
}
