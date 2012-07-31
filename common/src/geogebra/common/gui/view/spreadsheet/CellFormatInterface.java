package geogebra.common.gui.view.spreadsheet;

import geogebra.common.awt.GPoint;

import java.util.HashMap;

public interface CellFormatInterface {

	Object getCellFormat(GPoint cell, int formatBorder);

	HashMap<GPoint, Object> getFormatMap(int formatBorder);

	void getXML(StringBuilder sb);

	void processXMLString(String cellFormat);

}
