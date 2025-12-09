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

import org.geogebra.common.util.MouseCursor;

/**
 * Represents ongoing drag operation
 */
final class DragState {
	final MouseCursor cursor;
	final int startColumn;
	final int startRow;

	/**
	 * @param cursor cursor type for desktop devices
	 * @param startRow drag start row (-1 for header)
	 * @param startColumn drag start column (-1 for header)
	 */
	DragState(MouseCursor cursor, int startRow, int startColumn) {
		this.cursor = cursor;
		this.startRow = startRow;
		this.startColumn = startColumn;
	}

	boolean isModifyingOperation() {
		return cursor != MouseCursor.DEFAULT;
	}
}
