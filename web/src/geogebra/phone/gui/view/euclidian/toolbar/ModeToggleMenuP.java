package geogebra.phone.gui.view.euclidian.toolbar;

import geogebra.html5.main.AppW;
import geogebra.web.gui.toolbar.ModeToggleMenu;
import geogebra.web.gui.toolbar.ToolBarW;
import geogebra.web.gui.toolbar.ToolbarSubemuW;

import java.util.Vector;

public class ModeToggleMenuP extends ModeToggleMenu {

	public ModeToggleMenuP(AppW appl, Vector<Integer> menu1, ToolBarW tb,
			int order) {
		super(appl, menu1, tb, order);
	}
	
	@Override
	protected ToolbarSubemuW createToolbarSubmenu(AppW app, int order) {
		return new ToolbarSubmenuP(app, order);
	}

	@Override
	public void showMenu() {
		toolbar.addStyleDependentName("opened");
		super.showMenu();
		remove(submenu);
		toolbar.add(submenu);
	}

	@Override
	public void hideMenu() {
		toolbar.removeStyleDependentName("opened");
		super.hideMenu();
	}
}
