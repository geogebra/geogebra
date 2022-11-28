package org.geogebra.web.full.main.activity;

import org.geogebra.web.full.gui.layout.DockPanelDecorator;
import org.geogebra.web.full.gui.view.algebra.AlgebraViewW;
import org.geogebra.web.full.util.StickyTable;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

public class DefaultDockPanelDecorator implements DockPanelDecorator {
	@Override
	public Panel decorate(ScrollPanel scrollPanel, AppW app) {
		return scrollPanel;
	}

	@Override
	public void onResize(AlgebraViewW aview, int offsetHeight) {
		// nothing to do.
	}

	@Override
	public void decorateTableTab(Widget tab, StickyTable<?> table) {
		tab.getElement().getFirstChildElement().getStyle().setHeight(100, Style.Unit.PCT);
	}
}
