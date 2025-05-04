package org.geogebra.web.full.gui.layout;

import org.geogebra.web.full.gui.view.algebra.AlgebraViewW;
import org.geogebra.web.full.util.StickyTable;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.user.client.ui.Panel;
import org.gwtproject.user.client.ui.Widget;

/**
 * Adds UI elements to view panels
 */
public interface DockPanelDecorator {

	/**
	 * Wraps the view panel in another panel together with additional UI (e.g.
	 * algebra header)
	 * @param algebraTab the algebra tab itself.
	 * @param algebraPanel algebra scroll panel
	 * @param app application
	 * @return wrapped panel
	 */
	Panel decorate(Widget algebraTab, Panel algebraPanel, AppW app);

	/**
	 * Update decoration after resize
	 * @param algebraView algebra view
	 * @param offsetHeight new height
	 */
	void onResize(AlgebraViewW algebraView, int offsetHeight);

	/**
	 * Put additional stuff here for TableTab.
	 *
	 * @param tab to decorate.
	 * @param table to decorate.
	 */
	void decorateTableTab(Widget tab, StickyTable<?> table);

	/**
	 *
	 * @param tabHeight original tab height.
	 * @return modified tab height
	 */
	int getTabHeight(int tabHeight);

	/**
	 * Resizes the table on normal screen.
	 *
	 * @param tabHeight the tab height where the table is on.
	 */
	void resizeTable(int tabHeight, StickyTable<?> table);

	/**
	 * Resizes the table on small screen.

	 * @param tabHeight the tab height where the table is on.
	 */
	void resizeTableSmallScreen(int tabHeight, StickyTable<?> table);

	void setLabels();

	boolean hasShadedColumns();
}
