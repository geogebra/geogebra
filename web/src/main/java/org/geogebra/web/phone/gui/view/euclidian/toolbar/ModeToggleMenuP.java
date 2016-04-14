package org.geogebra.web.phone.gui.view.euclidian.toolbar;

import java.util.Vector;

import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.gui.toolbar.ModeToggleMenu;
import org.geogebra.web.web.gui.toolbar.ToolBarW;
import org.geogebra.web.web.gui.toolbar.ToolbarSubmenuW;

public class ModeToggleMenuP extends ModeToggleMenu {

	public ModeToggleMenuP(AppW appl, Vector<Integer> menu1, ToolBarW tb,
			int order) {
		super(appl, menu1, tb, order);
	}
	
	@Override
	protected ToolbarSubmenuW createToolbarSubmenu(AppW app, int order) {
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
