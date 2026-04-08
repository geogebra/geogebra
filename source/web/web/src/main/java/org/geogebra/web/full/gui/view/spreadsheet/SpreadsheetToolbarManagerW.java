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

import java.util.List;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.gui.view.spreadsheet.CellRangeUtil;
import org.geogebra.common.gui.view.spreadsheet.CreateObjectModel;
import org.geogebra.common.gui.view.spreadsheet.SpreadsheetModeProcessor;
import org.geogebra.common.gui.view.spreadsheet.SpreadsheetToolProcessor;
import org.geogebra.common.spreadsheet.core.Spreadsheet;
import org.geogebra.common.spreadsheet.core.TabularRange;
import org.geogebra.common.spreadsheet.style.CellFormat;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.GlobalKeyDispatcherW;

/**
 * Utility class to handle toolbar menu mode changes
 * 
 * 
 * @author G. Sturr
 *
 */
public class SpreadsheetToolbarManagerW {

	private final AppW app;
	private final Spreadsheet spreadsheet;

	/**
	 * @param app
	 *            application
	 */
	public SpreadsheetToolbarManagerW(AppW app) {
		this.app = app;
		this.spreadsheet = app.getSpreadsheet();
	}

	/**
	 * @param mode
	 *            app mode
	 */
	public void handleModeChange(int mode) {
		CellFormat format = (CellFormat) app.getSpreadsheetTableModel().getCellFormat(null);
		SpreadsheetToolProcessor toolProcessor = new SpreadsheetToolProcessor(app, format);
		if (spreadsheet == null) {
			return;
		}
		List<TabularRange> selections = spreadsheet.getSelections();
		switch (mode) {

		case EuclidianConstants.MODE_SPREADSHEET_CREATE_LIST:
			if (!selections.isEmpty()
					&& !CellRangeUtil.isEmpty(selections.get(0),
							app.getSpreadsheetTableModel())) {
				openDialog(CreateObjectModel.TYPE_LIST);
			}
			break;

		case EuclidianConstants.MODE_SPREADSHEET_CREATE_LISTOFPOINTS:
			if (toolProcessor
					.isCreatePointListPossible(selections)) {
				openDialog(CreateObjectModel.TYPE_LISTOFPOINTS);
			}
			break;

		case EuclidianConstants.MODE_SPREADSHEET_CREATE_MATRIX:
			if (toolProcessor
					.isCreateMatrixPossible(selections)) {
				openDialog(CreateObjectModel.TYPE_MATRIX);
			}
			break;

		case EuclidianConstants.MODE_SPREADSHEET_CREATE_TABLETEXT:
			if (toolProcessor
					.isCreateMatrixPossible(selections)) {
				openDialog(CreateObjectModel.TYPE_TABLETEXT);
			}
			break;

		case EuclidianConstants.MODE_SPREADSHEET_CREATE_POLYLINE:
			if (toolProcessor
					.isCreatePointListPossible(selections)) {
				openDialog(CreateObjectModel.TYPE_POLYLINE);
			}
			break;
			
		case EuclidianConstants.MODE_SPREADSHEET_SUM:
		case EuclidianConstants.MODE_SPREADSHEET_AVERAGE:
		case EuclidianConstants.MODE_SPREADSHEET_COUNT:
		case EuclidianConstants.MODE_SPREADSHEET_MIN:
		case EuclidianConstants.MODE_SPREADSHEET_MAX:
			// Handle autofunction modes
			if (!selections.isEmpty()) {
				new SpreadsheetModeProcessor(app, null).performAutoFunctionCreation(
						selections.get(0),
						GlobalKeyDispatcherW.getShiftDown());
			}
			break;

		default:
			// ignore other modes
		}
	}

	private void openDialog(int type) {
		if (spreadsheet != null) {
			new CreateObjectDialogW(app, spreadsheet, type,
					CreateObjectModel.getTitle(type)).show();
		}
	}
}
