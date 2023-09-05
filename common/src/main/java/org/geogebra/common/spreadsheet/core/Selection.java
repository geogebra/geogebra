package org.geogebra.common.spreadsheet.core;

/**
 * A contiguous range of cells in a {@link Spreadsheet}.
 *
 * @Note: toRow/toCol may be less than fromRow/fromCol, respectively.
 * If toRow is less than fromRow, the selection handle is on the upper
 * edge of the selection rectangle, and on the lower edge otherwise.
 * Similarly, if toCol is less than fromCol, the selection handle is on
 * the left edge of the selection rangle, and on the right edge otherwise.
 */
final class Selection {

	private final TabularRange range;
	private final SelectionType type;

	Selection(SelectionType type, TabularRange range) {
		this.range = range;
		this.type = type;
	}

	boolean isEmpty() {
		return range.isEmpty();
	}

	/**
	 * @param selection other selection
	 * @return bigger selection if this could be merged, null otherwise
	 */
	public Selection merge(Selection selection) {
		if (type != selection.type) {
			return null;
		}
		TabularRange mergedRange = range.merge(selection.range);
		return mergedRange == null ? null : new Selection(type, mergedRange);
	}

	public TabularRange getRange() {
		return range;
	}

	public boolean contains(int row, int column) {
		return (range.fromCol <= column && range.toCol >= column || range.fromCol < 0)
				&& (range.fromRow <= row && range.toRow >= row || range.fromRow < 0);
	}
}
