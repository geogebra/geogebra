/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.web.full.util;

import org.gwtproject.user.cellview.client.CellTable;

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
