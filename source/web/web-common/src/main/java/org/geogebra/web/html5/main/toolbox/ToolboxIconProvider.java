package org.geogebra.web.html5.main.toolbox;

import org.geogebra.web.html5.gui.view.IconSpec;

/**
 * Provides icon definitions for toolbar.
 */
public interface ToolboxIconProvider {

	/**
	 * @param icon icon kind
	 * @return icon definition
	 */
	IconSpec matchIconWithResource(ToolboxIcon icon);
}
