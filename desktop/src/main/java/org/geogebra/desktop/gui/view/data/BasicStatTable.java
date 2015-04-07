package org.geogebra.desktop.gui.view.data;

import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.JPanel;

import org.geogebra.common.gui.view.data.DataAnalysisModel;
import org.geogebra.common.gui.view.data.StatTableModel;
import org.geogebra.common.gui.view.data.DataVariable.GroupType;
import org.geogebra.common.gui.view.data.StatTableModel.Stat;
import org.geogebra.common.gui.view.data.StatTableModel.StatTableListener;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.desktop.main.AppD;

/**
 * Displays statistics for DataAnalysisView when in one variable or regression
 * mode.
 * 
 * @author G. Sturr
 * 
 */
public class BasicStatTable extends JPanel implements StatPanelInterface,
		StatTableListener {
	private static final long serialVersionUID = 1L;
	private StatTableModel model;

	protected DataAnalysisViewD daView;
	protected StatTable statTable;
	private AppD app;

	/*************************************************
	 * Construct the panel
	 * 
	 * @param app
	 * @param statDialog
	 * @param mode
	 */
	public BasicStatTable(AppD app, DataAnalysisViewD statDialog) {
		this(app, statDialog, true);
	} // END constructor

	public BasicStatTable(AppD app, DataAnalysisViewD statDialog,
			boolean defaultModel) {
		this.app = app;
		this.daView = statDialog;
		this.setLayout(new BorderLayout());
		if (defaultModel) {
			setModel(new StatTableModel(app, this));
		}
	}

	public void setModel(StatTableModel model) {
		this.model = model;
		initStatTable();
		updateFonts(app.getPlainFont());
	}

	protected void initStatTable() {

		statTable = new StatTable(getApp());
		statTable.setStatTable(getModel().getRowCount(), getModel().getRowNames(),
				getModel().getColumnCount(), getModel().getColumnNames());
		this.removeAll();
		this.add(statTable, BorderLayout.CENTER);
	}

	public String[] getRowNames() {
		return getModel().getRowNames();
	}

	public String[] getColumnNames() {
		return getModel().getColumnNames();
	}

	public int getRowCount() {
		return getModel().getRowCount();
	}

	public int getColumnCount() {
		return getModel().getColumnCount();
	}

	// =======================================================

	/**
	 * Evaluates all statistics for the selected data list. If data source is
	 * not valid, the result cells are set blank.
	 * 
	 */
	public void updatePanel() {
		getModel().updatePanel();
	}

	protected AlgoElement getAlgo(Stat algoName, GeoList dataList,
			GeoElement geoRegression) {
		return getModel().getAlgo(algoName, dataList, geoRegression);
	}

	protected AlgoElement getAlgoRawData(Stat stat, GeoList dataList,
			GeoElement geoRegression) {
		return getModel().getAlgoRawData(stat, dataList, geoRegression);

	}

	protected AlgoElement getAlgoFrequency(Stat stat, GeoList frequencyData,
			GeoElement geoRegression) {
		return getModel().getAlgoFrequency(stat, frequencyData, geoRegression);
	}

	protected AlgoElement getAlgoClass(StatTableModel.Stat stat,
			GeoList frequencyData, GeoElement geoRegression) {
		return getModel().getAlgoClass(stat, frequencyData, geoRegression);
	}

	public void updateFonts(Font font) {
		statTable.updateFonts(font);
	}

	public void setLabels() {
		statTable.setLabels(getModel().getRowNames(), getModel().getColumnNames());
	}

	public GeoList getDataSelected() {
		return daView.getController().getDataSelected();
	}

	public GeoElement getRegressionModel() {
		return daView.getRegressionModel();
	}

	public DataAnalysisModel.Regression getRegressionMode() {
		return daView.getModel().getRegressionMode();
	}

	public boolean isValidData() {
		return daView.getController().isValidData();
	}

	public void setValueAt(double value, int row, int col) {
		statTable.getModel().setValueAt(daView.getModel().format(value), row,
				col);
	}

	public boolean isViewValid() {
		return daView == null || daView.getDataSource() == null;
	}

	public int getMode() {
		return daView.getModel().getMode();
	}

	public GroupType groupType() {
		return daView.groupType();
	}

	public boolean isNumericData() {
		return daView.getDataSource().isNumericData();
	}

	public AppD getApp() {
		return app;
	}

	public void setApp(AppD app) {
		this.app = app;
	}

	public StatTableModel getModel() {
		return model;
	}

}
