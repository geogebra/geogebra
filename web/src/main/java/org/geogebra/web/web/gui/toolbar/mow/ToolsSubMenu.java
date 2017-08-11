package org.geogebra.web.web.gui.toolbar.mow;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.gui.toolbar.ToolBar;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.event.dom.client.ClickEvent;

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
		super(app/* , true */);
		addStyleName("toolsSubMenu");
		
	}

	@Override
	protected void createContentPanel() {
		super.createContentPanel();
		super.createPanelRow(ToolBar.getMOWToolsDefString());
	}

	@Override
	public int getFirstMode() {
		return EuclidianConstants.MODE_SHAPE_RECTANGLE;
	}

	public void onClick(ClickEvent event) {
		// TODO Auto-generated method stub

	}
}
