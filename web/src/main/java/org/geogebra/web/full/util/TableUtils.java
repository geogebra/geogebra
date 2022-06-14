package org.geogebra.web.full.util;

import com.google.gwt.user.cellview.client.CellTable;

/**
 * Method collection to manipulate CellTables
 * 
 * @author laszlo
 *
 */
public class TableUtils {
	/**
	 * Remove all columns from table.
	 * 
	 * @param table
	 *            to clear.
	 */
	public static void clear(CellTable<?> table) {
		int colCount = table.getColumnCount();
		for (int i = 0; i < colCount; i++) {
			table.removeColumn(0);
		}
	}

}
