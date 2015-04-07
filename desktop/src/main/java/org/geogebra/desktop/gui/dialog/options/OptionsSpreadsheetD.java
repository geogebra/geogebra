/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.desktop.gui.dialog.options;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.Border;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.main.settings.SpreadsheetSettings;
import org.geogebra.desktop.gui.view.spreadsheet.SpreadsheetView;
import org.geogebra.desktop.main.AppD;

/**
 * Panel with options for the spreadsheet view. G.Sturr 2010-3-5
 * 
 */
public class OptionsSpreadsheetD extends
		org.geogebra.common.gui.dialog.options.OptionsSpreadsheet implements
		OptionPanelD, ActionListener, FocusListener, SetLabels {

	private AppD app;
	private SpreadsheetView view;

	private JCheckBox cbShowFormulaBar, cbShowGrid, cbShowRowHeader,
			cbShowColumnHeader, cbShowHScrollbar, cbShowVScrollbar,
			cbAllowSpecialEditor, cbAllowToolTips, cbPrependCommands,
			cbEnableAutoComplete;

	private JPanel wrappedPanel;

	/**
	 * Creates a new dialog for the properties of the spreadsheet view.
	 */
	public OptionsSpreadsheetD(AppD app, SpreadsheetView view) {
		this.app = app;
		this.view = view;

		this.wrappedPanel = new JPanel();
		// build GUI
		initGUI();
		updateGUI();
		setLabels();
	}

	private SpreadsheetSettings settings() {
		return app.getSettings().getSpreadsheet();
	}

	private void initGUI() {

		wrappedPanel.removeAll();
		wrappedPanel.setLayout(new BorderLayout());
		wrappedPanel.add(new JScrollPane(buildLayoutOptionsPanel()),
				BorderLayout.CENTER);

		app.setComponentOrientation(wrappedPanel);
	}

	private JPanel buildLayoutOptionsPanel() {

		JPanel optionsPanel = new JPanel();
		optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));

		optionsPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		cbShowFormulaBar = new JCheckBox();
		cbShowFormulaBar.addActionListener(this);

		cbShowGrid = new JCheckBox();
		cbShowGrid.addActionListener(this);

		cbShowColumnHeader = new JCheckBox();
		cbShowColumnHeader.addActionListener(this);

		cbShowRowHeader = new JCheckBox();
		cbShowRowHeader.addActionListener(this);

		cbShowHScrollbar = new JCheckBox();
		cbShowHScrollbar.addActionListener(this);

		cbShowVScrollbar = new JCheckBox();
		cbShowVScrollbar.addActionListener(this);

		cbAllowSpecialEditor = new JCheckBox();
		cbAllowSpecialEditor.addActionListener(this);

		cbAllowToolTips = new JCheckBox();
		cbAllowToolTips.addActionListener(this);

		cbPrependCommands = new JCheckBox();
		cbPrependCommands.addActionListener(this);

		cbEnableAutoComplete = new JCheckBox();
		cbEnableAutoComplete.addActionListener(this);

		optionsPanel.add(cbShowFormulaBar);
		optionsPanel.add(cbShowGrid);
		optionsPanel.add(cbShowColumnHeader);
		optionsPanel.add(cbShowRowHeader);
		optionsPanel.add(cbShowVScrollbar);
		optionsPanel.add(cbShowHScrollbar);

		// spacer
		optionsPanel.add(Box.createVerticalStrut(16));

		optionsPanel.add(cbAllowSpecialEditor);
		optionsPanel.add(cbAllowToolTips);
		optionsPanel.add(cbPrependCommands);
		optionsPanel.add(cbEnableAutoComplete);

		return optionsPanel;
	}

	/**
	 * Update spreadsheet panel labels. Should be applied if the language was
	 * changed. Will be called after initialization automatically.
	 */
	public void setLabels() {

		// TODO -- add labels as needed
		cbShowFormulaBar.setText(app.getMenu("ShowInputField"));
		cbShowGrid.setText(app.getMenu("ShowGridlines"));
		cbShowColumnHeader.setText(app.getMenu("ShowColumnHeader"));
		cbShowRowHeader.setText(app.getMenu("ShowRowHeader"));
		cbShowHScrollbar.setText(app.getMenu("ShowHorizontalScrollbars"));
		cbShowVScrollbar.setText(app.getMenu("ShowVerticalScrollbars"));
		cbAllowSpecialEditor.setText(app.getMenu("UseButtonsAndCheckboxes"));
		cbAllowToolTips.setText(app.getMenu("AllowTooltips"));
		cbPrependCommands.setText(app.getMenu("RequireEquals"));
		cbEnableAutoComplete.setText(app.getMenu("UseAutoComplete"));
	}

	/**
	 * Save the settings of this panel.
	 */
	public void applyModifications() {
		// TODO -- add any settings that need changing on Apply button click
		// or after dialog close
	}

	public void updateGUI() {

		updateCheckBox(cbShowFormulaBar, settings().showFormulaBar());
		updateCheckBox(cbShowGrid, settings().showGrid());
		updateCheckBox(cbShowRowHeader, settings().showRowHeader());
		updateCheckBox(cbShowColumnHeader, settings().showColumnHeader());
		updateCheckBox(cbShowHScrollbar, settings().showHScrollBar());
		updateCheckBox(cbShowVScrollbar, settings().showVScrollBar());
		updateCheckBox(cbAllowSpecialEditor, settings().allowSpecialEditor());
		updateCheckBox(cbAllowToolTips, settings().allowToolTips());
		updateCheckBox(cbPrependCommands, settings().equalsRequired());
		updateCheckBox(cbEnableAutoComplete, settings().isEnableAutoComplete());

	}

	private void updateCheckBox(JCheckBox cb, boolean value) {
		cb.removeActionListener(this);
		cb.setSelected(value);
		cb.addActionListener(this);
	}

	public void actionPerformed(ActionEvent e) {
		doActionPerformed(e.getSource());
	}

	private void doActionPerformed(Object source) {

		// ========================================
		// layout options

		if (source == cbShowFormulaBar) {
			settings().setShowFormulaBar(cbShowFormulaBar.isSelected());
		}

		if (source == cbShowGrid) {
			settings().setShowGrid(cbShowGrid.isSelected());
		}

		else if (source == cbShowRowHeader) {
			settings().setShowRowHeader(cbShowRowHeader.isSelected());
		}

		else if (source == cbShowColumnHeader) {
			settings().setShowColumnHeader(cbShowColumnHeader.isSelected());
		}

		else if (source == cbShowHScrollbar) {
			settings().setShowHScrollBar(cbShowHScrollbar.isSelected());
		}

		else if (source == cbShowVScrollbar) {
			settings().setShowVScrollBar(cbShowVScrollbar.isSelected());
		}

		else if (source == cbAllowSpecialEditor) {
			settings().setAllowSpecialEditor(cbAllowSpecialEditor.isSelected());
		}

		else if (source == cbAllowToolTips) {
			settings().setAllowToolTips(cbAllowToolTips.isSelected());
		}

		else if (source == cbPrependCommands) {
			settings().setEqualsRequired(cbPrependCommands.isSelected());
		}

		else if (source == cbEnableAutoComplete) {
			settings().setEnableAutoComplete(cbEnableAutoComplete.isSelected());
		}

		updateGUI();
	}

	public void focusGained(FocusEvent arg0) {
	}

	public void focusLost(FocusEvent e) {
		doActionPerformed(e.getSource());
	}

	public JPanel getWrappedPanel() {
		return this.wrappedPanel;
	}

	public void revalidate() {
		getWrappedPanel().revalidate();

	}

	public void setBorder(Border border) {
		wrappedPanel.setBorder(border);
	}

	public void updateFont() {

		Font font = app.getPlainFont();

		cbShowFormulaBar.setFont(font);
		cbShowGrid.setFont(font);
		cbShowColumnHeader.setFont(font);
		cbShowRowHeader.setFont(font);
		cbShowHScrollbar.setFont(font);
		cbShowVScrollbar.setFont(font);
		cbAllowSpecialEditor.setFont(font);
		cbAllowToolTips.setFont(font);
		cbPrependCommands.setFont(font);
		cbEnableAutoComplete.setFont(font);

	}

	public void setSelected(boolean flag) {
		// see OptionsEuclidianD for possible implementation
	}

}
