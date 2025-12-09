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

package org.geogebra.common.gui.view.spreadsheet;

import org.geogebra.common.kernel.View;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;
import org.geogebra.common.spreadsheet.core.TabularRange;
import org.geogebra.editor.share.util.Unicode;

/**
 * Spreadsheet view (Classic).
 */
public interface SpreadsheetViewInterface extends View {

	final static String LEFT_CLASS_RULE = Unicode.LESS_EQUAL + " x <";
	final static String RIGHT_CLASS_RULE = "< x " + Unicode.LESS_EQUAL;
	final static String LESS_THAN_OR_EQUAL_TO_X = Unicode.LESS_EQUAL + " X";
	final static String GREATER_THAN_OR_EQUAL_TO_X = "X " + Unicode.GREATER_EQUAL;
	final static String X_BETWEEN = Unicode.LESS_EQUAL + " X "
			+ Unicode.LESS_EQUAL;

	// x -> Y
	static final String X_TO_Y = "X " + Unicode.IMPLIES + " Y";

	// Y <- X
	static final String Y_FROM_X = "Y " + Unicode.IMPLIED_FROM + " X";

	/**
	 * @return spreadsheet table
	 */
	public MyTableInterface getSpreadsheetTable();

	/**
	 * Revalidate row header (desktop only).
	 */
	public void rowHeaderRevalidate();

	/**
	 * Store new cell format in settings.
	 * @param cellFormat cell format
	 */
	public void updateCellFormat(String cellFormat);

	/**
	 * @return parent application
	 */
	public App getApplication();

	/**
	 * Scroll into view if needed.
	 * @param geo element
	 * @param labelNew new label
	 */
	public void scrollIfNeeded(GeoElement geo, String labelNew);

	/**
	 * Show a dialog to set up tracing.
	 * @param geo element to be traced
	 * @param traceCell trace destination
	 */
	public void showTraceDialog(GeoElement geo, TabularRange traceCell);

	/**
	 * @param enable whether to enable keyboard
	 */
	void setKeyboardEnabled(boolean enable);

	/**
	 * @return whether this view is visible
	 */
	public boolean isShowing();

}
