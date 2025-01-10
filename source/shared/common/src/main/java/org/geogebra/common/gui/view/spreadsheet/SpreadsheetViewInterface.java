package org.geogebra.common.gui.view.spreadsheet;

import org.geogebra.common.kernel.View;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;
import org.geogebra.common.spreadsheet.core.TabularRange;

import com.himamis.retex.editor.share.util.Unicode;

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

	public MyTableInterface getSpreadsheetTable();

	public void rowHeaderRevalidate();

	public void columnHeaderRevalidate();

	public void updateCellFormat(String s);

	public App getApplication();

	public int getMode();

	public void scrollIfNeeded(GeoElement geo, String labelNew);

	public void showTraceDialog(GeoElement geo, TabularRange traceCell);

	public void setKeyboardEnabled(boolean enable);

	/**
	 * @return whether this view is visible
	 */
	public boolean isShowing();

}
