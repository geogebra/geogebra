package org.geogebra.web.full.html5;

import org.geogebra.web.full.javax.swing.GPopupMenuW;
import org.geogebra.web.html5.gui.BaseWidgetFactory;
import org.geogebra.web.html5.main.AppW;

public class MenuWidgetFactory extends BaseWidgetFactory {
	public MenuWidgetFactory(AppW app) {
		super();
	}

	@Override
	public GPopupMenuW newPopupMenu(AppW app) {
		return new GPopupMenuWMock(app);
	}
}
