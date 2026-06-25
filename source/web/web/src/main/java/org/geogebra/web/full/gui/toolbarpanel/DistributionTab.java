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

package org.geogebra.web.full.gui.toolbarpanel;

import org.geogebra.common.io.layout.DockPanelData;
import org.geogebra.web.full.gui.toolbarpanel.tableview.StickyProbabilityTable;
import org.geogebra.web.full.gui.view.probcalculator.DistributionPanel;
import org.geogebra.web.full.gui.view.probcalculator.ProbabilityCalculatorViewW;

public class DistributionTab extends ToolbarTab {

	private final ToolbarPanel toolbarPanel;
	private DistributionPanel distrPanel;

	/**
	 * Constructor
	 * @param toolbarPanel parent toolbar panel
	 */
	public DistributionTab(ToolbarPanel toolbarPanel, StickyProbabilityTable table) {
		super(toolbarPanel);
		this.toolbarPanel = toolbarPanel;
		createContent(table);
	}

	private void createContent(StickyProbabilityTable table) {
		ProbabilityCalculatorViewW view = (ProbabilityCalculatorViewW) toolbarPanel.getApp()
				.getGuiManager().getProbabilityCalculator();
		distrPanel = new DistributionPanel(view, toolbarPanel.getApp());
		ProbabilityTableAdapter probTable = new ProbabilityTableAdapter(table,
				toolbarPanel.getApp(), view);
		view.setTable(probTable);
		view.updateDiscreteTable();
		view.updateLowHighResult();
		add(distrPanel);
	}

	@Override
	protected void onActive() {
		// to do fill
	}

	@Override
	public DockPanelData.TabIds getID() {
		return DockPanelData.TabIds.DISTRIBUTION;
	}

	@Override
	public void setLabels() {
		clear();
		ProbabilityCalculatorViewW view = (ProbabilityCalculatorViewW) toolbarPanel.getApp()
				.getGuiManager().getProbabilityCalculator();
		distrPanel = new DistributionPanel(view, toolbarPanel.getApp());
		add(distrPanel);
	}

	@Override
	public void open() {
		// to do fill
	}

	@Override
	public void close() {
		toolbarPanel.close(false);
	}
}
