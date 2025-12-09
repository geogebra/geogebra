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

package org.geogebra.desktop.euclidian;

import java.awt.Cursor;

import org.geogebra.common.util.MouseCursor;

public class CursorMap {

	/**
	 * @param cursor cursor type
	 * @return AWT cursor
	 */
	public static Cursor get(MouseCursor cursor) {
		switch (cursor) {
		case RESIZE_X:
			return Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR);
		case RESIZE_Y:
			return Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR);
		default:
			return Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
		}
	}
}
