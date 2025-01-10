package org.geogebra.web.full.javax.swing;

import java.util.HashMap;
import java.util.Map;

import org.geogebra.common.gui.AccessibilityManagerInterface;
import org.geogebra.common.gui.MayHaveFocus;
import org.geogebra.common.util.DoubleUtil;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.menubar.GMenuBar;
import org.geogebra.web.full.html5.AttachedToDOM;
import org.geogebra.web.html5.gui.GPopupPanel;
import org.geogebra.web.html5.gui.GeoGebraFrameW;
import org.geogebra.web.html5.gui.menu.AriaMenuBar;
import org.geogebra.web.html5.gui.menu.AriaMenuItem;
import org.geogebra.web.html5.gui.menu.MenuHoverListener;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.resources.SVGResource;
import org.gwtproject.core.client.Scheduler;
import org.gwtproject.core.client.Scheduler.ScheduledCommand;
import org.gwtproject.dom.client.Element;
import org.gwtproject.event.dom.client.ClickEvent;
import org.gwtproject.event.dom.client.KeyCodes;
import org.gwtproject.user.client.DOM;
import org.gwtproject.user.client.Event;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Widget;

import com.himamis.retex.editor.share.util.JavaKeyCodes;

/**
 * Popup menu for web.
 *
 * @author Judit Elias
 */
public class GPopupMenuW implements AttachedToDOM, MenuHoverListener {

	public static final int SUBMENU_VERTICAL_PADDING = 16;
	/**
	 * popup panel
	 */
	protected GPopupPanel popupPanel;
	/**
	 * popup menu
	 */
	protected AriaMenuBar popupMenu;
	/**
	 * popup panel for submenu this field used to avoid having more submenu at
	 * the same time
	 */
	private GPopupMenuW subPopup;
	private final AppW app;

	private boolean horizontal;
	protected AriaMenuItem openItem = null;

	/**
	 * @param app
	 *
	 *            Creates a popup menu. App needed for get environment style
	 */
	public GPopupMenuW(AppW app) {
		this(app, false);
	}

	/**
	 * @param app
	 *
	 *            Creates a popup menu. App needed for get environment style
	 * @param horizontal
	 *            whether this is horizontal menu
	 */
	public GPopupMenuW(AppW app, boolean horizontal) {
		this.app = app;
		this.horizontal = horizontal;
		popupPanel = new GPopupPanel(app.getAppletFrame(), app);
		popupMenu = new PopupMenuBar(app);
		popupMenu.setAutoOpen(true);
		popupPanel.add(popupMenu);

		popupPanel.addCloseHandler(event -> removeSubPopup());

		popupPanel.setAutoHideEnabled(true);
	}

	/**
	 * Constructor for submenu-popups
	 *
	 * @param mb
	 *            menu
	 * @param app
	 *            application
	 */
	public GPopupMenuW(AriaMenuBar mb, AppW app, Widget heading) {
		this.app = app;
		popupPanel = new GPopupPanel(app.getAppletFrame(), app);
		popupMenu = mb;
		if (heading != null) {
			FlowPanel merged = new FlowPanel();
			merged.add(heading);
			merged.add(mb);
			merged.addStyleName("submenuWithHeading");
			popupPanel.add(merged);
			popupPanel.addStyleName("hasHeading");
		} else {
			popupPanel.add(mb);
		}
		popupPanel.addStyleName("contextSubMenu");
	}

	/**
	 * @param v
	 *            whether to show this
	 */
	public void setVisible(boolean v) {
		popupPanel.setVisible(v);
	}

	/**
	 * Shows the popup menu, ensures that the popup menu must be on the client
	 * area.
	 *
	 * @param xScaled  x-coord of the popup
	 * @param yScaled  y-coord of the popup
	 */
	public final void show(double xScaled, double yScaled) {
		GeoGebraFrameW frame = app.getAppletFrame();
		double scaleX = getScaleX();
		double frameTopScaled = frame.getAbsoluteTop() / getScaleY();
		double frameLeftScaled = frame.getAbsoluteLeft() / scaleX;
		int top = (int) (yScaled - frameTopScaled);
		int left = (int) (xScaled - frameLeftScaled);
		boolean newPoz = false;
		showAtPoint(left, top);
		if ((left + popupPanel.getOffsetWidth()) > frame.getOffsetWidth()) {
			left = frame.getOffsetWidth() - popupPanel.getOffsetWidth();
			newPoz = true;
		}
		if ((top + popupPanel.getOffsetHeight()) > frame.getOffsetHeight()) {
			top = frame.getOffsetHeight() - popupPanel.getOffsetHeight();
			newPoz = true;
		}

		if (newPoz || !DoubleUtil.isEqual(1, scaleX)) {
			popupPanel.setPopupPosition(left, top);
		}

		positionAndShowSubmenu();
	}

	/**
	 * Shows the popup menu at the p point, independently of there is enough
	 * place for the popup menu. (Maybe some details of the popup menu won't be
	 * visible.)
	 *
	 * @param x
	 *            x-coord to show popup
	 * @param y
	 *            y-coord to show popup
	 */
	public final void showAtPoint(int x, int y) {
		popupPanel.setPopupPosition(x, y);
		popupPanel.show();
	}

	/**
	 * @param c
	 *            widget
	 * @param x
	 *            coord to show popup
	 * @param y
	 *            coord to show popup
	 */
	public void show(Widget c, int x, int y) {
		show(c.getElement(), x, y);
	}

	/**
	 * @param c
	 *            canvas
	 * @param x
	 *            coord to show popup
	 * @param y
	 *            coord to show popup
	 */
	public void show(Element c, int x, int y) {
		show((int) (c.getAbsoluteLeft() / getScaleX() + x),
				(int) (c.getAbsoluteTop() / getScaleY() + y));
	}

	@Override
	public void removeFromDOM() {
		removeSubPopup();
		popupPanel.removeFromParent();
	}

	/**
	 * clear popup
	 */
	public void clearItems() {
		popupMenu.clearItems();
	}

	/**
	 * @return nr of menu items
	 */
	public int getComponentCount() {
		return popupMenu.getWidgetCount();
	}

	/**
	 * add separator to menu
	 */
	public void addSeparator() {
		if (!app.isUnbundled()) {
			popupMenu.addSeparator();
		}
	}

	/**
	 * add vertical separator
	 */
	public void addVerticalSeparator() {
		popupMenu.addSeparator();
	}

	private void addHideCommandFor(AriaMenuItem item) {
		AriaMenuBar submenu = item.getSubMenu();
		if (submenu == null) {
			final ScheduledCommand oldCmd = item.getScheduledCommand();
			ScheduledCommand cmd = () -> {
				if (oldCmd != null) {
					oldCmd.execute();
				}
				hideMenu();
			};
			item.setScheduledCommand(cmd);
		} else {
			submenu.addHandler(event -> popupPanel.hide(), ClickEvent.getType());
		}
	}

	/**
	 * Hide menu and mark this as hidden
	 */
	public void hideMenu() {
		popupPanel.hide();
		hide();
	}

	private static SVGResource getSubMenuIcon(boolean isRTL) {
		return isRTL ? MaterialDesignResources.INSTANCE.arrow_drop_left_black()
				: MaterialDesignResources.INSTANCE.arrow_drop_right_black();
	}

	/**
	 * @param item
	 *            to add to popup menu
	 */
	public void addItem(final AriaMenuItem item) {
		addItem(item, true);
	}

	/**
	 * @param item
	 *            check mark menu item
	 */
	public void addItem(GCheckmarkMenuItem item) {
		addItem(item.getMenuItem());
	}

	/**
	 * @param item
	 *            to add
	 * @param autoHide
	 *            true if auto hide
	 */
	public void addItem(final AriaMenuItem item, boolean autoHide) {
		final AriaMenuBar subMenu = item.getSubMenu();
		if (autoHide) {
			addHideCommandFor(item);
		}
		if (subMenu == null) {
			popupMenu.addItem(item);
		} else {
			// The submenu is not added for the menu as submenu,
			// but this will be placed on a different popup panel.
			// In this way we can set this popup panel's position easily.
			item.getElement().setAttribute("hasPopup", "true");
			popupMenu.addItem(item);
			ScheduledCommand itemCommand = () -> openSubmenu(item);
			item.setScheduledCommand(itemCommand);

			// adding arrow for the menuitem

			popupMenu.setSelectionListener(this);
			if (!horizontal) {
				SVGResource imgRes = getSubMenuIcon(
						app.getLocalization().isRightToLeftReadingOrder());
				popupMenu.appendSubmenu(item, imgRes);
			}
		}
	}

	protected void openSubmenu(AriaMenuItem item) {
		final AriaMenuBar subMenu = item.getSubMenu();
		if (subPopup != null) {
			subPopup.removeFromDOM();
		}
		subPopup = new GPopupMenuW(subMenu, getApp(), item.getSubmenuHeading());

		subPopup.setVisible(true);
		subMenu.unselect();
		subMenu.stylePopup(subPopup.getPopupPanel());
		// Calculate the position of the "submenu", and show it
		openItem = item;
		positionAndShowSubmenu();
	}

	/**
	 * Calculates where to place the submenu and show it.
	 */
	private void positionAndShowSubmenu() {
		if (subPopup == null || openItem == null) {
			return;
		}

		int x = isRightToLeftReadingOrder() ? getPopupXCoordRTL() : getPopupXCoord();
		int y = Math.min(alignPopupToOpenItem(), alignPopupToBottom());
		showSubPopup(x, y);
	}

	private int alignPopupToOpenItem() {
		int absoluteTop = (int) ((openItem.getAbsoluteTop()
				- getApp().getAppletFrame().getAbsoluteTop()) / getScaleY());
		return Math.max(SUBMENU_VERTICAL_PADDING, absoluteTop);
	}

	/**
	 * Submenu is placed to the left by default,
	 * to the right if it would go off-screen.
	 * @return x where submenu should be placed
	 */
	private int getPopupXCoord() {
		int xCoordRightSide = getRightSubPopupXCord();
		int rightSideMargin = app.getAppletFrame().getOffsetWidth();
		int spaceOnTheRightSide = rightSideMargin - xCoordRightSide;

		int xCoordLeftSide = getLeftSubPopupXCord();
		int spaceOnTheLeftSide = (int) ((getPopupLeft()
				- app.getAppletFrame().getAbsoluteLeft()) / getScaleX());

		if (spaceOnTheRightSide >= getSubPopupWidth()) {
			return xCoordRightSide;
		} else if (spaceOnTheLeftSide >= getSubPopupWidth()) {
			return xCoordLeftSide;
		} else {
			if (spaceOnTheRightSide >= spaceOnTheLeftSide) {
				return Math.max(rightSideMargin - getSubPopupWidth(), 0);
			} else {
				return Math.max(xCoordLeftSide, 0);
			}
		}
	}

	/**
	 * Submenu is placed to the right by default,
	 * to the left if it would go off-screen. (RTL)
	 * @return x where submenu should be placed
	 */
	private int getPopupXCoordRTL() {
		return getLeftSubPopupXCord() < 0 ? getRightSubPopupXCord() : getLeftSubPopupXCord();
	}

	private boolean isRightToLeftReadingOrder() {
		return app.getLocalization().isRightToLeftReadingOrder();
	}

	private int alignPopupToBottom() {
		return app.getAppletFrame().getOffsetHeight() - getSubPopupHeight();
	}

	/**
	 * Adds an expand/collapse item {@link GCollapseMenuItem} to the popup.
	 *
	 * @param ci
	 *            The collapse item to add.
	 */
	public void addItem(GCollapseMenuItem ci) {
		addItem(ci.getMenuItem(), false);
		((PopupMenuBar) popupMenu).addItem(ci);
	}

	/**
	 * Show submenu popup
	 *
	 * @param xCoord
	 *            popup x
	 * @param yCoord
	 *            popup y
	 */
	protected void showSubPopup(int xCoord, int yCoord) {
		if (horizontal) {
			app.registerPopup(subPopup.getPopupPanel());
			subPopup.showAtPoint(xCoord, yCoord + 32);
		} else {
			subPopup.showAtPoint(xCoord, yCoord);
		}
	}

	/**
	 * @return app scale (vertical)
	 */
	protected double getScaleY() {
		return app.getGeoGebraElement().getScaleY();
	}

	/**
	 * @return app scale (horizontal)
	 */
	protected double getScaleX() {
		return app.getGeoGebraElement().getScaleX();
	}

	/**
	 * @return the width of the submenu.
	 */
	public int getSubPopupWidth() {
		int width;
		boolean shown = subPopup.popupPanel.isShowing();
		if (!shown) {
			subPopup.popupPanel.show();
		}
		width = subPopup.popupPanel.getOffsetWidth();
		if (!shown) {
			subPopup.popupPanel.hide();
		}
		return width;
	}

	/**
	 * @return the height of the submenu.
	 */
	public int getSubPopupHeight() {
		int ret;
		boolean shown = subPopup.popupPanel.isShowing();
		if (!shown) {
			subPopup.popupPanel.show();
		}
		ret = subPopup.popupPanel.getOffsetHeight();
		if (!shown) {
			subPopup.popupPanel.hide();
		}
		return ret;
	}

	/**
	 * Gets the submenu's suggested absolute left position in pixels, as
	 * measured from the browser window's client area, in case of the submenu is
	 * on the left side of its parent menu.
	 *
	 * @return submenu's left position in pixels
	 */
	public final int getLeftSubPopupXCord() {
		int xCord;
		xCord = (int) ((getPopupLeft()
				- app.getAppletFrame().getAbsoluteLeft()) / getScaleX()
				- getSubPopupWidth());
		return xCord;
	}

	/**
	 * @return left x-coord of menu
	 */
	public int getPopupLeft() {
		return popupPanel.getAbsoluteLeft();
	}

	/**
	 * Gets the submenu's suggested absolute left position in pixels, as
	 * measured from the browser window's client area, in case of the submenu is
	 * on the right side of its parent menu.
	 *
	 * @return submenu's left position in pixels
	 */
	public final int getRightSubPopupXCord() {
		return (int) ((getPopupLeft()
				- app.getAppletFrame().getAbsoluteLeft()) / getScaleX()
				+ popupPanel.getOffsetWidth());
	}

	/**
	 * @param s
	 *            title
	 * @param c
	 *            command
	 */
	public void addItem(String s, ScheduledCommand c) {
		addItem(new AriaMenuItem(s, null, c));
	}

	/**
	 * hide popup menu
	 */
	public final void hide() {
		if (!popupPanel.isShowing()) {
			return;
		}

		AccessibilityManagerInterface am = getApp()
				.getAccessibilityManager();
		MayHaveFocus anchor = am.getAnchor();
		if (subPopup != null && subPopup.isMenuShown()) {
			removeSubPopup();
		} else {
			popupPanel.hide();
		}
		if (anchor != null) {
			anchor.focusIfVisible(true);
		}
	}

	/**
	 * @return popup menu
	 */
	public AriaMenuBar getPopupMenu() {
		return popupMenu;
	}

	/**
	 * @return popup panel
	 */
	public GPopupPanel getPopupPanel() {
		return popupPanel;
	}

	/**
	 * @return true if menu is shown
	 */
	public boolean isMenuShown() {
		return popupMenu.isAttached() && popupMenu.isVisible();
	}

	/**
	 * @return application
	 */
	public AppW getApp() {
		return app;
	}

	/**
	 * remove sub popup menu
	 */
	public void removeSubPopup() {
		if (subPopup != null) {
			subPopup.removeFromDOM();
			subPopup = null;
			openItem = null;
		}
	}

	/**
	 * Handle arrow keys
	 * @param keyCode key code
	 */
	public void onArrowKeyPressed(int keyCode) {
		AriaMenuBar target = popupMenu;
		if (subPopup != null && subPopup.isMenuShown()
				&& subPopup.popupMenu.getSelectedItem() != null) {
			target = subPopup.popupMenu;
		}
		if (keyCode == JavaKeyCodes.VK_UP) {
			target.moveSelectionUp();
		} else if (keyCode == JavaKeyCodes.VK_DOWN) {
			target.moveSelectionDown();
		} else if (keyCode == JavaKeyCodes.VK_RIGHT) {
			if (target.getSelectedItem() != null
					&& target.getSelectedItem().getSubMenu() != null) {
				openSubmenu(target.getSelectedItem());
				target.getSelectedItem().getSubMenu().selectItem(0);
				target.getSelectedItem().getSubMenu().getItemAt(0).addStyleName("fakeFocus");
			}
		} else if (keyCode == JavaKeyCodes.VK_LEFT) {
			target.getSelectedItem().removeStyleName("fakeFocus");
			removeSubPopup();
		}
	}

	@Override
	public void onItemHover() {
		removeSubPopup();
	}

	/**
	 * @param c element
	 * @param x x-coordinate relative to element
	 * @param y y-coordinate relative to element
	 */
	public void showAndFocus(Element c, int x, int y) {
		show(c, x, y);
		Scheduler.get().scheduleDeferred(popupMenu::focus);
	}

	private class PopupMenuBar extends GMenuBar {

		private final Map<AriaMenuItem, GCollapseMenuItem> expandItems = new HashMap<>();
		private GCollapseMenuItem activeCollapseItem = null;

		public PopupMenuBar(AppW app1) {
			super("", app1);
			setHandleArrows(true);
		}

		public void addItem(GCollapseMenuItem ci) {
			expandItems.put(ci.getMenuItem(), ci);
		}

		private AriaMenuItem findItem(Element hItem) {
			for (AriaMenuItem item : getItems()) {
				if (item.getElement().isOrHasChild(hItem)) {
					return item;
				}
			}
			return null;
		}

		@Override
		public void onBrowserEvent(Event event) {
			if (DOM.eventGetType(event) == Event.ONMOUSEOVER) {
				AriaMenuItem item = findItem(DOM.eventGetTarget(event));
				if (item != null) {
					if (item.getSubMenu() != null) {
						removeFakeFocus();
						openSubmenu(item);
					} else {
						GPopupMenuW.this.onItemHover();
					}
				}
			} else if (DOM.eventGetType(event) == Event.ONKEYDOWN) {
				char keyCode = (char) event.getKeyCode();
				if (keyCode == KeyCodes.KEY_ESCAPE) {
					hide();
					event.stopPropagation();
				} else if (keyCode == KeyCodes.KEY_TAB) {
					if (event.getShiftKey()) {
						if (!moveSelectionUp()) {
							hide();
						}
					} else {
						if (!moveSelectionDown()) {
							hide();
						}
					}
					AriaMenuBar.eatEvent(event);
					return;
				}
			}
			super.onBrowserEvent(event);
		}

		@Override
		public boolean moveSelectionDown() {
			if (activeCollapseItem != null && activeCollapseItem.isExpanded()) {
				AriaMenuBar submenu = activeCollapseItem.getItems();
				if (submenu.moveSelectionDown()) {
					return true;
				}
				submenu.selectItem(null);
				activeCollapseItem = null;
			}
			boolean result = super.moveSelectionDown();
			activeCollapseItem = getCollapseMenuAt(getSelectedIndex());
			return result;
		}

		@Override
		public boolean moveSelectionUp() {
			if (activeCollapseItem != null && activeCollapseItem.isExpanded()) {
				AriaMenuItem mi = activeCollapseItem.getMenuItem();
				if (getSelectedItem() == mi && activeCollapseItem.getItems()
						.getSelectedIndex() == -1) {
					activeCollapseItem = null;
					return super.moveSelectionUp();
				}
				if (!activeCollapseItem.getItems().moveSelectionUp()) {
					selectItem(mi);
					activeCollapseItem.getItems().selectItem(null);
				}
				return true;
			}
			AriaMenuItem si = getSelectedItem();
			boolean result = super.moveSelectionUp();
			activeCollapseItem = getCollapseMenuAt(getSelectedIndex());
			if (activeCollapseItem != null
					&& activeCollapseItem.isExpanded()
					&& activeCollapseItem.getMenuItem() != si) {
				activeCollapseItem.getItems().selectLastItem();
			}

			return result;
		}

		private GCollapseMenuItem getCollapseMenuAt(int idx) {
			if (idx < 0 || idx >= getItems().size()) {
				return null;
			}
			return expandItems.get(getItemAt(idx));
		}
	}

	/**
	 * clear out fake focus
	 */
	public void removeFakeFocus() {
		for (AriaMenuItem item : popupMenu.getItems()) {
			item.removeStyleName("fakeFocus");
		}
	}
}