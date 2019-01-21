package org.geogebra.web.full.gui.view.spreadsheet;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.gui.view.spreadsheet.CreateObjectModel;
import org.geogebra.common.gui.view.spreadsheet.MyTable;
import org.geogebra.web.html5.main.AppW;

/**
 * Utility class to handle toolbar menu mode changes
 * 
 * 
 * @author G. Sturr
 *
 */
public class SpreadsheetToolbarManagerW {

	private AppW app;
	private SpreadsheetViewW view;
	private MyTableW table;

	private CreateObjectDialogW id;

	/**
	 * @param app
	 *            application
	 * @param view
	 *            spreadsheet view
	 */
	public SpreadsheetToolbarManagerW(AppW app, SpreadsheetViewW view) {
		this.app = app;
		this.view = view;
		this.table = view.getSpreadsheetTable();
	}

	/**
	 * @param mode
	 *            app mode
	 */
	public void handleModeChange(int mode) {

		// Application.printStacktrace("");
		table.setTableMode(MyTable.TABLE_MODE_STANDARD);

		switch (mode) {	

		case EuclidianConstants.MODE_SPREADSHEET_CREATE_LIST:
			//if(!app.getSelectedGeos().isEmpty() && prevMode == mode){
			if (!table.getSelectedCellRanges().get(0).isEmpty()) {
				openDialog(CreateObjectModel.TYPE_LIST);
			}
			break;

		case EuclidianConstants.MODE_SPREADSHEET_CREATE_LISTOFPOINTS:
			if (table.getCellRangeProcessor()
					.isCreatePointListPossible(table.getSelectedCellRanges())) {
				openDialog(CreateObjectModel.TYPE_LISTOFPOINTS);
			}
			break;

		case EuclidianConstants.MODE_SPREADSHEET_CREATE_MATRIX:
			if (table.getCellRangeProcessor()
					.isCreateMatrixPossible(table.getSelectedCellRanges())) {
				openDialog(CreateObjectModel.TYPE_MATRIX);
			}
			break;

		case EuclidianConstants.MODE_SPREADSHEET_CREATE_TABLETEXT:
			if (table.getCellRangeProcessor()
					.isCreateMatrixPossible(table.getSelectedCellRanges())) {
				openDialog(CreateObjectModel.TYPE_TABLETEXT);
			}
			break;

		case EuclidianConstants.MODE_SPREADSHEET_CREATE_POLYLINE:
			if (table.getCellRangeProcessor()
					.isCreatePointListPossible(table.getSelectedCellRanges())) {
				openDialog(CreateObjectModel.TYPE_POLYLINE);
			}
			break;
			
		case EuclidianConstants.MODE_SPREADSHEET_SUM:
		case EuclidianConstants.MODE_SPREADSHEET_AVERAGE:
		case EuclidianConstants.MODE_SPREADSHEET_COUNT:
		case EuclidianConstants.MODE_SPREADSHEET_MIN:
		case EuclidianConstants.MODE_SPREADSHEET_MAX:
			
			// Handle autofunction modes
			
			table.setTableMode(MyTable.TABLE_MODE_AUTOFUNCTION);

			break;

		default:
			// ignore other modes
		}				
	}

	private void openDialog(int type) {
		id = new CreateObjectDialogW(app, view, type);
		id.setVisible(true);
	}

}
