/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/
package geogebra.common.gui.view.spreadsheet;

import geogebra.common.kernel.geos.GeoElement;
/**
 * Handles tracing geos to spreadsheet
 * @author George Sturr + GeoGebraWeb team
 *
 */
public abstract class SpreadsheetTraceManager {

	/**
	 * @param geo element
	 * @return whether geo is being traced
	 */
	public abstract boolean isTraceGeo(GeoElement geo);

	/**
	 * If a user deletes the contents of a trace column then 
	 * 1) re-insert the header text cell (if header is shown) 
	 * 2) reset the trace row to the top of the column
	 * @param column1 first deleted column
	 * @param column2 last deleted column
	 */
	public abstract void handleColumnDelete(int column1, int column2);

	/**
	 * Start tracing given geo
	 * @param geo geo to be traced
	 */
	public abstract void addSpreadsheetTraceGeo(GeoElement geo);
	
}
