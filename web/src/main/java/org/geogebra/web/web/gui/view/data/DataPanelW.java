package org.geogebra.web.web.gui.view.data;

import java.util.ArrayList;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.gui.view.data.DataAnalysisModel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.main.GeoGebraColorConstants;
import org.geogebra.common.main.settings.SpreadsheetSettings;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.LocalizationW;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

public class DataPanelW extends FlowPanel implements StatPanelInterfaceW,
		RequiresResize
	
{
	private static final long serialVersionUID = 1L;

	private AppW app;
	private DataAnalysisViewW daView;
	private DataAnalysisControllerW statController;

	private CheckBox cbEnableAll;
	private Boolean[] selectionList;

	private Label lblHeader;
	public int preferredColumnWidth = SpreadsheetSettings.TABLE_CELL_WIDTH;

	private LocalizationW loc;

	private static final GColor DISABLED_BACKGROUND_COLOR = GColor.lightGray;
	private static final GColor SELECTED_BACKGROUND_COLOR_HEADER = GeoGebraColorConstants.TABLE_SELECTED_BACKGROUND_COLOR_HEADER;
	private static final GColor TABLE_GRID_COLOR = GeoGebraColorConstants.TABLE_GRID_COLOR;
//	private static final GColor TABLE_HEADER_COLOR = GeoGebraColorConstants.TABLE_HEADER_COLOR;
	private StatTableW dataTable;

	private ScrollPanel scrollPane;
	
	private class DataClickHandler implements ClickHandler {
		private int index;
		public DataClickHandler(int index) {
			this.index = index;
		}
		
		public void onClick(ClickEvent event) {
	        onDataClick(index);
        }
		
	}
	/*************************************************
	 * Construct a DataPanel
	 */
	public DataPanelW(AppW app, DataAnalysisViewW statDialog) {
		this.app = app;
		this.loc = (LocalizationW) app.getLocalization();
		this.daView = statDialog;
		this.statController = statDialog.getController();

		buildDataTable();
	
		cbEnableAll = new CheckBox("");
		cbEnableAll.addClickHandler(new ClickHandler() {
			
			public void onClick(ClickEvent event) {
				enableAll();
			}
		});
		
		populateDataTable(statController.getDataArray());
		createGUI();
		enableAll();
		setStyleName("daData");
	}

	private void buildDataTable() {
		dataTable = new StatTableW(app);
		
	}

	private void createGUI() {
		scrollPane = new ScrollPanel();
		scrollPane.add(dataTable);

		
		lblHeader = new Label();

		add(lblHeader);
		add(scrollPane);
		setLabels();
	}

	public void removeGeos() {

	}

	public void setLabels() {
		lblHeader.setText(loc.getMenu("Data"));
	}

	public void updatePanel() {
//		setRowHeight();
	}
//
//	public void updateFonts(Font font) {
//		// TODO Auto-generated method stub
//
//	}

	private Boolean[] updateSelectionList(ArrayList<GeoElement> dataArray) {

		selectionList = new Boolean[dataArray.size()];
		for (int i = 0; i < selectionList.length; ++i) {
			selectionList[i] = true;
		}

		return selectionList;
	}

	private void populateOneVarDataTable(ArrayList<GeoElement> dataArray) {
		String[] titles = daView.getDataTitles();
		int maxRows = dataArray.size() + 1;
		String[] rowNames = new String[maxRows];
		dataTable.setStatTable(maxRows, rowNames, 2, null);
		for (int row = 0; row < maxRows - 1; row++) {
			CheckBox cb = new CheckBox("" + (row + 1));
			cb.addClickHandler(new DataClickHandler(row));
			cb.setValue(true);
			dataTable.getTable().setWidget(row + 1, 0, cb); 

			dataTable.setValueAt(
					dataArray.get(row).toDefinedValueString(
							StringTemplate.defaultTemplate), row + 1, 1);
		}

		
		dataTable.getTable().setWidget(0, 0, cbEnableAll);
		dataTable.setValueAt(titles[0], 0, 1);

		updateSelectionList(dataArray);

	}
	
	private void populateRegressionDataTable(ArrayList<GeoElement> dataArray) {
		// a data source may be a list of points with a single title
		// so we must create a title for the y column
		setStyleName("daRegressionData");
		String[] titles = daView.getDataTitles();
		String[] rowNames = new String[dataArray.size()];
		String titleX = titles[0];
		String titleY = titles.length == 1 ? titleX : titles[1];

		int maxRows = dataArray.size() + 1;
		dataTable.setStatTable(maxRows, rowNames, 3, null);

		for (int row = 0; row < maxRows - 1; ++row) {
			CheckBox cb = new CheckBox("" + (row+1));
			cb.addClickHandler(new DataClickHandler(row));
			cb.setValue(true);
			dataTable.getTable().setWidget(row + 1, 0, cb); 

			dataTable.setValueAt(
					((GeoPoint) (dataArray.get(row))).getInhomX()+"", row+1, 1);
			dataTable.setValueAt(
					((GeoPoint) (dataArray.get(row))).getInhomY()+"", row+1, 2);
		}

		dataTable.getTable().setWidget(0, 0, cbEnableAll);

		// handle x,y titles
		if (daView.getDataSource().isPointData()) {

			dataTable.setValueAt(loc.getMenu("Column.X"), 0, 1);
			dataTable.setValueAt(loc.getMenu("Column.Y"), 0, 2);
		} else {
			dataTable.setValueAt(loc.getMenu("Column.X") + ": " + titleX, 0, 1);
			dataTable.setValueAt(loc.getMenu("Column.Y") + ": " + titleY, 0, 2);
		}

		updateSelectionList(dataArray);

	}

	
	private void populateDataTable(ArrayList<GeoElement> dataArray) {

		if (dataArray == null || dataArray.size() < 1) {
			return;
		}

		//GeoPoint geo = null;
			
	
		switch (daView.getModel().getMode()) {

		case DataAnalysisModel.MODE_ONEVAR:
			populateOneVarDataTable(dataArray);
			break;

		case DataAnalysisModel.MODE_REGRESSION:
			populateRegressionDataTable(dataArray);
			break;
		}

	}
//
//	/**
//	 * Loads the data table. Called on data set changes.
//	 */
//	public void loadDataTable(ArrayList<GeoElement> dataArray) {
//
//		// load the data model
//		populateDataTable(dataArray);
//
//		// prepare boolean selection list for the checkboxes
//		selectionList = new Boolean[dataArray.size()];
//		for (int i = 0; i < dataArray.size(); ++i) {
//			selectionList[i] = true;
//		}
//
//		// create a new header
//		rowHeader = new MyRowHeader(this, dataTable);
//		scrollPane.setRowHeaderView(rowHeader);
//		updateFonts(getFont());
//
//		// repaint
//		dataTable.repaint();
//		rowHeader.repaint();
//
//	}
//
//	public void ensureTableFill() {
//		Container p = getParent();
//		DefaultTableModel dataModel = (DefaultTableModel) dataTable.getModel();
//		if (dataTable.getHeight() < p.getHeight()) {
//			int newRows = (p.getHeight() - dataTable.getHeight())
//					/ dataTable.getRowHeight();
//			dataModel.setRowCount(dataTable.getRowCount() + newRows);
//			for (int i = 0; i <= dataTable.getRowCount(); ++i) {
//				if (rowHeader.getModel().getElementAt(i) != null)
//					((DefaultListModel) rowHeader.getModel()).add(i, true);
//			}
//		}
//
//	}

//	public void actionPerformed(ActionEvent e) {
//		if (e.getSource() == btnEnableAll) {
//			rowHeader.enableAll();
//			btnEnableAll.setEnabled(false);
//
//		}
//	}

	
//}
	
	public void onDataClick(int index) { 
		selectionList[index] = !selectionList[index];
		statController.updateSelectedDataList(index,
				selectionList[index]);
		cbEnableAll.setValue(isAllEnabled());
		cbEnableAll.setEnabled(true);
	}

	public void enableAll() {

		for (int i = 0; i < selectionList.length; ++i) {
			if (selectionList[i] == false) {
				statController.updateSelectedDataList(i, true);
				selectionList[i] = true;
				Widget w = dataTable.getTable().getWidget(i + 1, 0);
				if (w instanceof CheckBox) {
					((CheckBox)w).setValue(true);
				}
		
			}
			
		}
		cbEnableAll.setValue(true);
		cbEnableAll.setEnabled(false);
	}

	public boolean isAllEnabled() {
		for (int i = 0; i < selectionList.length; ++i) {
			if (selectionList[i] == false)
				return false;
		}
		return true;
	}

	public void onResize() {
	    scrollPane.setHeight(getOffsetHeight() - lblHeader.getOffsetHeight() + "px");
    }


}