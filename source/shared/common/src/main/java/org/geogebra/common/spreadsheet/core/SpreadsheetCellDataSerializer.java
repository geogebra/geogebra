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

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

/**
 * An abstraction for spreadsheet cell data conversion.
 * <p>
 * (This prevents direct dependencies on GeoElement and other classes from the kernel package.)
 */
public interface SpreadsheetCellDataSerializer {

	/**
	 * Converts spreadsheet cell data to a string representation for the cell editor.
	 * @param data Spreadsheet cell data.
	 * @return A string representation of the data.
	 */
	@Nonnull String getStringForEditor(@CheckForNull Object data);
}
