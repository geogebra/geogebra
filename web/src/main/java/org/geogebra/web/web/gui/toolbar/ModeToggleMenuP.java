package org.geogebra.web.web.gui.toolbar;

import java.util.Vector;

import org.geogebra.common.kernel.ModeSetter;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.gui.tooltip.ToolTipManagerW;
import org.geogebra.web.html5.gui.util.CancelEventTimer;
import org.geogebra.web.html5.gui.util.ListItem;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.dom.client.HumanInputEvent;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

public class ModeToggleMenuP extends ModeToggleMenu {

	FlowPanel submenuPanel;
	Label back;

	private int startPosition;
	private int endPosition;


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
	protected void buildButton() {
		super.buildButton();
		tbutton.getElement().setAttribute("isMobile", "true");
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
			// Log.debug("hide submenu");
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
	}

	@Override
	public void onTouchStart(TouchStartEvent event) {
		if (toolbar.isVisible()) {
			startPosition = toolbar.getAbsoluteLeft();
			// Log.debug("onTouchStart toolbar: " + toolbar.getAbsoluteLeft());
		} else {
			startPosition = submenuPanel.getAbsoluteLeft();
			// Log.debug("onTouchStart submenuPanel: " +
			// submenuPanel.getAbsoluteLeft());
		}
	}

	@Override
	public void onTouchEnd(TouchEndEvent event) {
		if (toolbar.isVisible()) {
			endPosition = toolbar.getAbsoluteLeft();
			// Log.debug("onTouchEnd toolbar: " + toolbar.getAbsoluteLeft());
		} else {
			endPosition = submenuPanel.getAbsoluteLeft();
			// Log.debug("onTouchEnd submenuPanel: " +
			// submenuPanel.getAbsoluteLeft());
		}

		onEnd(event);
		CancelEventTimer.touchEventOccured();
	}

	public void onEnd(DomEvent<?> event) {

		int mode = Integer.parseInt(event.getRelativeElement().getAttribute("mode"));
		if (mode < 999 || mode > 2000) {
			app.hideKeyboard();
		}
		tbutton.getElement().focus();
		event.stopPropagation();

		// make sure it is a click, not a scroll
		if (startPosition == endPosition) {
		// if menu item was tapped
			if (event.getSource() == tbutton) {
				showMenu(); // open submenu
			} else { // click ended on submenu item
				hideMenu();
				event.stopPropagation();
			}


		ToolTipManagerW.sharedInstance().setBlockToolTip(false);
		// if we click the toolbar button, only interpret it as real click if
		// there is only one tool in this menu
		app.setMode(mode, event.getSource() == tbutton && menu.size() > 1 ? ModeSetter.DOCK_PANEL : ModeSetter.TOOLBAR);
		ToolTipManagerW.sharedInstance().setBlockToolTip(true);
		}
		tbutton.getElement().focus();
	}
}
