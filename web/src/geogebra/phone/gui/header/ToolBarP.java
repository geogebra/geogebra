package geogebra.phone.gui.header;

import geogebra.web.gui.app.GGWToolBar;
import geogebra.web.gui.toolbar.ModeToggleMenu;
import geogebra.web.gui.toolbar.ToolBarW;

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
	}
}
