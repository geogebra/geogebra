package org.geogebra.web.full.gui.toolbar.mow;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.gui.AccessibilityGroup;
import org.geogebra.common.gui.toolbar.ToolBar;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.main.AppW;

/**
 * Submenu for media (i.e. photo, video, ...)
 */
public class MediaSubMenu extends SubMenuPanel {
	/**
	 * @param app
	 *            application
	 */
	public MediaSubMenu(AppW app) {
		super(app);
	}

	@Override
	protected void createContentPanel() {
		super.createContentPanel();
		boolean graspableMath = Browser.isGraspableMathEnabled();
		boolean h5p = app.getVendorSettings().isH5PEnabled()
				&& app.getLoginOperation().isLoggedIn();
		super.createPanelRow(ToolBar.getNotesMediaToolBar(graspableMath, h5p));
		makeButtonsAccessible(AccessibilityGroup.NOTES_TOOL_MEDIA);
	}

	@Override
	public int getFirstMode() {
		return EuclidianConstants.MODE_MEDIA_TEXT;
	}
}
