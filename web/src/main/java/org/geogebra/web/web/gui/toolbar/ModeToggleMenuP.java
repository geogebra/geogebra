package org.geogebra.web.web.gui.toolbar;

import java.util.Vector;

import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.event.dom.client.HumanInputEvent;
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
		return new ToolbarSubmenuP(app, order);
	}


	@Override
	public void showMenu() {
		// remove toolbar before showing submenu
		toolbar.setVisible(false);

		if (this.submenu == null) {
			this.buildGui();
		}
		if (submenu != null) {
			submenuPanel.add(submenu);
			submenu.setVisible(true);
		}
		toolbar.getGGWToolBar().setSubmenuDimensions();
	}

	@Override
	public void hideMenu() {

		if (submenu != null/* && submenu.isAttached() */) {
			Log.debug("hide submenu");
			submenuPanel.remove(submenu);
			submenu.setVisible(false);
		}
		toolbar.setVisible(true);
	}

	public int getButtonCount() {
		int count = submenu.getItemList().getWidgetCount();
		return count;
	}

	@Override
	public void onStart(HumanInputEvent<?> event) {

		event.preventDefault();
		event.stopPropagation();
		this.setFocus(true);
		if (menu.size() == 1) {
			showTooltipFor(event);

		}
	}
}
