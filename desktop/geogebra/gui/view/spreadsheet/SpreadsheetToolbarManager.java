package geogebra.gui.view.spreadsheet;

import geogebra.common.euclidian.EuclidianConstants;
import geogebra.gui.view.spreadsheet.statdialog.StatDialog;
import geogebra.main.Application;

/**
 * Utility class to handle toolbar menu mode changes
 * 
 * 
 * @author G. Sturr
 *
 */
public class SpreadsheetToolbarManager {

	private Application app;
	private SpreadsheetView view;
	private MyTable table;

	private CreateObjectDialog id;


	public SpreadsheetToolbarManager(Application app, SpreadsheetView view){

		this.app = app;
		this.view = view;
		this.table = view.getTable();
	}



	public void  handleModeChange(int mode){

		//Application.printStacktrace("");
		table.setTableMode(table.TABLE_MODE_STANDARD);

		switch (mode) {	


		case EuclidianConstants.MODE_SPREADSHEET_ONEVARSTATS:
			if(table.getCellRangeProcessor().isOneVarStatsPossible(table.selectedCellRanges))
				view.showStatDialog(StatDialog.MODE_ONEVAR);
			break;

		case EuclidianConstants.MODE_SPREADSHEET_TWOVARSTATS:
			if(table.getCellRangeProcessor().isCreatePointListPossible(table.selectedCellRanges))
				view.showStatDialog(StatDialog.MODE_REGRESSION);
			break;

		case EuclidianConstants.MODE_SPREADSHEET_MULTIVARSTATS:
			if(table.getCellRangeProcessor().isMultiVarStatsPossible(table.selectedCellRanges))
				view.showStatDialog(StatDialog.MODE_MULTIVAR);
			break;

		case EuclidianConstants.MODE_SPREADSHEET_CREATE_LIST:

			//if(!app.getSelectedGeos().isEmpty() && prevMode == mode){
			if(!table.selectedCellRanges.get(0).isEmpty()){
				id = new CreateObjectDialog(app,view, CreateObjectDialog.TYPE_LIST);
				id.setVisible(true);
			}
			break;


		case EuclidianConstants.MODE_SPREADSHEET_CREATE_LISTOFPOINTS:
			if(table.getCellRangeProcessor().isCreatePointListPossible(table.selectedCellRanges)){
				id = new CreateObjectDialog(app,view, CreateObjectDialog.TYPE_LISTOFPOINTS);
				id.setVisible(true);}

			break;


		case EuclidianConstants.MODE_SPREADSHEET_CREATE_MATRIX:
			if (table.getCellRangeProcessor().isCreateMatrixPossible(table.selectedCellRanges)){
				id = new CreateObjectDialog(app,view, CreateObjectDialog.TYPE_MATRIX);
				id.setVisible(true);
			}
			break;


		case EuclidianConstants.MODE_SPREADSHEET_CREATE_TABLETEXT:
			if(table.getCellRangeProcessor().isCreateMatrixPossible(table.selectedCellRanges)){
				id = new CreateObjectDialog(app,view, CreateObjectDialog.TYPE_TABLETEXT);
				id.setVisible(true);
			}
			break;

		case EuclidianConstants.MODE_SPREADSHEET_CREATE_POLYLINE:
			if(table.getCellRangeProcessor().isCreatePointListPossible(table.selectedCellRanges)){
				id = new CreateObjectDialog(app,view, CreateObjectDialog.TYPE_POLYLINE);
				id.setVisible(true);
			}
			break;


			
		case EuclidianConstants.MODE_SPREADSHEET_SUM:
		case EuclidianConstants.MODE_SPREADSHEET_AVERAGE:
		case EuclidianConstants.MODE_SPREADSHEET_COUNT:
		case EuclidianConstants.MODE_SPREADSHEET_MIN:
		case EuclidianConstants.MODE_SPREADSHEET_MAX:
			
			// Handle autofunction modes
			
			table.setTableMode(table.TABLE_MODE_AUTOFUNCTION);

			break;

		default:
			// ignore other modes
		}				

	}







}
