package org.geogebra.web.web.gui.view.data;

import org.geogebra.common.gui.view.data.DataAnalysisController;
import org.geogebra.web.html5.main.AppW;

/**
 * Class to control data management for the DataAnalysisView.
 * 
 * @author G. Sturr
 * 
 */
public class DataAnalysisControllerW extends DataAnalysisController {

	private DataAnalysisViewW view;
	

	public DataAnalysisControllerW(AppW app, DataAnalysisViewW view) {
		super(app);
		this.view = view;
	}

	/**
	 * Updates all panels in the DataAnalysisView.
	 * 
	 * @param doRedefine
	 *            if true then the internal GeoElements will be redefined.
	 */
	public void updateAllPanels(boolean doRedefine) {
		view.getDataDisplayPanel1().getModel().updatePlot(doRedefine);
		if (!getModel().isMultiVar() && view.getDataDisplayPanel2() != null)
			view.getDataDisplayPanel2().getModel().updatePlot(doRedefine);

		if (view.getStatisticsPanel() != null && getModel().showStatPanel()) {
			view.getStatisticsPanel().updatePanel();
		}

		if (view.getDataPanel() != null && view.getModel().showDataPanel()) {
			view.getDataPanel().updatePanel();
		}
		
	}

	@Override
	protected void updateRegressionPanel() {
		if (view.getRegressionPanel() != null)
			view.getRegressionPanel().updateRegressionPanel();

	}

	@Override
	protected void clearPredictionPanel() {
		view.getRegressionPanel().clearPredictionPanel();

	}

	@Override
	protected void removeGeos() {
		if (view.getDataDisplayPanel1() != null) {
			view.getDataDisplayPanel1().getModel().removeGeos();
		}

		if (view.getDataDisplayPanel2() != null) {
			view.getDataDisplayPanel2().getModel().removeGeos();
		}
	}

}
