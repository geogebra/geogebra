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
