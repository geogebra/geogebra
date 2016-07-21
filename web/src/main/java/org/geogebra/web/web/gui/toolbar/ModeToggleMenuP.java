package org.geogebra.web.web.gui.toolbar;

import java.util.Vector;

import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.user.client.ui.FlowPanel;

public class ModeToggleMenuP extends ModeToggleMenu {

	FlowPanel submenuPanel;

	public ModeToggleMenuP(AppW appl, Vector<Integer> menu1, ToolBarW tb,
			int order) {
		super(appl, menu1, tb, order);
	}

	public ModeToggleMenuP(AppW appl, Vector<Integer> menu1, ToolBarW tb, int order, FlowPanel submenuPanel) {
		super(appl, menu1, tb, order);
		this.submenuPanel = submenuPanel;
	}
	
	@Override
	protected ToolbarSubmenuW createToolbarSubmenu(AppW app, int order) {
		Log.debug("submenuP");
		return new ToolbarSubmenuP(app, order);
	}


	@Override
	public void showMenu() {
		// App.debug("show menu");
		// toolbar.addStyleDependentName("opened");

		// super.showMenu();
		// remove(submenu);
		if (this.submenu == null) {
			this.buildGui();
		}
		if (submenu != null) {
			submenuPanel.add(submenu);
			submenu.setVisible(true);

		}
		toolbar.getGGWToolBar().setSubmenuWith();
	}

	@Override
	public void hideMenu() {
		// toolbar.removeStyleDependentName("opened");
		// super.hideMenu();
		if (submenu != null && submenu.isAttached()) {
			submenuPanel.remove(submenu);
			submenu.setVisible(false);
			Log.debug("hide menu");
		}
	}

	public int getButtonCount() {
		int count = submenu.getItemList().getWidgetCount();
		Log.debug("buttoncount: " + count);
		return count;
	}
}
