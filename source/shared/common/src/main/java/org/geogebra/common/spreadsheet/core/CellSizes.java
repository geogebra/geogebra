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
