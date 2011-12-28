package geogebra.common.util;

import geogebra.common.kernel.geos.GeoElement;

import java.util.ArrayList;

/**
 * Spreadsheet trace settings.
 * 
 * @author G.Sturr 2010-5-20
 */
public class TraceSettings {

	public int traceColumn1 = -1;
	public int traceColumn2 = -1;
	public int traceRow1 = -1;
	public int traceRow2 = -1;
	public int tracingRow = 0;
	public int numRows = 10;
	public int headerOffset = 1; // show label is default

	public ArrayList<Double> lastTrace = new ArrayList<Double>();

	public boolean doColumnReset = false;
	public boolean needsColumnReset = false;
	public boolean doRowLimit = false;
	public boolean showLabel = true;
	public boolean showTraceList = false;

	public boolean doTraceGeoCopy = false;

	public void debug(GeoElement geo) {
		System.out.println("=====================================");
		System.out.println(geo.toString());
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

	public TraceSettings clone(TraceSettings t) {
		return this;
	}

}