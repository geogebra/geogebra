package org.geogebra.web.full.gui.layout;

import org.geogebra.web.full.gui.view.algebra.AlgebraViewW;
import org.geogebra.web.full.util.StickyTable;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Adds UI elements to view panels
 */
public interface DockPanelDecorator {

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
	Panel decorate(ScrollPanel algebrap, AppW app);

	void onResize(AlgebraViewW aview, int offsetHeight);

	/**
	 * Put additional stuff here for TableTab.
	 *
	 * @param tab to decorate.
	 * @param table to decorate.
	 * @param app application
	 */
	void decorateTableTab(Widget tab, StickyTable<?> table, AppW app);
}
