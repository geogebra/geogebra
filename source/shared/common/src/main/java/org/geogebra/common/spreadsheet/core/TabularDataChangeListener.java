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
 * An abstraction (only implemented by {@link Spreadsheet}) for listening to changes to
 * {@code KernelTabularDataAdapter}'s data, data dimensions, or cell sizes.
 *
 * @apiNote All indices (row, column) are 0-based.
 */
public interface TabularDataChangeListener {

	/**
	 * Called when the data at (row, column) was updated or deleted.
	 * @param row Row index, or -1 if all rows are affected.
	 * @param column Column index, or -1 if all columns are affected.
	 */
	void tabularDataDidChange(int row, int column);

	/**
	 * Called when the number or size of rows and/or columns has changed.
	 * @param dimensions New spreadsheet dimensions.
	 */
	void tabularDataDimensionsDidChange(SpreadsheetDimensions dimensions);
}
