package org.geogebra.web.web.gui.view.spreadsheet;

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


	public SpreadsheetToolbarManagerW(AppW app, SpreadsheetViewW view){

		this.app = app;
		this.view = view;
		this.table = (MyTableW) view.getSpreadsheetTable();
	}



	public void  handleModeChange(int mode){

		//Application.printStacktrace("");
		table.setTableMode(MyTable.TABLE_MODE_STANDARD);

		switch (mode) {	

		
		case EuclidianConstants.MODE_SPREADSHEET_CREATE_LIST:

			//if(!app.getSelectedGeos().isEmpty() && prevMode == mode){
			if(!table.selectedCellRanges.get(0).isEmpty()){
				id = new CreateObjectDialogW(app,view, CreateObjectModel.TYPE_LIST);
				id.setVisible(true);
			}
			break;


		case EuclidianConstants.MODE_SPREADSHEET_CREATE_LISTOFPOINTS:
			if(table.getCellRangeProcessor().isCreatePointListPossible(table.selectedCellRanges)){
				id = new CreateObjectDialogW(app,view, CreateObjectModel.TYPE_LISTOFPOINTS);
				id.setVisible(true);}

			break;


		case EuclidianConstants.MODE_SPREADSHEET_CREATE_MATRIX:
			if (table.getCellRangeProcessor().isCreateMatrixPossible(table.selectedCellRanges)){
				id = new CreateObjectDialogW(app,view, CreateObjectModel.TYPE_MATRIX);
				id.setVisible(true);
			}
			break;


		case EuclidianConstants.MODE_SPREADSHEET_CREATE_TABLETEXT:
			if(table.getCellRangeProcessor().isCreateMatrixPossible(table.selectedCellRanges)){
				id = new CreateObjectDialogW(app,view, CreateObjectModel.TYPE_TABLETEXT);
				id.setVisible(true);
			}
			break;

		case EuclidianConstants.MODE_SPREADSHEET_CREATE_POLYLINE:
			if(table.getCellRangeProcessor().isCreatePointListPossible(table.selectedCellRanges)){
				id = new CreateObjectDialogW(app,view, CreateObjectModel.TYPE_POLYLINE);
				id.setVisible(true);
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







}
