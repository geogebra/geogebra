package geogebra.phone.gui.view.euclidian.toolbar;

import geogebra.html5.main.AppW;
import geogebra.web.gui.app.GGWToolBar;
import geogebra.web.gui.toolbar.ModeToggleMenu;
import geogebra.web.gui.toolbar.ToolBarW;

import java.util.Vector;

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
