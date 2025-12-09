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

import java.util.Objects;

import javax.annotation.CheckForNull;

/**
 * A reference for a single spreadsheet cell (e.g., A1, $A$1, AAA$10)
 */
final class SpreadsheetCellReference {

	final int rowIndex;
	final boolean rowIsAbsolute;
	final int columnIndex;
	final boolean columnIsAbsolute;

	SpreadsheetCellReference(int rowIndex, int columnIndex) {
		this(rowIndex, false, columnIndex, false);
	}

	SpreadsheetCellReference(int rowIndex, boolean rowIsAbsolute, int columnIndex,
			boolean columnIsAbsolute) {
		this.rowIndex = rowIndex;
		this.rowIsAbsolute = rowIsAbsolute;
		this.columnIndex = columnIndex;
		this.columnIsAbsolute = columnIsAbsolute;
	}

	public boolean equalsIgnoringAbsolute(@CheckForNull SpreadsheetCellReference other) {
		if (other == null) {
			return false;
		}
		return rowIndex == other.rowIndex && columnIndex == other.columnIndex;
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof SpreadsheetCellReference)) {
			return false;
		}
		SpreadsheetCellReference other = (SpreadsheetCellReference) object;
		return rowIndex == other.rowIndex
				&& rowIsAbsolute == other.rowIsAbsolute
				&& columnIndex == other.columnIndex
				&& columnIsAbsolute == other.columnIsAbsolute;
	}

	@Override
	public int hashCode() {
		return Objects.hash(rowIndex, rowIsAbsolute, columnIndex, columnIsAbsolute);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if (columnIsAbsolute) {
			sb.append("$");
		}
		sb.append(Spreadsheet.getColumnName(columnIndex));
		if (rowIsAbsolute) {
			sb.append("$");
		}
		sb.append(rowIndex + 1);
		return sb.toString();
	}
}
