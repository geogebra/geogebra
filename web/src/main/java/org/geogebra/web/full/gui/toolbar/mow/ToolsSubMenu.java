package org.geogebra.web.full.gui.toolbar.mow;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.gui.AccessibilityGroup;
import org.geogebra.common.gui.toolbar.ToolBar;
import org.geogebra.web.html5.main.AppW;

/**
 * Tools submenu for MOWToolbar.
 * 
 * @author Laszlo Gal
 * 
 */
public class ToolsSubMenu extends SubMenuPanel {
	/**
	 * 
	 * @param app
	 *            ggb app.
	 */
	public ToolsSubMenu(AppW app) {
		super(app);
	}

	@Override
	protected void createContentPanel() {
		super.createContentPanel();
		super.createPanelRow(ToolBar.getMOWToolsDefString());
		makeButtonsAccessible(AccessibilityGroup.NOTES_TOOL_TOOLS);
	}

	@Override
	public int getFirstMode() {
		return EuclidianConstants.MODE_SHAPE_RECTANGLE;
	}

	@Override
	public boolean isValidMode(int mode) {
		return EuclidianConstants.isShapeMode(mode);
	}
}
