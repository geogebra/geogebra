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

package org.geogebra.web.full.main.activity;

import org.geogebra.web.full.gui.layout.DockPanelDecorator;
import org.geogebra.web.full.gui.view.algebra.AlgebraViewW;
import org.geogebra.web.full.util.StickyTable;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.dom.client.Style;
import org.gwtproject.user.client.ui.Panel;
import org.gwtproject.user.client.ui.Widget;

public class DefaultDockPanelDecorator implements DockPanelDecorator {

	@Override
	public Panel decorate(Widget algebraTab, Panel algebraPanel, AppW app) {
		return algebraPanel;
	}

	@Override
	public void onResize(AlgebraViewW algebraView, int offsetHeight) {
		// nothing to do.
	}

	@Override
	public void decorateTableTab(Widget tab, StickyTable<?> table) {
		tab.getElement().getFirstChildElement().getStyle().setHeight(100, Style.Unit.PCT);
	}

	@Override
	public int getTabHeight(int tabHeight) {
		return tabHeight;
	}

	@Override
	public void resizeTable(int tabHeight, StickyTable<?> table) {
		if (table != null) {
			table.setHeight(tabHeight);
		}
	}

	@Override
	public void resizeTableSmallScreen(int tabHeight, StickyTable<?> table) {
		resizeTable(tabHeight, table);
	}

	@Override
	public void setLabels() {
		// nothing to do here
	}

	@Override
	public boolean hasShadedColumns() {
		return true;
	}

}
