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
 * A carrier structure for spreadsheet cell size info.
 */
public final class CellSizes {

	public final @Nonnull Map<Integer, Double> customColumnWidths;
	public final @Nonnull Map<Integer, Double> customRowHeights;

	/**
	 * @param customColumnWidths map {@code columnIndex => width} for columns with non-default size
	 * @param customRowHeights map {@code rowIndex => height} for rows with non-default size
	 */
	public CellSizes(@Nonnull Map<Integer, Double> customColumnWidths,
			@Nonnull Map<Integer, Double> customRowHeights) {
		this.customColumnWidths = customColumnWidths;
		this.customRowHeights = customRowHeights;
	}
}
