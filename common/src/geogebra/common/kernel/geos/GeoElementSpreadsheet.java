package geogebra.common.kernel.geos;

import geogebra.common.awt.GPoint;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.arithmetic.FunctionalNVar;
import geogebra.common.main.App;

import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;

/**
 * Collection of methods for handling spreadsheet cell names
 *
 */
public class GeoElementSpreadsheet {
	/**
	 * match A1, ABG1, A123 but not A0, A000, A0001 etc
	 */
	public static final RegExp spreadsheetPattern = RegExp
			.compile("^\\$?([A-Z]+)\\$?([1-9][0-9]*)$");
	/**
	 * match fooA1bar, but not fooA01bar etc.
	 */
	public static final RegExp spreadsheetPatternPart = RegExp
			.compile("\\$?([A-Z]+)\\$?([1-9][0-9]*)");

	//public static final RegExp spreadsheetPatternGlobal = RegExp
	//		.compile("\\$?([A-Z]+)\\$?([1-9][0-9]*)", "g");
	/**
	 * Converts column number to name
	 * @param column column number
	 * @return column name
	 */
	public static String getSpreadsheetColumnName(int column) {
		int i = column +1;
		String col = "";
		while (i > 0) {
			col = (char) ('A' + (i - 1) % 26) + col;
			i = (i - 1) / 26;
		}
		return col;
	}

	/**
	 * Extracts column from cell name
	 * @param label cell label
	 * @return column name
	 */
	public static String getSpreadsheetColumnName(String label) {
		MatchResult matcher = spreadsheetPattern.exec(label);
		if (matcher == null)
			return null;
		return matcher.getGroup(1);
	}

	/**
	 * Converts coordinates into cell name
	 * @param column cell column
	 * @param row cell row
	 * @return cell name
	 * @author Cong Liu
	 */
	public static String getSpreadsheetCellName(int column, int row) {
		return getSpreadsheetColumnName(column) + (row+1);
	}

	/**
	 * Determines spreadsheet row and column indices for a given cell name (e.g.
	 * "B3" sets column = 1 and row = 2. If the cell name does not match a
	 * possible spreadsheet cell then both row and column are returned as -1.
	 * 
	 * @param cellName
	 *            given cell name
	 * @return coordinates of spreadsheet cell
	 */
	public static GPoint spreadsheetIndices(String cellName) {

		MatchResult matcher = spreadsheetPattern.exec(cellName);

		// return (-1,-1) if not a spreadsheet cell name
		return new GPoint(getSpreadsheetColumn(matcher),
				getSpreadsheetRow(matcher));
	}

	/**
	 * Checks whether geo has valid cell name. We use getLabel() rather than getLabelSimple() here
	 * because of labels like $A$1 
	 * @param geo geo
	 * @return true if label is valid cell name
	 * @author Michael Borcherds
	 */
	public static boolean hasSpreadsheetLabel(GeoElement geo) {
		return isSpreadsheetLabel(geo.getLabel(StringTemplate.defaultTemplate));
	}

	/**
	 * Checks whether str is valid cell name
	 * @param str label
	 * @return true if label is valid cell name
	 * @author Michael Borcherds
	 */
	public static boolean isSpreadsheetLabel(String str) {
		if (str == null)
			return false;

		MatchResult matcher = spreadsheetPattern.exec(str);		
		return matcher != null;
	}

	/**
	 * @param matcher matcher
	 * @return column matching the matcher
	 */
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
	/**
	 * Returns row number based on matcher, which was obtained using spreadsheetPattern
	 * @author Cong Liu 
	 * @param matcher matcher
	 * @return row number
	 */
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
	public static GPoint getSpreadsheetCoordsForLabel(String inputLabel) {
		// we need to also support wrapped GeoElements like
		// $A4 that are implemented as dependent geos (using ExpressionNode)
		GPoint p = spreadsheetIndices(inputLabel);
		if (p.x >= 0 && p.y >= 0) {
			return p;
		}
		return null;
	}

	private static StringBuilder sb;

	/**
	 * used to set a cell to another geo used by FillCells[] etc
	 * @param app application
	 * @param row destination row
	 * @param col destination column
	 * @param cellGeo source element
	 */
	
	public void setSpreadsheetCell(App app, int row, int col,
			GeoElement cellGeo) {
		String cellName = getSpreadsheetCellName(col, row);

		if (sb == null)
			sb = new StringBuilder();
		else
			sb.setLength(0);

		sb.append(cellName);
		if (cellGeo instanceof FunctionalNVar){
			sb.append("(");
			sb.append(((FunctionalNVar)cellGeo).getVarString(StringTemplate.defaultTemplate));
			sb.append(")");
		}
		

		// getLabel() returns algoParent.getCommandDescription() or
		// toValueString()
		// if there's no label (eg {1,2})
		String label = cellGeo.getLabel(StringTemplate.defaultTemplate);

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
		if (cellGeo instanceof FunctionalNVar && cellGeo.isLabelSet()){
			sb.append("(");
			sb.append(((FunctionalNVar)cellGeo).getVarString(StringTemplate.defaultTemplate));
			sb.append(")");
		}

		// Application.debug(sb.toString());

		app.getKernel().getAlgebraProcessor()
				.processAlgebraCommand(sb.toString(), false);

		GeoElement cell = app.getKernel().lookupLabel(cellName);
		if (cell != null) {
			(cell).setVisualStyle(cellGeo);
			(cell).setAuxiliaryObject(true);
		}

	}
	
	/**
	 * @param label label
	 * @param cons construction
	 * @return created geo or null
	 */
	public static GeoElement autoCreate(String label, Construction cons) {
		MatchResult cellNameMatcher = spreadsheetPattern
				.exec(label);
		if (cellNameMatcher != null) {
			String col = cellNameMatcher.getGroup(1);
			int row = Integer.parseInt(cellNameMatcher.getGroup(2));

			// try to get neighbouring cell for object type look above
			GeoElement neighbourCell = cons.geoTableVarLookup(col + (row - 1));
			if (neighbourCell == null) // look below
				neighbourCell = cons.geoTableVarLookup(col + (row + 1));

			String label1 = col + row;
			return cons.createSpreadsheetGeoElement(neighbourCell, label1);
		}
		return null;
	}
}
