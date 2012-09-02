package geogebra.gui.view.spreadsheet.statdialog;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.geos.GeoList;
import geogebra.main.AppD;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import org.apache.commons.math.stat.StatUtils;
import org.apache.commons.math.stat.descriptive.SummaryStatistics;

/**
 * Extension of StatTable that displays summary statistics for two data sets.
 * The two data sets are taken from the current collection of data provided by a
 * MultiVar StatDialog. JComboBoxes for choosing data sets are embedded in the
 * table.
 * 
 * @author G. Sturr
 * 
 */
public class TwoVarStatPanel extends StatTable implements ActionListener {
	private static final long serialVersionUID = 1L;
	protected AppD app;
	private Kernel kernel;
	protected DataAnalysisViewD statDialog;
	protected MyTable statTable;

	private Integer[] selectedDataIndex = { 0, 1 };
	private ActionListener parentActionListener;
	private boolean isPairedData = false;

	private boolean isIniting;
	private double mean1, mean2, sd1, sd2, sdDifference, meanDifference;
	private long n1, n2;

	public TwoVarStatPanel(AppD app, DataAnalysisViewD statDialog,
			boolean isPairedData, ActionListener parentActionListener) {
		super(app);

		this.app = app;
		this.statDialog = statDialog;
		kernel = app.getKernel();
		statTable = this.getTable();
		this.parentActionListener = parentActionListener;

		setTable(isPairedData);

	}

	public void setTable(boolean isPairedData) {

		isIniting = true;

		this.isPairedData = isPairedData;
		setStatTable(getRowCount(), getRowNames(), getColumnCount(),
				getColumnNames());

		// create an array of data titles for the table celll comboboxes
		// the array includes and extra element to store the combo box label
		String[] titles = statDialog.getDataTitles();
		String[] titlesPlusLabel = new String[titles.length + 1];
		System.arraycopy(titles, 0, titlesPlusLabel, 0, titles.length);

		// create hash maps for the table comboboxes:
		// key = cell location for the combo box
		// value = String[] to hold menu items plus label
		HashMap<Point, String[]> cellMap = new HashMap<Point, String[]>();
		titlesPlusLabel[titlesPlusLabel.length - 1] = app.getMenu("Sample1");
		cellMap.put(new Point(0, 0), titlesPlusLabel.clone());
		titlesPlusLabel[titlesPlusLabel.length - 1] = app.getMenu("Sample2");
		cellMap.put(new Point(1, 0), titlesPlusLabel.clone());

		// set the table combo boxes
		setComboBoxCells(cellMap, this);
		setComboCellSelectedIndex(selectedDataIndex[0], 0, 0);
		setComboCellSelectedIndex(selectedDataIndex[1], 1, 0);
		getModel().setValueAt(selectedDataIndex[0], 0, 0);
		getModel().setValueAt(selectedDataIndex[1], 1, 0);

		this.setMinimumSize(this.getPreferredSize());

		isIniting = false;
	}

	public void setPairedData(boolean isPairedData) {
		this.isPairedData = isPairedData;
	}

	public String[] getRowNames() {

		return null;
		/*
		 * if(isPairedData){ String[] names = { "1", "2",
		 * app.getMenu("Differences") }; return names; }else{ String[] names = {
		 * "1", "2" }; return names; }
		 */

	}

	public String[] getColumnNames() {

		String[] names = { " ", app.getMenu("Mean"),
				app.getMenu("SampleStandardDeviation.short"),
				app.getMenu("Length.short") };
		return names;
	}

	public int getRowCount() {
		if (isPairedData) {
			return 3;
		}
		return 2;
	}

	public int getColumnCount() {
		return getColumnNames().length;
	}

	public Integer[] getSelectedDataIndex() {
		return selectedDataIndex;
	}

	public void setSelectedDataIndex(Integer[] selectedDataIndex) {
		this.selectedDataIndex = selectedDataIndex;
	}

	public void updatePanel() {

		// get the sample data statistics; if error clear the table and exit
		boolean ok = evaluateSampleData();
		if (!ok) {
			this.clear();
			return;
		}

		// load table with the sample statistics
		statTable.setValueAt(statDialog.format(mean1), 0, 1);
		statTable.setValueAt(statDialog.format(sd1), 0, 2);
		statTable.setValueAt(statDialog.format(n1), 0, 3);
		statTable.setValueAt(statDialog.format(mean2), 1, 1);
		statTable.setValueAt(statDialog.format(sd2), 1, 2);
		statTable.setValueAt(statDialog.format(n2), 1, 3);

		// get the sample data statistics; if error (e.g. unequal sizes) clear
		// the table and exit
		if (isPairedData) {
			ok = evaluatePairedDifferences();
			if (!ok) {
				this.clear();
				return;
			}
			// load paired difference statistics into the next row
			statTable.setValueAt(app.getMenu("Differences"), 2, 0);
			statTable.setValueAt(statDialog.format(meanDifference), 2, 1);
			statTable.setValueAt(statDialog.format(sdDifference), 2, 2);
			statTable.setValueAt(statDialog.format(n1), 2, 3);
		}

		this.setMinimumSize(this.getPreferredSize());

	}

	private boolean evaluatePairedDifferences() {

		try {
			// get the sample data

			GeoList dataCollection = statDialog.getController()
					.getDataSelected();

			GeoList dataList1 = (GeoList) dataCollection
					.get(selectedDataIndex[0]);
			double[] sample1 = statDialog.getController()
					.getValueArray(dataList1);
			SummaryStatistics stats1 = new SummaryStatistics();
			for (int i = 0; i < sample1.length; i++) {
				stats1.addValue(sample1[i]);
			}

			GeoList dataList2 = (GeoList) dataCollection
					.get(selectedDataIndex[1]);
			double[] sample2 = statDialog.getController()
					.getValueArray(dataList2);
			SummaryStatistics stats2 = new SummaryStatistics();
			for (int i = 0; i < sample2.length; i++) {
				stats2.addValue(sample2[i]);
			}

			// exit if sample sizes are unequal
			if (stats1.getN() != stats2.getN())
				return false;

			// get statistics
			meanDifference = StatUtils.meanDifference(sample1, sample2);
			sdDifference = Math.sqrt(StatUtils.varianceDifference(sample1,
					sample2, meanDifference));

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;

	}

	private boolean evaluateSampleData() {

		try {
			// get the sample data
			GeoList dataCollection = statDialog.getController()
					.getDataSelected();

			GeoList dataList1 = (GeoList) dataCollection
					.get(selectedDataIndex[0]);
			double[] sample1 = statDialog.getController()
					.getValueArray(dataList1);
			SummaryStatistics stats1 = new SummaryStatistics();
			for (int i = 0; i < sample1.length; i++) {
				stats1.addValue(sample1[i]);
			}

			GeoList dataList2 = (GeoList) dataCollection
					.get(selectedDataIndex[1]);
			double[] sample2 = statDialog.getController()
					.getValueArray(dataList2);
			SummaryStatistics stats2 = new SummaryStatistics();
			for (int i = 0; i < sample2.length; i++) {
				stats2.addValue(sample2[i]);
			}

			mean1 = stats1.getMean();
			sd1 = stats1.getStandardDeviation();
			n1 = stats1.getN();
			mean2 = stats2.getMean();
			sd2 = stats2.getStandardDeviation();
			n2 = stats2.getN();

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public void actionPerformed(ActionEvent e) {
		if (isIniting)
			return;

		if (e.getActionCommand().equals("updateTable")) {
			selectedDataIndex[0] = getComboCellEditorSelectedIndex(0, 0);
			selectedDataIndex[1] = getComboCellEditorSelectedIndex(1, 0);
			this.updatePanel();
		}
		parentActionListener.actionPerformed(e);
	}

}
