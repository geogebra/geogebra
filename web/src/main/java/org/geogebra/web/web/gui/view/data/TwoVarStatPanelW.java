package org.geogebra.web.web.gui.view.data;

import org.geogebra.common.gui.view.data.TwoVarStatModel;
import org.geogebra.common.gui.view.data.TwoVarInferenceModel.UpdatePanel;
import org.geogebra.common.gui.view.data.TwoVarStatModel.TwoVarStatListener;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.web.html5.gui.util.LayoutUtil;
import org.geogebra.web.html5.main.AppW;

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
public class TwoVarStatPanelW extends StatTableW implements TwoVarStatListener {
	private static final long serialVersionUID = 1L;
	protected AppW app;
	private Kernel kernel;
	protected DataAnalysisViewW statDialog;
	protected MyTable statTable;
	private TwoVarStatModel model;
	private UpdatePanel listener;
	private boolean isIniting;
	
	public TwoVarStatPanelW(AppW app, DataAnalysisViewW statDialog,
			boolean isPairedData, UpdatePanel listener) {
		super(app);
		model = new TwoVarStatModel(app, isPairedData, this);
		this.app = app;
		this.statDialog = statDialog;
		kernel = app.getKernel();
		statTable = getTable();
		this.listener = listener;

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
		createListBoxCell(0, 0, app.getMenu("Sample1"), titles, 
				model.getSelectedDataIndex0());
		createListBoxCell(1, 0, app.getMenu("Sample2"), titles, 
				model.getSelectedDataIndex1());

		isIniting = false;
	}

	private void createListBoxCell(final int row, final int col, String title, String[] items,
			int selectedIdx) {
	    Label label = new Label(title);
	    final ListBox listBox = new ListBox();
	    for (String item: items) {
	    	listBox.addItem(item);
	    }
	    
	    listBox.addChangeHandler(new ChangeHandler() {
			
			public void onChange(ChangeEvent event) {
				int idx = listBox.getSelectedIndex();
				if (row == 0) {
					model.setSelectedDataIndex0(idx);
				} else {
					model.setSelectedDataIndex1(idx);		
				}
				updatePanel();
				listener.updatePanel();
			}
		});
	    
	    listBox.setSelectedIndex(selectedIdx);
	    
	    FlowPanel p = new FlowPanel();
	    p.add(LayoutUtil.panelRow(label, listBox));
	    getTable().setWidget(row, col, p);
    }

	public void updatePanel() {
		model.update();
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
