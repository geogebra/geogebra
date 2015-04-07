package org.geogebra.web.phone.gui.view.euclidian.toolbar;

import java.util.Vector;

import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.gui.app.GGWToolBar;
import org.geogebra.web.web.gui.toolbar.ModeToggleMenu;
import org.geogebra.web.web.gui.toolbar.ToolBarW;

public class ToolBarP extends ToolBarW {

	public ToolBarP(GGWToolBar tb) {
		super(tb);
	}

	@Override
	public void buildGui() {
		super.buildGui();

		for (ModeToggleMenu toggleMenu : getModeToggleMenus()) {
			toggleMenu.setStyleName("phoneToolButton");
		}
		menuList.removeStyleName("toolbar_mainItem");
		menuList.addStyleName("phonetoolbar_mainItem");
	}

	@Override
	protected ModeToggleMenu createModeToggleMenu(AppW app,
			Vector<Integer> menu, int order) {
		return new ModeToggleMenuP(app, menu, this, order);
	}
}
