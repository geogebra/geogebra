package geogebra.common.kernel.geos;

import geogebra.common.awt.Point;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.AbstractApplication;

import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;

public class GeoElementSpreadsheet {
	/*
	 * match A1, ABG1, A123 but not A0, A000, A0001 etc
	 */
	public static final RegExp spreadsheetPattern = RegExp
			.compile("\\$?([A-Z]+)\\$?([1-9][0-9]*)");

	//public static final RegExp spreadsheetPatternGlobal = RegExp
	//		.compile("\\$?([A-Z]+)\\$?([1-9][0-9]*)", "g");

	public static String getSpreadsheetColumnName(int i) {
		++i;
		String col = "";
		while (i > 0) {
			col = (char) ('A' + (i - 1) % 26) + col;
			i = (i - 1) / 26;
		}
		return col;
	}

	public static String getSpreadsheetColumnName(String label) {
		MatchResult matcher = spreadsheetPattern.exec(label);
		if (matcher == null)
			return null;
		return matcher.getGroup(1);
	}

	// Cong Liu
	public static String getSpreadsheetCellName(int column, int row) {
		++row;
		return getSpreadsheetColumnName(column) + row;
	}

	/**
	 * Determines spreadsheet row and column indices for a given cell name (e.g.
	 * "B3" sets column = 1 and row = 2. If the cell name does not match a
	 * possible spreadsheet cell then both row and column are returned as -1.
	 * 
	 * @param cellName
	 *            given cell name
	 * @return coordinates of spreedsheet cell
	 */
	public static Point spreadsheetIndices(String cellName) {

		MatchResult matcher = spreadsheetPattern
				.exec(cellName);
		int column = getSpreadsheetColumn(matcher);
		int row = getSpreadsheetRow(matcher);

		return new Point(column, row);
	}

	// Michael Borcherds
	public boolean isSpreadsheetLabel(String str) {
		//Matcher matcher = spreadsheetPattern.matcher(str);
		 MatchResult matcher = spreadsheetPattern.exec(str);
			//if (matcher.matches()) {
				if (matcher != null) {
			return true;
		}
		return false;
	}

	public static int getSpreadsheetColumn(MatchResult matcher) {
		//if (!matcher.matches())
		//	return -1;
		if (matcher == null)
			return -1;

		String s = matcher.getGroup(1);
		int column = 0;
		while (s.length() > 0) {
			column *= 26;
			column += s.charAt(0) - 'A' + 1;
			s = s.substring(1);
		}
		// Application.debug(column);
		return column - 1;
	}

	// Cong Liu
	public static int getSpreadsheetRow(MatchResult matcher) {
		if (matcher == null)
			return -1;
		//String s = matcher.group(2);
		String s = matcher.getGroup(2);
		return Integer.parseInt(s) - 1;
	}

	/**
	 * Returns a point with the spreadsheet coordinates of the given inputLabel.
	 * Note that this can also be used for names that include $ signs like
	 * "$A1".
	 * 
	 * @param inputLabel
	 *            label of spredsheet cell
	 * @return null for non-spreadsheet names
	 */
	public static Point getSpreadsheetCoordsForLabel(String inputLabel) {
		// we need to also support wrapped GeoElements like
		// $A4 that are implemented as dependent geos (using ExpressionNode)
		Point p = spreadsheetIndices(inputLabel);
		if (p.x >= 0 && p.y >= 0) {
			return p;
		}
		return null;
	}

	private static StringBuilder sb;

	/*
	 * used to set a cell to another geo used by FillCells[] etc
	 */
	
	public void setSpreadsheetCell(AbstractApplication app, int row, int col,
			GeoElement cellGeo) {
		String cellName = dogetSpreadsheetCellName(col, row);

		if (sb == null)
			sb = new StringBuilder();
		else
			sb.setLength(0);

		sb.append(cellName);
		if (cellGeo.isGeoFunction())
			sb.append("(x)");

		// getLabel() returns algoParent.getCommandDescription() or
		// toValueString()
		// if there's no label (eg {1,2})
		String label = cellGeo.getLabel();

		// need an = for B3=B4
		// need a : for B2:x^2 + y^2 = 2
		if (label.indexOf('=') == -1)
			sb.append('=');
		else
			sb.append(':');

		sb.append(label);

		// we only sometimes need (x), eg
		// B2(x)=f(x)
		// B2(x)=x^2
		if (cellGeo.isGeoFunction() && cellGeo.isLabelSet())
			sb.append("(x)");

		// Application.debug(sb.toString());

		app.getKernel().getAlgebraProcessor()
				.processAlgebraCommand(sb.toString(), false);

		GeoElement cell = app.getKernel().lookupLabel(cellName);
		if (cell != null) {
			(cell).setVisualStyle(cellGeo);
			(cell).setAuxiliaryObject(true);
		}

	}

	
	public Point dospreadsheetIndices(String labelPrefix) {
		return spreadsheetIndices(labelPrefix);
	}

	
	public String dogetSpreadsheetCellName(int i, int row) {
		return getSpreadsheetCellName(i, row);
	}

	
	public boolean doisSpreadsheetLabel(String label2) {
		// TODO Auto-generated method stub
		return isSpreadsheetLabel(label2);
	}

	
	public String dogetSpreadsheetColumnName(int x) {
		return getSpreadsheetColumnName(x);
	}

	
	public String dogetSpreadsheetColumnName(String s) {
		return getSpreadsheetColumnName(s);
	}

	
	public Point dogetSpreadsheetCoordsForLabel(String label) {
		return getSpreadsheetCoordsForLabel(label);
	}

	
	public GeoElement autoCreate(String label, Construction cons) {
		MatchResult cellNameMatcher = spreadsheetPattern
				.exec(label);
		if (cellNameMatcher != null) {
			String col = cellNameMatcher.getGroup(1);
			int row = Integer.parseInt(cellNameMatcher.getGroup(2));

			// try to get neighbouring cell for object type look above
			GeoElement neighbourCell = cons.geoTableVarLookup(col + (row - 1));
			if (neighbourCell == null) // look below
				neighbourCell = cons.geoTableVarLookup(col + (row + 1));

			label = col + row;
			return cons.createSpreadsheetGeoElement(neighbourCell, label);
		}
		return null;
	}
}
