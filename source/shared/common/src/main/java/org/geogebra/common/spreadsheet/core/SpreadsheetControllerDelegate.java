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

import javax.annotation.Nonnull;

/**
 * {@link SpreadsheetController}-to-{@link Spreadsheet} coordination backlink.
 */
interface SpreadsheetControllerDelegate {

	/**
	 * Invoked when the list of cell references (in the currently editing cell), or the current
	 * cell reference (the one under the cursor) changes.
	 * @param cellSizes the new cell sizes
	 */
	void cellSizesChanged(@Nonnull CellSizes cellSizes);

	/**
	 * A state change in the controller is requiring a repaint of the spreadsheet.
	 */
	void repaintNeeded();
}
