package org.geogebra.common.util;

import java.util.ArrayList;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;

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
	public ArrayList<Double> lastTrace = new ArrayList<Double>();

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

	/********************************************************
	 * Constructor.
	 */
	public SpreadsheetTraceSettings() {

	}

	/**
	 * Prints current field values.
	 * 
	 * @param geo
	 */
	public void debug(GeoElement geo) {
		System.out.println("=====================================");
		System.out.println(geo.toString(StringTemplate.defaultTemplate));
		System.out.println("traceColumn1 = " + traceColumn1);
		System.out.println("traceColumn2 = " + traceColumn2);
		System.out.println("traceRow1 = " + traceRow1);
		System.out.println("traceRow2 = " + traceRow2);
		System.out.println("tracingRow = " + tracingRow);
		System.out.println("numRows = " + numRows);
		System.out.println("headerOffset = " + headerOffset);
		System.out.println("doColumnReset = " + doColumnReset);
		System.out.println("needsColumnReset =" + needsColumnReset);
		System.out.println("doRowLimit =" + doRowLimit);
		System.out.println("showLabel =" + showLabel);
		System.out.println("showTraceList =" + showTraceList);
		System.out.println("doTraceGeoCopy =" + doTraceGeoCopy);
		System.out.println("=====================================");
	}

}