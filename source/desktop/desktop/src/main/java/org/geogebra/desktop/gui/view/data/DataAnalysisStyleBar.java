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

package org.geogebra.desktop.gui.view.data;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.gui.view.data.DataAnalysisModel;
import org.geogebra.common.gui.view.data.DataVariable.GroupType;
import org.geogebra.common.main.Localization;
import org.geogebra.desktop.gui.inputfield.MyTextFieldD;
import org.geogebra.desktop.gui.util.ToggleButtonD;
import org.geogebra.desktop.main.AppD;
import org.geogebra.desktop.util.GuiResourcesD;

/**
 * @author G. Sturr
 * 
 */
public class DataAnalysisStyleBar extends JToolBar implements ActionListener, SetLabels {

	private static final long serialVersionUID = 1L;

	private AppD app;
	private DataAnalysisViewD daView;
	protected int iconHeight = 18;
	private JButton btnRounding;
	private JButton btnPrint;
	private ToggleButtonD btnShowStatistics;
	private ToggleButtonD btnShowPlot2;
	private ToggleButtonD btnShowData;
	private ToggleButtonD btnDataSource;
	private MyTextFieldD fldDataSource;
	private ToggleButtonD btnExport;
	private ToggleButtonD btnSwapXY;

	/**
	 * @param app application
	 * @param statDialog stats dialog
	 */
	public DataAnalysisStyleBar(AppD app, DataAnalysisViewD statDialog) {

		this.daView = statDialog;
		this.app = app;
		this.setFloatable(false);
		createGUI();
		updateGUI();
		setLabels();
	}

	private void createGUI() {
		this.removeAll();

		btnPrint = new JButton(app.getMenuIcon(GuiResourcesD.DOCUMENT_PRINT));
		iconHeight = app.getScaledIconSize();

		btnPrint.addActionListener(this);
		btnPrint.setFocusPainted(false);
		btnPrint.setBorderPainted(false);
		btnPrint.setContentAreaFilled(false);
		btnPrint.setFocusable(false);

		btnShowStatistics = new ToggleButtonD(
				app.getScaledIcon(
						GuiResourcesD.STYLINGBAR_VARIABLE_ANALYSIS_STATISTICS),
				iconHeight);
		btnShowStatistics.addActionListener(this);
		btnShowStatistics.setFocusPainted(false);
		btnShowStatistics.setFocusable(false);

		btnShowData = new ToggleButtonD(
				app.getScaledIcon(
						GuiResourcesD.STYLINGBAR_VARIABLE_ANALYSIS_DATA),
				iconHeight);
		btnShowData.addActionListener(this);
		btnShowData.setFocusPainted(false);
		btnShowData.setFocusable(false);

		btnShowPlot2 = new ToggleButtonD(
				app.getScaledIcon(
						GuiResourcesD.STYLINGBAR_VARIABLE_ANALYSIS_2PLOT),
				iconHeight);
		btnShowPlot2.addActionListener(this);
		btnShowPlot2.setFocusPainted(false);
		btnShowPlot2.setFocusable(false);

		// create export button
		btnExport = new ToggleButtonD(
				app.getScaledIcon(GuiResourcesD.EXPORT16), iconHeight);
		btnExport.setFocusPainted(false);
		btnExport.setFocusable(false);
		btnExport.addActionListener(this);

		btnSwapXY = new ToggleButtonD(iconHeight);
		btnSwapXY.setSelected(!daView.getController().isLeftToRight());
		btnSwapXY.addActionListener(this);
		btnSwapXY.setFocusable(false);

		buildRoundingButton();
		createDataSourcePanel();

		// add(btnRounding);
		add(btnDataSource);
		addSeparator();
		add(btnShowStatistics);
		add(btnShowData);
		add(btnShowPlot2);
		add(btnSwapXY);
	}

	/**
	 * Rebuild the UI.
	 */
	public void reinit() {
		createGUI();
	}

	/**
	 * Update the UI
	 */
	public void updateGUI() {
		DataAnalysisModel model = daView.getModel();
		btnShowStatistics.setSelected(model.showStatPanel());
		if (model.showStatPanel() && daView.getStatisticsPanel().isVisible()) {
			daView.getStatisticsPanel().updatePanel();
		}

		switch (model.getMode()) {
		case DataAnalysisModel.MODE_ONEVAR:
			if (daView.getDataSource() != null
					&& daView.groupType() == GroupType.RAWDATA) {
				btnShowData.setVisible(true);
			} else {
				btnShowData.setVisible(false);
			}
			break;
		case DataAnalysisModel.MODE_REGRESSION:
			btnShowData.setVisible(true);
			break;
		case DataAnalysisModel.MODE_MULTIVAR:
			btnShowData.setVisible(false);
			break;
		default:
			btnShowData.setVisible(false);
		}

		btnShowData.setSelected(model.showDataPanel());

		btnShowPlot2.setVisible(!model.isMultiVar());
		btnShowPlot2.setSelected(model.showDataDisplayPanel2());

		// fldDataSource.setText(statDialog.getStatDialogController()
		// .getSourceString());
		fldDataSource.revalidate();

		btnSwapXY.setVisible(model.isRegressionMode());
		btnSwapXY.setSelected(!daView.getController().isLeftToRight());
	}

	private JPanel createDataSourcePanel() {

		btnDataSource = new ToggleButtonD(
				app.getScaledIcon(GuiResourcesD.ARROW_CURSOR_GRABBING),
				iconHeight);

		btnDataSource.addActionListener(this);
		fldDataSource = new MyTextFieldD(app);

		JPanel dataSourcePanel = new JPanel(new BorderLayout(5, 0));
		// dataSourcePanel.add(btnDataSource, app.borderWest());
		// dataSourcePanel.add(fldDataSource, BorderLayout.CENTER);

		dataSourcePanel.setBorder(BorderFactory.createEmptyBorder(1, 5, 1, 5));

		return dataSourcePanel;
	}

	/**
	 * Builds popup button with options menu items
	 */
	private void buildRoundingButton() {

		btnRounding = new JButton(
				app.getScaledIcon(GuiResourcesD.TRIANGLE_DOWN));
		btnRounding.setHorizontalTextPosition(SwingConstants.LEFT);
		btnRounding.setHorizontalAlignment(SwingConstants.LEFT);
	}

	@Override
	public void setLabels() {
		Localization loc = app.getLocalization();
		btnRounding.setText(loc.getMenu(".xx"));
		btnShowStatistics.setToolTipText(loc.getMenu("ShowStatistics"));
		btnShowData.setToolTipText(loc.getMenu("ShowData"));
		btnShowPlot2.setToolTipText(loc.getMenu("ShowPlot2"));
		btnPrint.setToolTipText(loc.getMenu("Print"));
		btnDataSource.setToolTipText(loc.getMenu("ShowDataSource"));

		String swapString = loc.getMenu("Column.X") + " \u21C6 "
				+ loc.getMenu("Column.Y");
		btnSwapXY.setFont(app.getPlainFont());
		btnSwapXY.setText(swapString);

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		DataAnalysisModel model = daView.getModel();
		Object source = e.getSource();
		if (source == btnShowStatistics) {
			model.setShowStatistics(btnShowStatistics.isSelected());
			updateGUI();
		} else if (source == btnShowData) {
			model.setShowDataPanel(btnShowData.isSelected());
			updateGUI();
		}

		else if (source == btnShowPlot2) {
			model.setShowComboPanel2(btnShowPlot2.isSelected());
			updateGUI();
		}

		else if (source == btnSwapXY) {
			daView.getController().swapXY();
			updateGUI();
		}

		else if (source == btnDataSource) {
			btnDataSource.setSelected(false);
			app.getDialogManager().showDataSourceDialog(model.getMode(), false);
		}

		else if (source == btnExport) {
			JPopupMenu menu = daView.getExportMenu();
			menu.show(btnExport, 0, btnExport.getHeight());
			btnExport.setSelected(false);
		}

		else if (source == btnPrint) {
			daView.doPrint();
		}

	}

}
