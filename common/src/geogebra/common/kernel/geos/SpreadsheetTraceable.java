package geogebra.common.kernel.geos;

import geogebra.common.kernel.arithmetic.ExpressionValue;

import java.util.ArrayList;

/**
 * @author Michael Borcherds
 * 
 * NumberValue extends SpreadsheetTraceable as all NumberValues can be traced to spreadsheet
 * 
 * default implementations in GeoElement
 */
public interface SpreadsheetTraceable extends ExpressionValue {
	
	/*
	 * list containing GeoNumeric / GeoAngle
	 */
	public ArrayList<GeoNumeric> getSpreadsheetTraceList();
	public StringBuilder[] getColumnHeadings();

}
