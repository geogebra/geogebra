package org.geogebra.desktop.gui.view.data;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import org.geogebra.common.gui.view.data.TwoVarStatModel;
import org.geogebra.common.gui.view.data.TwoVarStatModel.TwoVarStatListener;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.desktop.main.AppD;

/**
 * Extension of StatTable that displays summary statistics for two data sets.
 * The two data sets are taken from the current collection of data provided by a
 * MultiVar StatDialog. JComboBoxes for choosing data sets are embedded in the
 * table.
 * 
 * @author G. Sturr
 * 
 */
public class TwoVarStatPanel extends StatTable implements ActionListener,
		TwoVarStatListener {
	private static final long serialVersionUID = 1L;
	protected AppD app;
	private Kernel kernel;
	protected DataAnalysisViewD statDialog;
	protected MyTable statTable;
	private TwoVarStatModel model;
	private ActionListener parentActionListener;
	private boolean isIniting;

	public TwoVarStatPanel(AppD app, DataAnalysisViewD statDialog,
			boolean isPairedData, ActionListener parentActionListener) {
		super(app);
		model = new TwoVarStatModel(app, isPairedData, this);
		this.app = app;
		this.statDialog = statDialog;
		kernel = app.getKernel();
		statTable = this.getTable();
		this.parentActionListener = parentActionListener;

		setTable(isPairedData);

	}

	public void setTable(boolean isPairedData) {

		isIniting = true;

		model.setPairedData(isPairedData);
		setStatTable(model.getRowCount(), model.getRowNames(),
				model.getColumnCount(), model.getColumnNames());

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
		int idx0 = model.getSelectedDataIndex0();
		int idx1 = model.getSelectedDataIndex1();

		setComboBoxCells(cellMap, this);
		setComboCellSelectedIndex(idx0, 0, 0);
		setComboCellSelectedIndex(idx1, 1, 0);
		getModel().setValueAt(idx0, 0, 0);
		getModel().setValueAt(idx1, 1, 0);

		this.setMinimumSize(this.getPreferredSize());

		isIniting = false;
	}

	public void updatePanel() {

		model.update();

		this.setMinimumSize(this.getPreferredSize());

	}

	public void actionPerformed(ActionEvent e) {
		if (isIniting)
			return;

		if (e.getActionCommand().equals("updateTable")) {
			model.setSelectedDataIndex0(getComboCellEditorSelectedIndex(0, 0));
			model.setSelectedDataIndex1(getComboCellEditorSelectedIndex(1, 0));
			this.updatePanel();
		}
		parentActionListener.actionPerformed(e);
	}

	public void setValueAt(String value, int row, int col) {
		statTable.setValueAt(value, row, col);

	}

	public void setValueAt(double value, int row, int col) {
		statTable.setValueAt(statDialog.format(value), row, col);
	}

	public GeoList getDataSelected() {
		return statDialog.getController().getDataSelected();
	}

	public double[] getValueArray(GeoList list) {
		return statDialog.getController().getValueArray(list);
	}

	public Integer[] getSelectedDataIndex() {
		return model.getSelectedDataIndex();
	}

}
