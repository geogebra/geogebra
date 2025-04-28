package org.geogebra.web.html5.main.topbar;

import org.geogebra.web.html5.gui.view.IconSpec;

/**
 * Provides icon definitions for top bar.
 */
public interface TopBarIconProvider {

	/**
	 * @param icon icon kind
	 * @return icon definition
	 */
	IconSpec matchIconWithResource(TopBarIcon icon);
}
