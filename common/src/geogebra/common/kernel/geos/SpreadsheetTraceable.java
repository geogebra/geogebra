/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/
package geogebra.common.kernel.geos;

import geogebra.common.kernel.arithmetic.ExpressionValue;
import geogebra.common.util.SpreadsheetTraceSettings;

import java.util.ArrayList;

/**
 * @author Michael Borcherds
 * 
 * NumberValue extends SpreadsheetTraceable as all NumberValues can be traced to spreadsheet
 * 
 * default implementations in GeoElement
 */
public interface SpreadsheetTraceable extends ExpressionValue {
	
	/**
	 * @return list containing GeoNumeric / GeoAngle
	 */
	public void addToSpreadsheetTraceList(ArrayList<GeoNumeric> al);
	/**
	 * @return list of column headings
	 */
	public ArrayList<GeoText> getColumnHeadings();
	public SpreadsheetTraceSettings getTraceSettings();

}
