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

package org.geogebra.common.kernel.geos;

import java.util.ArrayList;

import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.util.SpreadsheetTraceSettings;

/**
 * @author Michael Borcherds
 * 
 *         NumberValue extends SpreadsheetTraceable as all NumberValues can be
 *         traced to spreadsheet
 * 
 *         default implementations in GeoElement TODO this interface should
 *         extend GeoElementND and should not be extended by NumberValue
 */
public interface SpreadsheetTraceable extends ExpressionValue {

	/**
	 * @param al
	 *            list containing GeoNumeric / GeoAngle
	 */
	public void addToSpreadsheetTraceList(ArrayList<GeoNumeric> al);

	/**
	 * @return list of column headings
	 */
	public ArrayList<GeoText> getColumnHeadings();

	/**
	 * @return spreadsheet trace settings
	 */
	public SpreadsheetTraceSettings getTraceSettings();

}
