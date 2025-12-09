/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.web.full.gui.view.spreadsheet;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.gui.view.spreadsheet.CellRangeUtil;
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

	private CreateObjectDialogW createObjectDialog;
	public static final int TYPE_LIST = 0;
	public static final int TYPE_MATRIX = 2;
	public static final int TYPE_LISTOFPOINTS = 1;
	public static final int TYPE_TABLETEXT = 3;
	public static final int TYPE_POLYLINE = 4;

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
		table.setTableMode(MyTable.TABLE_MODE_STANDARD);

		switch (mode) {

		case EuclidianConstants.MODE_SPREADSHEET_CREATE_LIST:
			//if(!app.getSelectedGeos().isEmpty() && prevMode == mode){
			if (!CellRangeUtil.isEmpty(table.getFirstSelection(), app)) {
				openDialog(CreateObjectModel.TYPE_LIST);
			}
			break;

		case EuclidianConstants.MODE_SPREADSHEET_CREATE_LISTOFPOINTS:
			if (table.getCellRangeProcessor()
					.isCreatePointListPossible(table.getSelectedRanges())) {
				openDialog(CreateObjectModel.TYPE_LISTOFPOINTS);
			}
			break;

		case EuclidianConstants.MODE_SPREADSHEET_CREATE_MATRIX:
			if (table.getCellRangeProcessor()
					.isCreateMatrixPossible(table.getSelectedRanges())) {
				openDialog(CreateObjectModel.TYPE_MATRIX);
			}
			break;

		case EuclidianConstants.MODE_SPREADSHEET_CREATE_TABLETEXT:
			if (table.getCellRangeProcessor()
					.isCreateMatrixPossible(table.getSelectedRanges())) {
				openDialog(CreateObjectModel.TYPE_TABLETEXT);
			}
			break;

		case EuclidianConstants.MODE_SPREADSHEET_CREATE_POLYLINE:
			if (table.getCellRangeProcessor()
					.isCreatePointListPossible(table.getSelectedRanges())) {
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
		createObjectDialog = new CreateObjectDialogW(app, view, type,
				CreateObjectModel.getTitle(type));
		createObjectDialog.show();
	}
}