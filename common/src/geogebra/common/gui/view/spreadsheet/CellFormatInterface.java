package geogebra.common.gui.view.spreadsheet;

import java.util.HashMap;

import geogebra.common.awt.GPoint;

public interface CellFormatInterface {

	Object getCellFormat(GPoint cell, int formatBorder);

	HashMap<GPoint, Object> getFormatMap(int formatBorder);

	void getXML(StringBuilder sb);

	void processXMLString(String cellFormat);

}
