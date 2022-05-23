package org.geogebra.web.full.gui.view.data;

import org.geogebra.common.gui.view.data.DataAnalysisModel;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.images.AppResources;
import org.geogebra.web.full.gui.util.StyleBarW;
import org.geogebra.web.html5.gui.util.ToggleButton;
import org.geogebra.web.html5.main.AppW;

public class DataAnalysisStyleBarW extends StyleBarW {
	private DataAnalysisViewW daView;
	private ToggleButton btnShowStatistics;
	private ToggleButton btnShowPlot2;
	private ToggleButton btnShowData;
	private ToggleButton btnExport;
	private ToggleButton btnSwapXY;

	/**
	 * @param app
	 *            application
	 * @param statDialog
	 *            statistics dialog
	 */
	public DataAnalysisStyleBarW(AppW app, DataAnalysisViewW statDialog) {
		super(app, App.VIEW_DATA_ANALYSIS);
		this.daView = statDialog;
		createGUI();
		updateGUI();
		setLabels();
		addViewButton();
	}

	private void createGUI() {
		this.clear();

		btnShowStatistics = new ToggleButton(AppResources.INSTANCE.dataview_showstatistics());
		btnShowStatistics.addFastClickHandler(this::actionPerformed);

		btnShowData = new ToggleButton(AppResources.INSTANCE.dataview_showdata());
		btnShowData.addFastClickHandler(this::actionPerformed);

		btnShowPlot2 = new ToggleButton(AppResources.INSTANCE.dataview_showplot2());
		btnShowPlot2.addFastClickHandler(this::actionPerformed);

		// create export button
		btnExport = new ToggleButton(MaterialDesignResources.INSTANCE.prob_calc_export());
		btnExport.addFastClickHandler(this::actionPerformed);

		btnSwapXY = new ToggleButton(getSwapXYString(app.getLocalization()));
		btnSwapXY.setSelected(!daView.getController().isLeftToRight());
		btnSwapXY.addFastClickHandler(this::actionPerformed);
		btnSwapXY.getElement().addClassName("daSwapXYButton");

		add(btnShowStatistics);
		add(btnShowData);
		add(btnShowPlot2);
		add(btnSwapXY);
	}

	/**
	 * Update UI
	 */
	public void updateGUI() {
		DataAnalysisModel model = daView.getModel();
		btnShowStatistics.setSelected(model.showStatPanel());
		if (model.showStatPanel() && daView.getStatisticsPanel().isVisible()) {
			daView.getStatisticsPanel().updatePanel();
		}

		switch (model.getMode()) {
		case DataAnalysisModel.MODE_ONEVAR:
			btnShowData.setVisible(true);
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
		btnSwapXY.setVisible(model.isRegressionMode());
		btnSwapXY.setSelected(!daView.getController().isLeftToRight());
	}

	@Override
	public void setLabels() {
		super.setLabels();
		Localization loc = app.getLocalization();
		btnShowStatistics.setTitle(loc.getMenu("ShowStatistics"));
		btnShowData.setTitle(loc.getMenu("ShowData"));
		btnShowPlot2.setTitle(loc.getMenu("ShowPlot2"));
		btnSwapXY.setText(getSwapXYString(loc));
	}

	private static String getSwapXYString(Localization loc) {
		return loc.getMenu("Column.X") + " \u21C6 " + loc.getMenu("Column.Y");
	}

	/**
	 * Handle input change
	 * @param source - event source
	 */
	public void actionPerformed(Object source) {
		DataAnalysisModel model = daView.getModel();
		if (source == btnShowStatistics) {
			model.setShowStatistics(btnShowStatistics.isSelected());
			updateGUI();
		} else if (source == btnShowData) {
			model.setShowDataPanel(btnShowData.isSelected());
			updateGUI();
		} else if (source == btnShowPlot2) {
			model.setShowComboPanel2(btnShowPlot2.isSelected());
			updateGUI();
		} else if (source == btnSwapXY) {
			daView.getController().swapXY();
			updateGUI();
		} else if (source == btnExport) {
			btnExport.setSelected(false);
		}
	}

	@Override
    public void setOpen(boolean showStyleBar) {
	    // nothing to do here
    }
}
