package org.geogebra.common.gui.view.spreadsheet;

import org.geogebra.common.kernel.View;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;
import org.geogebra.common.util.lang.Unicode;

public interface SpreadsheetViewInterface extends View {

	final static String LEFT_CLASS_RULE = Unicode.LESS_EQUAL + " x <";
	final static String RIGHT_CLASS_RULE = "< x " + Unicode.LESS_EQUAL;
	final static String LESS_THAN_OR_EQUAL_TO_X = Unicode.LESS_EQUAL + " X";

	public MyTableInterface getSpreadsheetTable();

	public void rowHeaderRevalidate();

	public void columnHeaderRevalidate();

	public void updateCellFormat(String s);

	public App getApplication();

	public int getMode();

	public void scrollIfNeeded(GeoElement geo, String labelNew);

	public void showTraceDialog(GeoElement geo, CellRange traceCell);

	public void setKeyboardEnabled(boolean enable);
}
