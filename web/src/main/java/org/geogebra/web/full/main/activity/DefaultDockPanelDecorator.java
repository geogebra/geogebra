package org.geogebra.web.full.main.activity;

import org.geogebra.web.full.gui.layout.DockPanelDecorator;
import org.geogebra.web.full.gui.view.algebra.AlgebraViewW;
import org.geogebra.web.full.util.StickyTable;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

public class DefaultDockPanelDecorator implements DockPanelDecorator {
	@Override
	public Panel decorate(Panel panel, AppW app) {
		return panel;
	}

	@Override
	public void onResize(AlgebraViewW aview, int offsetHeight) {
		// nothing to do.
	}

	@Override
	public void decorateTableTab(Widget tab, StickyTable<?> table) {
		tab.getElement().getFirstChildElement().getStyle().setHeight(100, Style.Unit.PCT);
	}

	@Override
	public int getTabHeight(int tabHeight) {
		return 0;
	}

	@Override
	public void resizeTable(StickyTable<?> table, int tabHeight) {
		table.setHeight(tabHeight);
	}

	@Override
	public void resizeTableSmallScreen(StickyTable<?> table, int tabHeight) {
		resizeTable(table, tabHeight);
	}
}
