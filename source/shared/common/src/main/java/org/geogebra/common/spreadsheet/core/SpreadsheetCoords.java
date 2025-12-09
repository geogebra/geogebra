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

import com.google.j2objc.annotations.Property;

/**
 * Spreadsheet cell coordinates (0-based)
 */
public final class SpreadsheetCoords {

	@Property("readonly")
	public int row;
	@Property("readonly")
	public int column;

	public SpreadsheetCoords() {
		// (0, 0)
	}

	/**
	 * @param row spreadsheet row (0 based)
	 * @param column spreadsheet column (0 based)
	 */
	public SpreadsheetCoords(int row, int column) {
		this.row = row;
		this.column = column;
	}

	/**
	 * @param other other coordinates
	 */
	public void setLocation(SpreadsheetCoords other) {
		this.column = other.column;
		this.row = other.row;
	}

	/**
	 * @param row new row
	 * @param column new column
	 */
	public void setLocation(int row, int column) {
		this.column = column;
		this.row = row;
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof SpreadsheetCoords
				&& (row == ((SpreadsheetCoords) obj).row)
				&& (column == ((SpreadsheetCoords) obj).column);
	}

	@Override
	public int hashCode() {
		return 100 * row + column;
	}
}
