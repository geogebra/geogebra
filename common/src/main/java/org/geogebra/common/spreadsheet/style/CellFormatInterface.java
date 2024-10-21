package org.geogebra.common.spreadsheet.style;

import java.util.HashMap;

import org.geogebra.common.gui.view.spreadsheet.HasTableSelection;
import org.geogebra.common.spreadsheet.core.Direction;
import org.geogebra.common.spreadsheet.core.SpreadsheetCoords;

public interface CellFormatInterface {

	Object getCellFormat(int x, int y, int formatBorder);

	HashMap<SpreadsheetCoords, Object> getFormatMap(int formatBorder);

	void getXML(StringBuilder sb);

	void processXMLString(String cellFormat);

	int getAlignment(int col, int row, boolean b);

	void shiftFormats(int startIndex, int shiftAmount,
			Direction direction);

	void setFormat(SpreadsheetCoords coords, int formatBgcolor, Object o);

	void setTable(HasTableSelection table);

}
