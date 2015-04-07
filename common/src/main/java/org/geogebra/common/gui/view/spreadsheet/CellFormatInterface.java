package org.geogebra.common.gui.view.spreadsheet;

import java.util.HashMap;

import org.geogebra.common.awt.GPoint;

public interface CellFormatInterface {

	Object getCellFormat(int x, int y, int formatBorder);

	HashMap<GPoint, Object> getFormatMap(int formatBorder);

	void getXML(StringBuilder sb);

	void processXMLString(String cellFormat);

	void shiftFormats(int startIndex, int shiftAmount,
			CellRangeProcessor.Direction direction);

}
