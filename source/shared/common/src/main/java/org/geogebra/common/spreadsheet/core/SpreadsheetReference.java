package org.geogebra.common.spreadsheet.core;

import java.util.Objects;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

/**
 * A (relative or absolute) spreadsheet cell or cell range reference.
 *
 * @apiNote Row and column indexes are 0-based.
 */
final class SpreadsheetReference {

	final @Nonnull SpreadsheetCellReference fromCell;
	final @CheckForNull SpreadsheetCellReference toCell;

	SpreadsheetReference(@Nonnull SpreadsheetCellReference fromCell,
			@CheckForNull SpreadsheetCellReference toCell) {
		this.fromCell = fromCell;
		this.toCell = toCell;
	}

	boolean isSingleCell() {
		return !isRange();
	}

	boolean isRange() {
		return toCell != null;
	}

	public boolean equalsIgnoringAbsolute(@CheckForNull SpreadsheetReference other) {
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
		StringBuilder sb = new StringBuilder();
		sb.append(fromCell.toString());
		if (toCell != null) {
			sb.append(":");
			sb.append(toCell.toString());
		}
		return sb.toString();
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof SpreadsheetReference)) {
			return false;
		}
		SpreadsheetReference other = (SpreadsheetReference) object;
		return Objects.equals(fromCell, other.fromCell)
				&& Objects.equals(toCell, other.toCell);
	}

	@Override
	public int hashCode() {
		return Objects.hash(fromCell, toCell);
	}
}
