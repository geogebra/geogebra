package org.geogebra.web.web.gui.toolbar;

import java.util.Vector;

import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.gui.util.ListItem;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HumanInputEvent;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

public class ModeToggleMenuP extends ModeToggleMenu {

	FlowPanel submenuPanel;
	Label back;

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
		Log.debug("create toolbar submenu P");
		return new ToolbarSubmenuP(app, order);
	}

	@Override
	protected void buildGui() {
		submenu = createToolbarSubmenu(app, order);

		back = new Label("<");
		back.addStyleName("submenuBack");
		back.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				hideMenu();
			}
		});
		submenu.add(back);

		for (int k = 0; k < menu.size(); k++) {
			final int addMode = menu.get(k).intValue();
			if (addMode < 0) { // TODO
				// // separator within menu:
				// tm.addSeparator();
			} else { // standard case: add mode
				// check mode
				if (!"".equals(app.getToolName(addMode))) {
					ListItem subLi = submenu.addItem(addMode);
					addDomHandlers(subLi);
				}
			}
		}
	}

	@Override
	public void showMenu() {
		Log.debug("show menu phone");
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
