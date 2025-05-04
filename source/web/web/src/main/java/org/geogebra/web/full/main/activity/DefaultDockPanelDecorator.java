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
