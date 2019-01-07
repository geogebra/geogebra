package org.geogebra.web.full.gui.layout;

import org.geogebra.web.html5.main.AppW;

import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.ScrollPanel;

/**
 * Adds UI elements to view panels
 */
public interface DockPanelDecorator extends RequiresResize {

	/**
	 * Wraps the view panel in another panel together with additional UI (e.g.
	 * algebra header)
	 * 
	 * @param algebrap
	 *            algebra scroll panel
	 * @param app
	 *            application
	 * @return wrapped panel
	 */
	public Panel decorate(ScrollPanel algebrap, AppW app);
}
