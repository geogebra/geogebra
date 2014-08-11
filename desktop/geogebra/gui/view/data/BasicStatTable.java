package geogebra.gui.view.data;

import geogebra.common.gui.view.data.DataAnalysisModel;
import geogebra.common.gui.view.data.DataVariable.GroupType;
import geogebra.common.gui.view.data.StatTableModel;
import geogebra.common.gui.view.data.StatTableModel.Stat;
import geogebra.common.gui.view.data.StatTableModel.StatTableListener;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.main.AppD;

import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.JPanel;

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
	protected AppD app;

	/*************************************************
	 * Construct the panel
	 * 
	 * @param app
	 * @param statDialog
	 * @param mode
	 */
	public BasicStatTable(AppD app, DataAnalysisViewD statDialog) {

		this.app = app;
		model = new StatTableModel(app, this);
		this.daView = statDialog;

		this.setLayout(new BorderLayout());
		initStatTable();
		updateFonts(app.getPlainFont());

	} // END constructor

	protected void initStatTable() {

		statTable = new StatTable(app);
		statTable.setStatTable(model.getRowCount(), model.getRowNames(),
				model.getColumnCount(), model.getColumnNames());
		this.removeAll();
		this.add(statTable, BorderLayout.CENTER);
	}

	public String[] getRowNames() {
		return model.getRowNames();
	}

	public String[] getColumnNames() {
		return model.getColumnNames();
	}

	public int getRowCount() {
		return model.getRowCount();
	}

	public int getColumnCount() {
		return model.getColumnCount();
	}

	// =======================================================

	/**
	 * Evaluates all statistics for the selected data list. If data source is
	 * not valid, the result cells are set blank.
	 * 
	 */
	public void updatePanel() {
		model.updatePanel();
	}

	protected AlgoElement getAlgo(Stat algoName, GeoList dataList,
			GeoElement geoRegression) {
		return model.getAlgo(algoName, dataList, geoRegression);
	}

	protected AlgoElement getAlgoRawData(Stat stat, GeoList dataList,
			GeoElement geoRegression) {
		return model.getAlgoRawData(stat, dataList, geoRegression);

	}

	protected AlgoElement getAlgoFrequency(Stat stat, GeoList frequencyData,
			GeoElement geoRegression) {
		return model.getAlgoFrequency(stat, frequencyData, geoRegression);
	}

	protected AlgoElement getAlgoClass(StatTableModel.Stat stat,
			GeoList frequencyData, GeoElement geoRegression) {
		return model.getAlgoClass(stat, frequencyData, geoRegression);
	}

	public void updateFonts(Font font) {
		statTable.updateFonts(font);
	}

	public void setLabels() {
		statTable.setLabels(model.getRowNames(), model.getColumnNames());
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

}
