package org.geogebra.common.spreadsheet.core;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.geogebra.regexp.shared.MatchResult;
import org.geogebra.regexp.shared.RegExp;

/**
 * Methods for parsing spreadsheet cell references.
 *
 * @apiNote All indices are 0-based.
 * @implNote This duplicates some code from GeoElementSpreadsheet, which we don't want to reuse
 * here (or refactor at this point).
 */
final class SpreadsheetReferenceParsing {

	/** match A1, ABG1, $A$123 but not A0, A000, A0001 etc */
	private static final RegExp CELL_REFERENCE_REGEX = RegExp
			.compile("(?<![A-Za-z0-9\\$])(\\$?)([A-Z]+)(\\$?)([1-9][0-9]*)\\b");

	/** regex capture group with "$" or "" for column */
	private final static int CAPTURE_GROUP_COLUMN_DOLLAR = 1;
	/** regex capture group for column name */
	private final static int CAPTURE_GROUP_COLUMN = 2;
	/** regex capture group with "$" or "" for row */
	private final static int CAPTURE_GROUP_ROW_DOLLAR = 3;
	/** regex capture group for row number */
	private final static int CAPTURE_GROUP_ROW = 4;

	/**
	 * Parse a cell or cell range reference.
	 * @param candidate The input string.
	 * @return A {@code TabularRange} representing the cell (e.g., "A1") or range (e.g., "A1:A10")
	 * reference input string, or {@code null} if candidate is not a valid cell or range reference.
	 */
	static @CheckForNull SpreadsheetReference parseReference(@Nonnull String candidate) {
		String[] parts = candidate.split(":");
		if (parts.length > 2) {
			return null; // must be "A1" or "A1:A10"
		}
		return parseCellReferences(parts[0], parts.length == 2 ? parts[1] : null);
	}

	/**
	 * Parses one or two cell references into a {@link SpreadsheetReference}.
	 * @param fromReference A cell or range reference string.
	 * @param toReference A cell or range reference string. May be {@code null}.
	 * @return A {@code SpreadsheetReference} representing the given cell or range of cells
	 * (if toReference is non-null and a valid cell reference). Returns {@code null} if
	 * {@code fromReference} or {@code toReference} are invalid strings.
	 */
	private static @CheckForNull SpreadsheetReference parseCellReferences(
			@Nonnull String fromReference, @CheckForNull String toReference) {
		SpreadsheetCellReference fromCell = parseCellReference(fromReference);
		if (fromCell == null) {
			return null; // invalid reference
		}
		SpreadsheetCellReference toCell = parseCellReference(toReference);
		if (toCell == null && toReference != null) {
			return null; // invalid reference
		}
		return new SpreadsheetReference(fromCell, toCell);
	}

	/**
	 * Parse a cell reference.
	 * @param cellReference A cell reference (e.g., "A1", "A$1", "$A1", "$A$1").
	 * @return A {@link SpreadsheetReference} representing the cell reference, or null if
	 * {@code cellReference} is an invalid input.
	 */
	private static @CheckForNull SpreadsheetCellReference parseCellReference(
			@CheckForNull String cellReference) {
		if (cellReference == null) {
			return null;
		}
		MatchResult match = CELL_REFERENCE_REGEX.exec(cellReference);
		if (match == null) {
			return null; // not a valid reference
		}
		boolean columnIsAbsolute = "$".equals(match.getGroup(CAPTURE_GROUP_COLUMN_DOLLAR));
		String columnName = match.getGroup(CAPTURE_GROUP_COLUMN);
		int columnIndex = columnIndexFromName(columnName);
		if (columnIndex < 0) {
			return null; // invalid column name
		}
		boolean rowIsAbsolute = "$".equals(match.getGroup(CAPTURE_GROUP_ROW_DOLLAR));
		try {
			int rowNumber = Integer.parseInt(match.getGroup(CAPTURE_GROUP_ROW));
			return new SpreadsheetCellReference(rowNumber - 1, rowIsAbsolute,
					columnIndex, columnIsAbsolute);
		} catch (NumberFormatException e) {
			return null;
		}
	}

	/**
	 * Find the column index for a column name.
	 * @param columnName A spreadsheet column name, e.g. AAB
	 * @return The column index (0-based), or -1 if {@code columnName} is not a valid column name.
	 */
	private static int columnIndexFromName(@Nonnull String columnName) {
		String name = columnName.trim().toUpperCase();
		if (name.length() >= 7) {
			return -1; // 26^7 = 8.031.810.176, this would already overflow a 32bit int
		}
		int column = 0;
		for (int i = 0; i < name.length(); i++) {
			char ch = name.charAt(i);
			if (ch < 'A' || ch > 'Z') {
				return -1;
			}
			int value = (ch - 'A') + 1;
			column = column * 26 + value;
		}
		if (column > Spreadsheet.MAX_COLUMNS) {
			return -1;
		}
		return column - 1;
	}
}
