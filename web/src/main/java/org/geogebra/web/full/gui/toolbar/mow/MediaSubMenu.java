package org.geogebra.web.full.gui.toolbar.mow;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.gui.AccessibilityGroup;
import org.geogebra.common.gui.toolbar.ToolBar;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.main.AppW;

/**
 * Submenu for media (i.e. photo, video, ...)
 * 
 * @author Alicia Hofstaetter
 *
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
		boolean isEnabled = Browser.isGraspableMathEnabled();
		super.createPanelRow(ToolBar.getMOWMediaToolBarDefString(isEnabled));
		makeButtonsAccessible(AccessibilityGroup.NOTES_TOOL_MEDIA);
	}

	@Override
	public int getFirstMode() {
		return EuclidianConstants.MODE_MEDIA_TEXT;
	}

	@Override
	public boolean isValidMode(int mode) {
		return mode == EuclidianConstants.MODE_MEDIA_TEXT
				|| mode == EuclidianConstants.MODE_IMAGE
				|| mode == EuclidianConstants.MODE_CAMERA
				|| mode == EuclidianConstants.MODE_VIDEO
				|| mode == EuclidianConstants.MODE_AUDIO
				|| mode == EuclidianConstants.MODE_GRAPHING
				|| mode == EuclidianConstants.MODE_PDF
				|| mode == EuclidianConstants.MODE_EXTENSION
				|| mode == EuclidianConstants.MODE_TABLE
				|| mode == EuclidianConstants.MODE_EQUATION
				|| mode == EuclidianConstants.MODE_GRASPABLE_MATH
				|| mode == EuclidianConstants.MODE_CAS;
	}
}
