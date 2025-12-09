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

package org.geogebra.desktop.gui.view.spreadsheet;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.gui.view.spreadsheet.CellRangeUtil;
import org.geogebra.common.gui.view.spreadsheet.CreateObjectModel;
import org.geogebra.common.gui.view.spreadsheet.MyTable;
import org.geogebra.desktop.main.AppD;

/**
 * Utility class to handle toolbar menu mode changes
 * 
 * 
 * @author G. Sturr
 * 
 */
public class SpreadsheetToolbarManager {

	private final AppD app;
	private final SpreadsheetViewD view;
	private final MyTableD table;

	/**
	 * @param app application
	 * @param view spreadsheet
	 */
	public SpreadsheetToolbarManager(AppD app, SpreadsheetViewD view) {
		this.app = app;
		this.view = view;
		this.table = (MyTableD) view.getSpreadsheetTable();
	}

	/**
	 * @param mode app mode
	 */
	public void handleModeChange(int mode) {
		table.setTableMode(MyTable.TABLE_MODE_STANDARD);

		CreateObjectDialog id;
		switch (mode) {

		case EuclidianConstants.MODE_SPREADSHEET_CREATE_LIST:
			if (!CellRangeUtil.isEmpty(table.getFirstSelection(), app)) {
				id = new CreateObjectDialog(app, view,
						CreateObjectModel.TYPE_LIST);
				id.setVisible(true);
			}
			break;

		case EuclidianConstants.MODE_SPREADSHEET_CREATE_LISTOFPOINTS:
			if (table.getCellRangeProcessor()
					.isCreatePointListPossible(table.getSelectedRanges())) {
				id = new CreateObjectDialog(app, view,
						CreateObjectModel.TYPE_LISTOFPOINTS);
				id.setVisible(true);
			}

			break;

		case EuclidianConstants.MODE_SPREADSHEET_CREATE_MATRIX:
			if (table.getCellRangeProcessor()
					.isCreateMatrixPossible(table.getSelectedRanges())) {
				id = new CreateObjectDialog(app, view,
						CreateObjectModel.TYPE_MATRIX);
				id.setVisible(true);
			}
			break;

		case EuclidianConstants.MODE_SPREADSHEET_CREATE_TABLETEXT:
			if (table.getCellRangeProcessor()
					.isCreateMatrixPossible(table.getSelectedRanges())) {
				id = new CreateObjectDialog(app, view,
						CreateObjectModel.TYPE_TABLETEXT);
				id.setVisible(true);
			}
			break;

		case EuclidianConstants.MODE_SPREADSHEET_CREATE_POLYLINE:
			if (table.getCellRangeProcessor()
					.isCreatePointListPossible(table.getSelectedRanges())) {
				id = new CreateObjectDialog(app, view,
						CreateObjectModel.TYPE_POLYLINE);
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
