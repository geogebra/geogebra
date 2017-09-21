package org.geogebra.web.web.gui.toolbar.mow;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.gui.toolbar.ToolBar;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.event.dom.client.ClickEvent;

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
		super.createPanelRow(ToolBar.getMOWMediaToolBarDefString());
	}

	@Override
	public int getFirstMode() {
		return EuclidianConstants.MODE_TEXT;
	}

	public void onClick(ClickEvent event) {
		// do nothing here
	}
}
