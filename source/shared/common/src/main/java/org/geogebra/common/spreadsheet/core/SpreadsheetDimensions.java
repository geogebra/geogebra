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

import java.util.Map;

import javax.annotation.Nonnull;

/**
 * Provides the number of rows and columns in the spreadsheet, and the column/row sizes.
 *
 * @apiNote This interface only exists to abstract out the {@code SpreadsheetSettings} from the
 * {@code spreadsheet.core} package (only SpreadsheetSettings implements this interface).
 */
public interface SpreadsheetDimensions {
	/**
	 * @return column widths
	 */
	@Nonnull Map<Integer, Double> getColumnWidths();

	/**
	 * @return row heights
	 */
	@Nonnull Map<Integer, Double> getRowHeights();

	/**
	 * @return number of rows
	 */
	int getRows();

	/**
	 * @return number of columns
	 */
	int getColumns();
}
