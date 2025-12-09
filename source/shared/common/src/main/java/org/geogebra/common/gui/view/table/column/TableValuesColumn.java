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

package org.geogebra.common.gui.view.table.column;

import org.geogebra.common.gui.view.table.TableValuesCell;
import org.geogebra.common.gui.view.table.TableValuesListener;
import org.geogebra.common.kernel.kernelND.GeoEvaluatable;

/**
 * Table values column.
 */
public interface TableValuesColumn extends TableValuesListener {

	/**
	 * Get the double value for the row.
	 * @param row row
	 * @return double value. NaN when the input is not a number.
	 */
	double getDoubleValue(int row);

	/**
	 * Get the string value for the row
	 * @param row row
	 * @return cell value
	 */
	TableValuesCell getCellValue(int row);

	/**
	 * Get the header name
	 * @return header name
	 */
	String getHeader();

	/**
	 * Invalidates the header name, forcing the column to recompute.
	 */
	void invalidateHeader();

	/**
	 * Get the evaluatable.
	 * @return evaluatable
	 */
	GeoEvaluatable getEvaluatable();
}