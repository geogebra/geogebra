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

package org.geogebra.common.gui;

import java.util.Vector;

import org.geogebra.common.gui.toolbar.ToolBar;
import org.geogebra.common.gui.toolbar.ToolbarItem;

public class CustomizeToolbarModel {

	/**
	 * @param toolbarDefinition
	 *            toolbar definition string (see EuclidianConstants)
	 * @return vector of menus (vectors of ints) and separators (ints)
	 * 
	 */
	public static Vector<Integer> generateToolsVector(
			String toolbarDefinition) {
		Vector<Integer> vector = new Vector<>();
		// separator
		vector.add(ToolBar.SEPARATOR);

		// get default toolbar as nested vectors
		Vector<ToolbarItem> defTools;
		try {
			defTools = ToolBar.parseToolbarString(toolbarDefinition);
		} catch (Exception e) {
			return new Vector<>();
		}
		for (ToolbarItem element : defTools) {
			if (element.getMenu() != null) {
				Vector<Integer> menu = element.getMenu();
				for (Integer modeInt : menu) {
					if (modeInt != -1) {
						vector.add(modeInt);
					}
				}
			} else {
				Integer modeInt = element.getMode();
				if (modeInt != -1) {
					vector.add(modeInt);
				}
			}
		}
		return vector;
	}

}
