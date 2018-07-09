package org.geogebra.web.full.gui.toolbar.mow;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.gui.toolbar.ToolBar;
import org.geogebra.common.main.Feature;
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
		super(app/* , true */);
		addStyleName("mediaSubMenu");
	}

	@Override
	protected void createContentPanel() {
		super.createContentPanel();
		super.createPanelRow(ToolBar
				.getMOWMediaToolBarDefString(app.has(Feature.EMBED_EXTENSION)));
	}

	@Override
	public int getFirstMode() {
		return EuclidianConstants.MODE_TEXT;
	}
}
