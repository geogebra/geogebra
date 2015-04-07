package org.geogebra.common.gui;

import java.util.Vector;

import org.geogebra.common.gui.toolbar.ToolBar;
import org.geogebra.common.gui.toolbar.ToolbarItem;

public class CustomizeToolbarModel {

	public CustomizeToolbarModel() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param toolbarDefinition
	 *            toolbar definition string (see EuclidianConstants)
	 * @return vector of menus (vectors of ints) and separators (ints)
	 * 
	 */
	public static Vector<Integer> generateToolsVector(String toolbarDefinition) {
		Vector<Integer> vector = new Vector<Integer>();
		// separator
		vector.add(ToolBar.SEPARATOR);

		// get default toolbar as nested vectors
		Vector<ToolbarItem> defTools = null;
		try {
			defTools = ToolBar.parseToolbarString(toolbarDefinition);
		} catch (Exception e) {
			return new Vector<Integer>();
		}
		for (int i = 0; i < defTools.size(); i++) {
			ToolbarItem element = defTools.get(i);

			if (element.getMenu() != null) {
				Vector<Integer> menu = element.getMenu();
				for (int j = 0; j < menu.size(); j++) {
					Integer modeInt = menu.get(j);
					int mode = modeInt.intValue();
					if (mode != -1)
						vector.add(modeInt);
				}
			} else {
				Integer modeInt = element.getMode();
				int mode = modeInt.intValue();
				if (mode != -1)
					vector.add(modeInt);
			}
		}
		return vector;
	}

}
