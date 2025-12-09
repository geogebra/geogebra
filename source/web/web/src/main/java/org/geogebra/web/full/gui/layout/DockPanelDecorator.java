/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

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

	/**
	 * Update localized texts.
	 */
	void setLabels();

	/**
	 * @return whether table of values should have shaded columns
	 */
	boolean hasShadedColumns();
}
