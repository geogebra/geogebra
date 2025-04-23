package org.geogebra.web.html5.gui;

import org.geogebra.common.kernel.ModeSetter;

/**
 * Classic toolbar or its component.
 */
public interface ToolBarInterface {

	int setMode(int mode, ModeSetter m);

	void setVisible(boolean show);

	void closeAllSubmenu();

	boolean isMobileToolbar();

	boolean isShown();

}
