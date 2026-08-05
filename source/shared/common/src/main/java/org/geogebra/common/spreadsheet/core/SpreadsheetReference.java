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

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * A (relative or absolute) spreadsheet cell or cell range reference.
 *
 * @apiNote Row and column indexes are 0-based.
 */
public final class SpreadsheetReference {

	public final @NonNull SpreadsheetCellReference fromCell;
	public final @Nullable SpreadsheetCellReference toCell;

	/**
	 * Null-safe factory.
	 * @param range A range, possibly {@code null}.
	 * @return A spreadsheet reference, or {@code null} if {@code range} is null.
	 */
	public static @Nullable SpreadsheetReference fromRange(@Nullable TabularRange range) {
		return range != null ? new SpreadsheetReference(range) : null;
	}

	SpreadsheetReference(@NonNull SpreadsheetCellReference fromCell,
			@Nullable SpreadsheetCellReference toCell) {
		this.fromCell = fromCell;
		this.toCell = toCell;
	}

	SpreadsheetReference(@NonNull TabularRange range) {
		this.fromCell = new SpreadsheetCellReference(range.getMinRow(), range.getMinColumn());
		this.toCell = range.isSingleCell() ? null
				: new SpreadsheetCellReference(range.getMaxRow(), range.getMaxColumn());
	}

	/**
	 * @return {@code true} if references a single cell only
	 */
	public boolean isSingleCell() {
		return !isRange();
	}

	boolean isRange() {
		return toCell != null;
	}

	boolean equalsIgnoringAbsolute(@Nullable SpreadsheetReference other) {
		if (other == null) {
			return false;
		}
		if (!fromCell.equalsIgnoringAbsolute(other.fromCell)) {
			return false;
		}
		if (toCell != null) {
			return toCell.equalsIgnoringAbsolute(other.toCell);
		}
		return other.toCell == null;
	}

	@Override
	public String toString() {
		return toCell == null ? fromCell.toString() : fromCell + ":" + toCell;
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof SpreadsheetReference other)) {
			return false;
		}
		return Objects.equals(fromCell, other.fromCell)
				&& Objects.equals(toCell, other.toCell);
	}

	@Override
	public int hashCode() {
		return Objects.hash(fromCell, toCell);
	}
}
