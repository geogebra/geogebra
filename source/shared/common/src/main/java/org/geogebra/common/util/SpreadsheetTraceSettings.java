package org.geogebra.common.util;

import java.util.ArrayList;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.util.debug.Log;

/**
 * This class creates a set of fields for GeoElements that will be traced in the
 * spreadsheet.
 * 
 * @author G.Sturr 2010-5-20
 */
public class SpreadsheetTraceSettings {

	/** First trace column */
	public int traceColumn1 = -1;

	/** Last trace column */
	public int traceColumn2 = -1;

	/** First trace row */
	public int traceRow1 = -1;

	/** Last trace row */
	public int traceRow2 = -1;

	/** Current trace row */
	public int tracingRow = 0;

	/** Number of rows used for tracing */
	public int numRows = 10;

	/** Number of rows reserved for headers */
	public int headerOffset = 1;

	/** List of most recently recorded trace values */
	public ArrayList<Double> lastTrace = new ArrayList<>();

	// ============================
	// Flags
	// ============================

	/**
	 * Flag to set column reset behavior. If true then cells are traced in a new
	 * column after each mouse pause. This is the default behavior in v3.2
	 */
	public boolean doColumnReset = false;

	/** Flag to perform a column reset (i.e. trace rows in the next column) */
	public boolean needsColumnReset = false;

	/** Flag to limit the number of tracing rows */
	public boolean doRowLimit = false;

	/** Flag to hide/show header label */
	public boolean showLabel = true;

	/** Flag to hide/show a list of traced cells */
	public boolean showTraceList = false;

	/** Flag to trace with geo copies rather than numeric values */
	public boolean doTraceGeoCopy = false;

	/** Flag to pause the trace */
	public boolean pause = false;

	/**
	 * Prints current field values.
	 * 
	 * @param geo
	 *            element to debug
	 */
	public void debug(GeoElement geo) {
		Log.debug("=====================================");
		Log.debug(geo.toString(StringTemplate.defaultTemplate));
		Log.debug("traceColumn1 = " + traceColumn1);
		Log.debug("traceColumn2 = " + traceColumn2);
		Log.debug("traceRow1 = " + traceRow1);
		Log.debug("traceRow2 = " + traceRow2);
		Log.debug("tracingRow = " + tracingRow);
		Log.debug("numRows = " + numRows);
		Log.debug("headerOffset = " + headerOffset);
		Log.debug("doColumnReset = " + doColumnReset);
		Log.debug("needsColumnReset =" + needsColumnReset);
		Log.debug("doRowLimit =" + doRowLimit);
		Log.debug("showLabel =" + showLabel);
		Log.debug("showTraceList =" + showTraceList);
		Log.debug("doTraceGeoCopy =" + doTraceGeoCopy);
		Log.debug("=====================================");
	}

}