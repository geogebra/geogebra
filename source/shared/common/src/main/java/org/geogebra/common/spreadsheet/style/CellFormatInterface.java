package org.geogebra.common.spreadsheet.style;

import java.util.HashMap;

import org.geogebra.common.gui.view.spreadsheet.HasTableSelection;
import org.geogebra.common.spreadsheet.core.Direction;
import org.geogebra.common.spreadsheet.core.SpreadsheetCoords;

/**
 * Cell format interface.
 */
public interface CellFormatInterface {

	/**
	 * Get a format property of a cell
	 * @param col column index
	 * @param row row index
	 * @param formatType one of CellFormat.FORMAT_* constants
	 * @return format property value, type depends on {@code formatType}
	 */
	Object getCellFormat(int col, int row, int formatType);

	/**
	 * @param formatType one of CellFormat.FORMAT_* constants
	 * @return mapping from cell coordinates to property values
	 */
	HashMap<SpreadsheetCoords, Object> getFormatMap(int formatType);

	/**
	 * Returns XML representation of the format maps
	 */
	void getXML(StringBuilder sb);

	/**
	 * Decodes XML string and puts format values into the format maps.
	 *
	 * @param cellFormat
	 *            String to be decoded
	 */
	void processXMLString(String cellFormat);

	/**
	 * Gets alignment of a cell.
	 * @param col column index
	 * @param row row index
	 * @param textCell whether the cell holds a text object
	 * @return alignment (one of CellFormat.ALIGN_* constants),
	 *          if not present default depends on {@code textCell}
	 */
	int getAlignment(int col, int row, boolean textCell);

	/**
	 * Shift a portion of format properties in given direction.
	 * @param startIndex row/column index
	 * @param shiftAmount displacement in given direction (must be non-negative)
	 * @param direction direction of the shift
	 */
	void shiftFormats(int startIndex, int shiftAmount,
			Direction direction);

	/**
	 * Set format property of a cell.
	 * @param coords cell coordinates
	 * @param formatType one of CellFormat.FORMAT_* constants
	 * @param value property value
	 */
	void setFormat(SpreadsheetCoords coords, int formatType, Object value);

	/**
	 * Set a table as a listener to format changes.
	 * (Relevant for Classic spreadsheet.)
	 * @param table spreadsheet table
	 */
	void setTable(HasTableSelection table);

}
