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

package org.geogebra.desktop.gui.view.probcalculator;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;

import org.geogebra.common.gui.view.probcalculator.ProbabilityCalculatorStyleBar;
import org.geogebra.common.gui.view.probcalculator.ProbabilityCalculatorView;
import org.geogebra.desktop.gui.util.ToggleButtonD;
import org.geogebra.desktop.main.AppD;
import org.geogebra.desktop.main.LocalizationD;
import org.geogebra.desktop.util.GuiResourcesD;

/**
 * StyleBar for the ProbabilityCalculator view
 * 
 * @author G. Sturr
 * 
 */
public class ProbabilityCalculatorStyleBarD extends ProbabilityCalculatorStyleBar
		implements ActionListener {

	/** icon height in pixels */
	private int iconHeight = 18;

	JToolBar wrappedToolbar;

	private ToggleButtonD btnLineGraph;
	private ToggleButtonD btnStepGraph;
	private ToggleButtonD btnBarGraph;
	private ToggleButtonD btnExport;
	private ToggleButtonD btnNormalOverlay;

	private final LocalizationD loc;

	/**
	 * @param app
	 *            application
	 * @param probCalc
	 *            probability calculator
	 */
	public ProbabilityCalculatorStyleBarD(AppD app,
			ProbabilityCalculatorViewD probCalc) {
		super(app, probCalc);
		this.wrappedToolbar = new JToolBar();

		this.loc = app.getLocalization();
		wrappedToolbar.setFloatable(false);
		createGUI();
		updateLayout();
		updateGUI();
		setLabels();
	}

	/**
	 * Update icons for font size
	 */
	public void updateIcons() {
		if (btnLineGraph == null) {
			return;
		}
		iconHeight = ((AppD) getApp()).getScaledIconSize();
		btnLineGraph.setIcon(getScaledIcon(GuiResourcesD.LINE_GRAPH));
		btnStepGraph.setIcon(getScaledIcon(GuiResourcesD.STEP_GRAPH));
		btnBarGraph.setIcon(getScaledIcon(GuiResourcesD.BAR_GRAPH));
		btnExport.setIcon(getScaledIcon(GuiResourcesD.EXPORT16));
		btnNormalOverlay.setIcon(getScaledIcon(GuiResourcesD.NORMAL_OVERLAY));
	}

	private ImageIcon getScaledIcon(GuiResourcesD resource) {
		return ((AppD) getApp()).getScaledIcon(resource);
	}

	private void createGUI() {
		iconHeight = ((AppD) getApp()).getScaledIconSize();

		wrappedToolbar.removeAll();

		btnLineGraph = new ToggleButtonD(
				getScaledIcon(GuiResourcesD.LINE_GRAPH),
				iconHeight);
		btnLineGraph.addActionListener(this);

		btnStepGraph = new ToggleButtonD(
				getScaledIcon(GuiResourcesD.STEP_GRAPH),
				iconHeight);
		btnStepGraph.addActionListener(this);

		btnBarGraph = new ToggleButtonD(
				getScaledIcon(GuiResourcesD.BAR_GRAPH),
				iconHeight);
		btnBarGraph.addActionListener(this);

		ButtonGroup gp = new ButtonGroup();
		gp.add(btnBarGraph);
		gp.add(btnLineGraph);
		gp.add(btnStepGraph);

		// create export button
		btnExport = new ToggleButtonD(
				getScaledIcon(GuiResourcesD.EXPORT16), iconHeight);
		btnExport.setFocusable(false);
		btnExport.addActionListener(this);

		// create normal overlay button
		btnNormalOverlay = new ToggleButtonD(
				getScaledIcon(GuiResourcesD.NORMAL_OVERLAY),
				iconHeight);
		btnNormalOverlay.setFocusable(false);
		btnNormalOverlay.addActionListener(this);
	}

	/**
	 * Updates the button layout to fit the selected ProbabilityCalculator tab
	 */
	public void updateLayout() {

		wrappedToolbar.removeAll();

		if (((ProbabilityCalculatorViewD) getProbCalc()).isDistributionTabOpen()) {
			wrappedToolbar.add(btnLineGraph);
			wrappedToolbar.add(btnStepGraph);
			wrappedToolbar.add(btnBarGraph);
			wrappedToolbar.addSeparator();
			wrappedToolbar.add(btnNormalOverlay);

			wrappedToolbar.addSeparator();
			wrappedToolbar.add(btnExport);
		} else {
			// keep bar height uniform
			wrappedToolbar.add(Box.createVerticalStrut(20));
		}
		wrappedToolbar.revalidate();
		wrappedToolbar.repaint();
	}

	/**
	 * Updates the GUI
	 */
	public void updateGUI() {

		iconHeight = ((AppD) getApp()).getScaledIconSize();
		btnLineGraph.setVisible(getProbCalc().isDiscreteProbability());
		btnStepGraph.setVisible(getProbCalc().isDiscreteProbability());
		btnBarGraph.setVisible(getProbCalc().isDiscreteProbability());

		btnLineGraph.removeActionListener(this);
		btnStepGraph.removeActionListener(this);
		btnBarGraph.removeActionListener(this);
		btnNormalOverlay.removeActionListener(this);

		btnLineGraph.setSelected(getProbCalc()
				.getGraphType() == ProbabilityCalculatorView.GRAPH_LINE);
		btnStepGraph.setSelected(getProbCalc()
				.getGraphType() == ProbabilityCalculatorView.GRAPH_STEP);
		btnBarGraph.setSelected(getProbCalc()
				.getGraphType() == ProbabilityCalculatorView.GRAPH_BAR);

		btnNormalOverlay.setSelected(getProbCalc().isShowNormalOverlay());
		btnNormalOverlay.setVisible(getProbCalc().isOverlayDefined());
		btnLineGraph.addActionListener(this);
		btnStepGraph.addActionListener(this);
		btnBarGraph.addActionListener(this);
		btnNormalOverlay.addActionListener(this);
	}

	/**
	 * Updates localized labels
	 */
	public void setLabels() {
		btnExport.setToolTipText(loc.getMenu("Export"));
		btnLineGraph.setToolTipText(loc.getMenu("LineGraph"));

		btnStepGraph.setToolTipText(loc.getMenu("StepGraph"));
		btnBarGraph.setToolTipText(loc.getMenu("BarChart"));
		btnNormalOverlay.setToolTipText(loc.getMenu("OverlayNormalCurve"));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnLineGraph) {
			if (btnLineGraph.isSelected()) {
				getProbCalc()
						.setGraphType(ProbabilityCalculatorView.GRAPH_LINE);
			}
		}

		else if (e.getSource() == btnBarGraph) {
			if (btnBarGraph.isSelected()) {
				getProbCalc().setGraphType(ProbabilityCalculatorView.GRAPH_BAR);
			}
		}

		else if (e.getSource() == btnStepGraph) {
			if (btnStepGraph.isSelected()) {
				getProbCalc()
						.setGraphType(ProbabilityCalculatorView.GRAPH_STEP);
			}
		}

		else if (e.getSource() == btnNormalOverlay) {
			getProbCalc().setShowNormalOverlay(btnNormalOverlay.isSelected());
			getProbCalc().updateAll(false);
		}

		else if (e.getSource() == btnExport) {
			JPopupMenu menu = ((ProbabilityCalculatorViewD) getProbCalc())
					.getPlotPanel().getContextMenu();
			menu.show(btnExport,
					-menu.getPreferredSize().width + btnExport.getWidth(),
					btnExport.getHeight());
		}
	}

	/**
	 * @return the wrapped toolbar
	 */
	public JToolBar getWrappedToolbar() {
		return wrappedToolbar;
	}

}
