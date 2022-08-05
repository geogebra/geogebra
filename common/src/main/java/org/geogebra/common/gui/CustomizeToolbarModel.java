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
