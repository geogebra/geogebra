package org.geogebra.web.html5.gui;

import org.geogebra.common.kernel.ModeSetter;
import org.gwtproject.user.client.ui.HasVisibility;

/**
 * Classic toolbar or its component.
 */
public interface ToolBarInterface extends HasVisibility {

	/**
	 * Notify when app mode changes.
	 * @param mode app mode
	 * @param m mode setter
	 * @return new mode
	 */
	int setMode(int mode, ModeSetter m);

	/**
	 * Close all submenus
	 */
	void closeAllSubmenu();

	/**
	 * @return whether this has mobile layout
	 */
	boolean isMobileToolbar();

}
