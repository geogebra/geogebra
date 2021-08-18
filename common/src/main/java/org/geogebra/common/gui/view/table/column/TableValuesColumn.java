package org.geogebra.common.gui.view.table.column;

import org.geogebra.common.kernel.kernelND.GeoEvaluatable;

public interface TableValuesColumn {

	/**
	 * Get the double value for the row.
	 * @param row row
	 * @return double value
	 */
	double getDoubleValue(int row);

	/**
	 * Get the string value for the row
	 * @param row row
	 * @return string value
	 */
	String getStringValue(int row);

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
	 * Resets the cache values of the column.
	 * Ensure to call this before calling any of the
	 * {@link #getDoubleValue(int)} or {@link #getStringValue(int)}
	 * @param size size
	 */
	void invalidateValues(int size);

	/**
	 * Get the evaluatable.
	 * @return evaluatable
	 */
	GeoEvaluatable getEvaluatable();
}