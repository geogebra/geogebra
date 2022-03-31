package org.geogebra.web.full.gui.view.data;

import org.geogebra.common.gui.view.data.DataAnalysisModel;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.web.full.gui.images.AppResources;
import org.geogebra.web.full.gui.util.MyToggleButtonW;
import org.geogebra.web.full.gui.util.StyleBarW;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

/**
 * @author G. Sturr
 * 
 */
public class DataAnalysisStyleBarW extends StyleBarW implements ClickHandler {

	private DataAnalysisViewW daView;
	private MyToggleButtonW btnShowStatistics;
	private MyToggleButtonW btnShowPlot2;
	private MyToggleButtonW btnShowData;
	private MyToggleButtonW btnExport;
	private MyToggleButtonW btnSwapXY;

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

		btnShowStatistics = new MyToggleButtonW(AppResources.INSTANCE.dataview_showstatistics());
		btnShowStatistics.addClickHandler(this);

		btnShowData = new MyToggleButtonW(AppResources.INSTANCE.dataview_showdata());
		btnShowData.addClickHandler(this);

		btnShowPlot2 = new MyToggleButtonW(AppResources.INSTANCE.dataview_showplot2());
		btnShowPlot2.addClickHandler(this);

		// create export button
		btnExport = new MyToggleButtonW(AppResources.INSTANCE.export());
		btnExport.addClickHandler(this);

		btnSwapXY = new MyToggleButtonW(getSwapXYString(app.getLocalization()));
		btnSwapXY.setSelected(!daView.getController().isLeftToRight());
		btnSwapXY.addClickHandler(this);
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
			// if (daView.groupType() == GroupType.RAWDATA) {
			btnShowData.setVisible(true);
			// } else {
			// btnShowData.setVisible(false);
			// }
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
		btnShowStatistics.setToolTipText(loc.getMenu("ShowStatistics"));
		btnShowData.setToolTipText(loc.getMenu("ShowData"));
		btnShowPlot2.setToolTipText(loc.getMenu("ShowPlot2"));
		btnSwapXY.setText(getSwapXYString(loc));
	}

	private static String getSwapXYString(Localization loc) {
		return loc.getMenu("Column.X") + " \u21C6 " + loc.getMenu("Column.Y");
	}

	/**
	 * Handle input change
	 * 
	 * @param source
	 *            event source
	 */
	public void actionPerformed(Object source) {
		DataAnalysisModel model = daView.getModel();
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
		else if (source == btnExport) {
			btnExport.setSelected(false);
		}
	}

	@Override
    public void setOpen(boolean showStyleBar) {
	    // TODO Auto-generated method stub
    }

	@Override
	public void onClick(ClickEvent event) {
	    actionPerformed(event.getSource());
    }

}
