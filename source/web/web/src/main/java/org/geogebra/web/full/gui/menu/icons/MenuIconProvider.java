package org.geogebra.web.full.gui.menu.icons;

import org.geogebra.common.gui.menu.Icon;
import org.geogebra.web.html5.gui.view.IconSpec;

/**
 * Provides icon definitions for the menu.
 */
public interface MenuIconProvider {

	/**
	 * @param icon icon kind
	 * @return icon definition
	 */
	IconSpec matchIconWithResource(Icon icon);
}
