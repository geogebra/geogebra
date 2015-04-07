package org.geogebra.common.gui.view.data;

import org.apache.commons.math.stat.StatUtils;
import org.apache.commons.math.stat.descriptive.SummaryStatistics;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.main.App;

/**
 * Extension of StatTable that displays summary statistics for two data sets.
 * The two data sets are taken from the current collection of data provided by a
 * MultiVar StatDialog. JComboBoxes for choosing data sets are embedded in the
 * table.
 * 
 * @author G. Sturr
 * 
 */
public class TwoVarStatModel {
	public interface TwoVarStatListener {

		void setValueAt(String value, int row, int col);
		void setValueAt(double value, int row, int col);
		GeoList getDataSelected();
		double[] getValueArray(GeoList dataList1);
		void clear();

	}
	
	protected App app;

	private Integer[] selectedDataIndex = { 0, 1 };
	private boolean isPairedData = false;

	private double mean1, mean2, sd1, sd2, sdDifference, meanDifference;
	private long n1, n2;
	private TwoVarStatListener listener;

	public TwoVarStatModel(App app, boolean isPairedData, TwoVarStatListener listener) {

		this.app = app;
		this.isPairedData = isPairedData;
		this.listener = listener;
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

	public void update() {
		updateStat();
		updateDifferences();
	}
	
	private void updateStat() {
		// get the sample data statistics; if error clear the table and exit
		boolean ok = evaluateSampleData();
		if (!ok) {
			listener.clear();
			return;
		}

		// load table with the sample statistics
		listener.setValueAt(mean1, 0, 1);
		listener.setValueAt(sd1, 0, 2);
		listener.setValueAt(n1, 0, 3);
		listener.setValueAt(mean2, 1, 1);
		listener.setValueAt(sd2, 1, 2);
		listener.setValueAt(n2, 1, 3);
	}
	
	private void updateDifferences() {
		if (!isPairedData) {
			return;
		}
		
		// get the sample data statistics; if error (e.g. unequal sizes) clear
		// the table and exit
		boolean ok = evaluatePairedDifferences();
		if (!ok) {
			listener.clear();
			return;
		}

		// load paired difference statistics into the next row
		listener.setValueAt(app.getMenu("Differences"), 2, 0);
		listener.setValueAt(meanDifference, 2, 1);
		listener.setValueAt(sdDifference, 2, 2);
		listener.setValueAt(n1, 2, 3);
	}

	public int getSelectedDataIndex0() {
		return selectedDataIndex[0];
	}

	public int getSelectedDataIndex1() {
		return selectedDataIndex[1];
	}

	private boolean evaluatePairedDifferences() {

		try {
			// get the sample data

			GeoList dataCollection = listener.getDataSelected();

			GeoList dataList1 = (GeoList) dataCollection
					.get(selectedDataIndex[0]);
			double[] sample1 = listener.getValueArray(dataList1);
			SummaryStatistics stats1 = new SummaryStatistics();
			for (int i = 0; i < sample1.length; i++) {
				stats1.addValue(sample1[i]);
			}

			GeoList dataList2 = (GeoList) dataCollection
					.get(selectedDataIndex[1]);
			double[] sample2 = listener.getValueArray(dataList2);
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
			GeoList dataCollection = listener.getDataSelected();

			GeoList dataList1 = (GeoList) dataCollection
					.get(selectedDataIndex[0]);
			double[] sample1 = listener.getValueArray(dataList1);
			SummaryStatistics stats1 = new SummaryStatistics();
			for (int i = 0; i < sample1.length; i++) {
				stats1.addValue(sample1[i]);
			}

			GeoList dataList2 = (GeoList) dataCollection
					.get(selectedDataIndex[1]);
			double[] sample2 = listener.getValueArray(dataList2);
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

	public void setSelectedDataIndex0(int idx) {
		selectedDataIndex[0] = idx;
	}

	public void setSelectedDataIndex1(int idx) {
		selectedDataIndex[1] = idx;
	}

	

}
