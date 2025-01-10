package org.geogebra.common.gui.view.table.column;

import org.geogebra.common.gui.view.table.TableValuesCell;
import org.geogebra.common.gui.view.table.TableValuesListener;
import org.geogebra.common.kernel.kernelND.GeoEvaluatable;

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