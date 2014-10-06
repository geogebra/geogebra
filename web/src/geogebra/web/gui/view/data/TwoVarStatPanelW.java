package geogebra.web.gui.view.data;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.geos.GeoList;
import geogebra.html5.gui.util.LayoutUtil;
import geogebra.html5.main.AppW;

import org.apache.commons.math.stat.StatUtils;
import org.apache.commons.math.stat.descriptive.SummaryStatistics;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;

/**
 * Extension of StatTable that displays summary statistics for two data sets.
 * The two data sets are taken from the current collection of data provided by a
 * MultiVar StatDialog. JComboBoxes for choosing data sets are embedded in the
 * table.
 * 
 * @author G. Sturr
 * 
 */
public class TwoVarStatPanelW extends StatTableW implements ScheduledCommand  {
	private static final long serialVersionUID = 1L;
	protected AppW app;
	private Kernel kernel;
	protected DataAnalysisViewW statDialog;
	protected MyTable statTable;

	private Integer[] selectedDataIndex = { 0, 1 };
	//private ActionListener parentActionListener;
	private boolean isPairedData = false;

	private boolean isIniting;
	private double mean1, mean2, sd1, sd2, sdDifference, meanDifference;
	private long n1, n2;

	public TwoVarStatPanelW(AppW app, DataAnalysisViewW statDialog,
			boolean isPairedData/*, ActionListener parentActionListener*/) {
		super(app);

		this.app = app;
		this.statDialog = statDialog;
		kernel = app.getKernel();
		statTable = getTable();

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
		createListBoxCell(0, 0, app.getMenu("Sample1"), titles);
		createListBoxCell(1, 0, app.getMenu("Sample2"), titles);
//		String[] titlesPlusLabel = new String[titles.length + 1];
//		System.arraycopy(titles, 0, titlesPlusLabel, 0, titles.length);
//
//		// create hash maps for the table comboboxes:
//		// key = cell location for the combo box
//		// value = String[] to hold menu items plus label
//		HashMap<GPointW, String[]> cellMap = new HashMap<GPointW, String[]>();
//		titlesPlusLabel[titlesPlusLabel.length - 1] = app.getMenu("Sample1");
//		cellMap.put(new GPointW(0, 0), titlesPlusLabel);
//		titlesPlusLabel[titlesPlusLabel.length - 1] = app.getMenu("Sample2");
//		cellMap.put(new GPointW(1, 0), titlesPlusLabel);
//
//		// set the table combo boxes
//		setComboBoxCells(cellMap, this);
//		setComboCellSelectedIndex(selectedDataIndex[0], 0, 0);
//		setComboCellSelectedIndex(selectedDataIndex[1], 1, 0);
		//setValueAt(selectedDataIndex[0]+"", 0, 0);
		//setValueAt(selectedDataIndex[1]+"", 1, 0);


		isIniting = false;
	}

	private void createListBoxCell(int row, int col, String title, String[] items) {
	    Label label = new Label(title);
	    final ListBox listBox = new ListBox();
	    for (String item: items) {
	    	listBox.addItem(item);
	    }
	    
	    final int idx = row;  
	    listBox.addChangeHandler(new ChangeHandler() {
			
			public void onChange(ChangeEvent event) {
				selectedDataIndex[idx] = listBox.getSelectedIndex();
				updatePanel();
			}
		});
	    
	    FlowPanel p = new FlowPanel();
	    p.add(LayoutUtil.panelRow(label, listBox));
	    getTable().setWidget(row, col, p);
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
		setValueAt(statDialog.format(mean1), 0, 1);
		setValueAt(statDialog.format(sd1), 0, 2);
		setValueAt(statDialog.format(n1), 0, 3);
		setValueAt(statDialog.format(mean2), 1, 1);
		setValueAt(statDialog.format(sd2), 1, 2);
		setValueAt(statDialog.format(n2), 1, 3);

		// get the sample data statistics; if error (e.g. unequal sizes) clear
		// the table and exit
		if (isPairedData) {
			ok = evaluatePairedDifferences();
			if (!ok) {
				this.clear();
				return;
			}
			// load paired difference statistics into the next row
			setValueAt(app.getMenu("Differences"), 2, 0);
			setValueAt(statDialog.format(meanDifference), 2, 1);
			setValueAt(statDialog.format(sdDifference), 2, 2);
			setValueAt(statDialog.format(n1), 2, 3);
		}

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



	public void execute() {
	    // TODO Auto-generated method stub
	    
    }

}
