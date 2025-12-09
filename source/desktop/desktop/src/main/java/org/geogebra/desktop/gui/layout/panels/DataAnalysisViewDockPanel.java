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

package org.geogebra.desktop.gui.layout.panels;

import javax.swing.JComponent;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.main.App;
import org.geogebra.desktop.gui.GuiManagerD;
import org.geogebra.desktop.gui.layout.DockPanelD;
import org.geogebra.desktop.main.AppD;

/**
 * Dock panel for the probability calculator.
 */
public class DataAnalysisViewDockPanel extends DockPanelD {
	private static final long serialVersionUID = 1L;
	private AppD app;

	/**
	 * @param app application
	 */
	public DataAnalysisViewDockPanel(AppD app) {
		super(App.VIEW_DATA_ANALYSIS, // view id
				"DataAnalysis", // view title phrase
				getDefaultToolbar(), // toolbar string
				true, // style bar?
				-1, // menu order
				'D' // menu shortcut
		);

		this.app = app;
		this.setOpenInFrame(false);
		this.setDialog(true);
		this.setShowStyleBar(true);

	}

	@Override
	protected JComponent loadComponent() {
		return (JComponent) app.getGuiManager().getDataAnalysisView();
	}

	@Override
	protected JComponent loadStyleBar() {
		return ((GuiManagerD) app.getGuiManager()).getDataAnalysisView()
				.getStyleBar();
	}

	private static String getDefaultToolbar() {
		StringBuilder sb = new StringBuilder();

		sb.append(EuclidianConstants.MODE_MOVE);
		sb.append(" || ");
		sb.append(EuclidianConstants.MODE_SPREADSHEET_ONEVARSTATS);
		sb.append(" || ");
		sb.append(EuclidianConstants.MODE_SPREADSHEET_TWOVARSTATS);
		sb.append(" || ");
		sb.append(EuclidianConstants.MODE_SPREADSHEET_MULTIVARSTATS);

		return sb.toString();
	}

}
