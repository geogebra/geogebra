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

import org.geogebra.common.gui.view.data.DataAnalysisController;
import org.geogebra.desktop.main.AppD;

/**
 * Class to control data management for the DataAnalysisView.
 * 
 * @author G. Sturr
 * 
 */
public class DataAnalysisControllerD extends DataAnalysisController {

	private DataAnalysisViewD view;

	/**
	 * @param app application
	 * @param view DA dialog
	 */
	public DataAnalysisControllerD(AppD app, DataAnalysisViewD view) {
		super(app);
		this.view = view;
	}

	/**
	 * Updates all panels in the DataAnalysisView.
	 * 
	 * @param doRedefine
	 *            if true then the internal GeoElements will be redefined.
	 */
	@Override
	public void updateAllPanels(boolean doRedefine) {
		view.getDataDisplayPanel1().getModel().updatePlot(doRedefine);

		if (!getModel().isMultiVar() && view.getDataDisplayPanel2() != null) {
			view.getDataDisplayPanel2().getModel().updatePlot(doRedefine);
		}

		if (view.getStatisticsPanel() != null && getModel().showStatPanel()) {
			view.getStatisticsPanel().updatePanel();
		}

		if (view.getDataPanel() != null && view.getModel().showDataPanel()) {
			view.getDataPanel().updatePanel();
		}
	}

	@Override
	protected void updateRegressionPanel() {
		if (view.getRegressionPanel() != null) {
			view.getRegressionPanel().updateRegressionPanel();
		}

	}

	@Override
	protected void clearPredictionPanel() {
		view.getRegressionPanel().clearPredictionPanel();

	}

	@Override
	protected void removeGeos() {
		if (view.getDataDisplayPanel1() != null) {
			view.getDataDisplayPanel1().getModel().clearPlotGeoList();
		}

		if (view.getDataDisplayPanel2() != null) {
			view.getDataDisplayPanel2().getModel().clearPlotGeoList();
		}
	}

}
