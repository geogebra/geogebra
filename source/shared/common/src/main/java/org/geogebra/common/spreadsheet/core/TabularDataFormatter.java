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

package org.geogebra.common.spreadsheet.core;

/**
 * Class to convert TabularData to string.
 * Format: columns are separated by \t and rows are separated by \n .
 */
public final class TabularDataFormatter<T> {

	private final TabularData<T> data;

	/**
	 * @param data tabular data
	 */
	TabularDataFormatter(TabularData<T> data) {
		this.data = data;
	}

	/**
	 * Converts the range of cell values to a separated string
	 * by tabs between columns and newlines between rows.
	 *
	 * @param range to convert.
	 * @return the string representation.
	 */
	String toString(TabularRange range) {
		StringBuilder sb = new StringBuilder();
		for (int row = range.getFromRow(); row < range.getToRow() + 1; row++) {
			for (int column = range.getFromColumn(); column < range.getToColumn() + 1; column++) {
				sb.append(data.serializeContentAt(row, column));
				if (column != range.getToColumn()) {
					sb.append('\t');
				}
			}
			if (row != range.getToRow()) {
				sb.append('\n');
			}
		}
		return sb.toString();
	}
}
