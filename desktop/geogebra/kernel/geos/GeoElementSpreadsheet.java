package geogebra.kernel.geos;

import java.awt.Point;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GeoElementSpreadsheet {
	/*
	 * match A1, ABG1, A123
	 * but not A0, A000, A0001 etc
	 */
	public static final Pattern spreadsheetPattern =
		Pattern.compile("\\$?([A-Z]+)\\$?([1-9][0-9]*)");
	
	public static String getSpreadsheetColumnName(int i) {
        ++ i;
        String col = "";
        while (i > 0) {
              col = (char)('A' + (i-1) % 26)  + col;
              i = (i-1)/ 26;
        }
        return col;
    }

	public static String getSpreadsheetColumnName(String label) {
		Matcher matcher = spreadsheetPattern.matcher(label);
		if (! matcher.matches()) return null;
		return matcher.group(1);
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
		public static Point spreadsheetIndices(String cellName){
			
			Matcher matcher = GeoElementSpreadsheet.spreadsheetPattern.matcher(cellName);			
			int column = getSpreadsheetColumn(matcher);
			int row = getSpreadsheetRow(matcher);
			
			return new Point(column, row);
		}
		
		// Michael Borcherds
		public static boolean isSpreadsheetLabel(String str) {
			Matcher matcher = GeoElementSpreadsheet.spreadsheetPattern.matcher(str);
			if (matcher.matches()) return true;
			else return false;
		}
		
		public static int getSpreadsheetColumn(Matcher matcher) {
			if (! matcher.matches()) return -1;

			String s = matcher.group(1);
			int column = 0;
			while (s.length() > 0) {
				column *= 26;
				column += s.charAt(0) - 'A' + 1;
				s = s.substring(1);
			}
			//Application.debug(column);
			return column - 1;
		}

		// Cong Liu
		public static int getSpreadsheetRow(Matcher matcher) {
			if (! matcher.matches()) return -1;
			String s = matcher.group(2);
			return Integer.parseInt(s) - 1;
		}


}
