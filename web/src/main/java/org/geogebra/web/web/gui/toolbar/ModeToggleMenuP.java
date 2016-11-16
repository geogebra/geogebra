package org.geogebra.web.web.gui.toolbar;

import java.util.Vector;

import org.geogebra.common.kernel.ModeSetter;
import org.geogebra.web.html5.gui.FastClickHandler;
import org.geogebra.web.html5.gui.tooltip.ToolTipManagerW;
import org.geogebra.web.html5.gui.util.CancelEventTimer;
import org.geogebra.web.html5.gui.util.ListItem;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.gui.ImageFactory;
import org.geogebra.web.web.gui.images.PerspectiveResources;
import org.geogebra.web.web.gui.util.StandardButton;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.dom.client.HumanInputEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

public class ModeToggleMenuP extends ModeToggleMenu implements MouseMoveHandler {

	FlowPanel submenuPanel;
	StandardButton back;

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
		return new ToolbarSubmenuP(app, order);
	}

	@Override
	public void addDomHandlers(Widget w) {
		super.addDomHandlers(w);
		 w.addDomHandler(this, MouseMoveEvent.getType());
	}

	@Override
	protected void buildButton() {
		super.buildButton();
		tbutton.getElement().setAttribute("isMobile", "true");
	}

	private void addBackButton() {
		PerspectiveResources pr = ((ImageFactory) GWT.create(ImageFactory.class)).getPerspectiveResources();
		back = new StandardButton(pr.menu_header_back(), null, 32);
		back.addStyleName("submenuBack");
		back.addFastClickHandler(new FastClickHandler() {

			@Override
			public void onClick(Widget source) {
				hideMenu();
			}
		});
		// submenuPanel.submenuScrollPanel.toolbarPanel.add
		((FlowPanel) submenuPanel.getParent().getParent()).add(back);
	}

	public void removeBackButton() {
		if (back != null) {
			((FlowPanel) submenuPanel.getParent().getParent()).remove(back);
		}
	}

	@Override
	protected void buildGui() {
		submenu = createToolbarSubmenu(app, order);

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
		// hide toolbar before showing submenu
		toolbar.setVisible(false);
		toolbar.getParent().setVisible(false);

		if (this.submenu == null) {
			this.buildGui();
		}
		if (submenu != null) {
			submenuPanel.add(submenu);
			submenuPanel.getParent().setVisible(true);
			submenu.setVisible(true);
			addBackButton();
		}
		toolbar.getGGWToolBar().setSubmenuDimensions(app.getWidth());
	}

	@Override
	public void hideMenu() {

		if (submenu != null) {

			submenuPanel.remove(submenu);
			submenuPanel.getParent().setVisible(false);
			submenu.setVisible(false);
		}
		removeBackButton();
		toolbar.getParent().setVisible(true);
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
		} else {
			startPosition = submenuPanel.getAbsoluteLeft();
		}
		if (event.getSource() == tbutton) {
			tbutton.addStyleName("touched");
		}

		if (toolbar.isVisible()) {
			startPosition = toolbar.getAbsoluteLeft();
		} else {
			startPosition = submenuPanel.getAbsoluteLeft();
		}
		if (event.getSource() == tbutton) {
			tbutton.addStyleName("touched");
		}
	}

	@Override
	public void onTouchEnd(TouchEndEvent event) {
		if (toolbar.isVisible()) {
			endPosition = toolbar.getAbsoluteLeft();
		} else {
			endPosition = submenuPanel.getAbsoluteLeft();
		}
		if (event.getSource() == tbutton) {
			tbutton.removeStyleName("touched");
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
			// if tool button was tapped && if there is a submenu
			if (event.getSource() == tbutton && menu.size() > 1) {
				showMenu(); // open submenu
			} else { // click ended on submenu item
				hideMenu();
				event.stopPropagation();
			}


			ToolTipManagerW.sharedInstance().setBlockToolTip(false);
			// if we click the toolbar button, only interpret it as real click
			// if there is only one tool in this menu
			app.setMode(mode,
					event.getSource() == tbutton && menu.size() > 1 ? ModeSetter.DOCK_PANEL : ModeSetter.TOOLBAR);
			ToolTipManagerW.sharedInstance().setBlockToolTip(true);
		}
		tbutton.getElement().focus();
	}

	@Override
	public void onMouseDown(MouseDownEvent event) {
		event.preventDefault();
		startPosition = event.getClientX();
		if (toolbar.isVisible()) {
			toolbar.setStartPositions(startPosition,
				((ScrollPanel) toolbar.getParent()).getHorizontalScrollPosition());
		} else {
			toolbar.setStartPositions(startPosition,
					((ScrollPanel) submenuPanel.getParent()).getHorizontalScrollPosition());
		}
		toolbar.setMouseDown(true);
	}

	@Override
	public void onMouseUp(MouseUpEvent event) {
		toolbar.setMouseDown(false);
		endPosition = event.getClientX();

		if (startPosition == endPosition) {
			super.onMouseUp(event);
		}
	}


	@Override
	public void onMouseMove(MouseMoveEvent event) {
			toolbar.setPosition(event.getClientX());
	}

}
