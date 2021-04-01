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
import org.geogebra.web.html5.gui.util.AriaMenuBar;
import org.geogebra.web.html5.gui.util.AriaMenuItem;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.resources.SVGResource;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;

/**
 * Popup menu for web.
 * 
 * @author Judit Elias
 */
public class GPopupMenuW implements AttachedToDOM {

	public static final int SUBMENU_VERTICAL_PADDING = 16;
	/**
	 * popup panel
	 */
	protected GPopupPanel popupPanel;
	/**
	 * popup menu
	 */
	protected PopupMenuBar popupMenu;
	private int popupMenuSize = 0;
	/**
	 * popup panel for submenu this field used to avoid having more submenu at
	 * the same time
	 */
	GPopupMenuW subPopup;
	private AppW app;
	private boolean menuShown = false;

	private boolean horizontal;
	private AriaMenuItem openItem = null;

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
		popupPanel = new GPopupPanel(app.getPanel(), app);
		popupMenu = new PopupMenuBar(app);
		popupMenu.setAutoOpen(true);
		popupPanel.add(popupMenu);

		popupPanel.addCloseHandler(event -> {
			if (subPopup != null) {
				subPopup.removeFromDOM();
				subPopup = null;
			}
			if (event.isAutoClosed()) {
				setMenuShown(false);
			}
		});

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
	public GPopupMenuW(Widget mb, AppW app) {
		this.app = app;
		popupPanel = new GPopupPanel(app.getPanel(), app);
		popupPanel.add(mb);
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
	 * @param x  x-coord of the popup
	 * @param y  y-coord of the popup
	 */
	public final void show(double x, double y) {
		double yOffset = app.getPanel().getAbsoluteTop() / getScaleY();
		double xOffset = app.getPanel().getAbsoluteLeft() / getScaleX();
		int top = (int) (y - yOffset);
		int left = (int) (x - xOffset);
		boolean newPoz = false;
		showAtPoint(left, top);
		int leftMargin = app.getPanel().getAbsoluteLeft()
				+ app.getPanel().getOffsetWidth();
		int bottomMargin = app.getPanel().getAbsoluteTop()
				+ app.getPanel().getOffsetHeight();
		if ((x + popupPanel.getOffsetWidth())
				* getScaleX() > leftMargin
						+ Window.getScrollLeft()) {
			left = (int) ((leftMargin + Window.getScrollLeft())
					/ getScaleX() - xOffset - popupPanel.getOffsetWidth());
			newPoz = true;
		}
		if ((y + popupPanel.getOffsetHeight())
				* getScaleY() > bottomMargin
						+ Window.getScrollTop()) {
			top = (int) ((bottomMargin + Window.getScrollTop())
					/ getScaleY() - yOffset - popupPanel.getOffsetHeight());
			newPoz = true;
		}

		if (newPoz || !DoubleUtil.isEqual(1, getScaleX())) {
			popupPanel.setPopupPosition(left, top);
			// App.debug(left + "x" + top);
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
	 *            canvas
	 * @param x
	 *            coord to show popup
	 * @param y
	 *            coord to show popup
	 */
	public void showScaled(Element c, int x, int y) {
		show((int) (c.getAbsoluteLeft() / getScaleX() + x),
				(int) (c.getAbsoluteTop() / getScaleY() + y));
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
		show(c.getAbsoluteLeft() + x, c.getAbsoluteTop() + y);
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
		return popupMenuSize;
	}

	/**
	 * add seperator to menu
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
		setMenuShown(false);
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
			String itemHTML = item.getHTML();
			ScheduledCommand itemCommand = null;
			final AriaMenuItem newItem = new AriaMenuItem(itemHTML, true,
					itemCommand);
			newItem.setStyleName(item.getStyleName());
			newItem.getElement().setAttribute("hasPopup", "true");
			popupMenu.addItem(newItem);
			itemCommand = () -> {
				if (subPopup != null) {
					subPopup.removeFromDOM();
				}
				subPopup = new GPopupMenuW(subMenu, getApp());
				subPopup.setVisible(true);
				subMenu.unselect();
				subMenu.stylePopup(subPopup.getPopupPanel());
				// Calculate the position of the "submenu", and show it
				openItem = newItem;
				positionAndShowSubmenu();

			};
			newItem.setScheduledCommand(itemCommand);

			// adding arrow for the menuitem

			popupMenu.setParentMenu(this);
			if (!horizontal) {
				SVGResource imgRes = getSubMenuIcon(
						app.getLocalization().isRightToLeftReadingOrder());
				popupMenu.appendSubmenu(newItem, imgRes);
			}

		}
		popupMenuSize++;
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
		return Math.max(SUBMENU_VERTICAL_PADDING, getRelativeTop(openItem.getAbsoluteTop()));
	}

	/**
	 * Submenu is placed to the left by default,
	 * to the right if it would go offscreen.
	 * @return x where submenu should placed
	 */
	private int getPopupXCoord() {
		int xCoord = getRightSubPopupXCord();
		int leftMargin = app.getPanel().getAbsoluteLeft()
					+ app.getPanel().getOffsetWidth();
		return (xCoord + getSubPopupWidth()
				> leftMargin)
				? getLeftSubPopupXCord() : xCoord;
	}

	/**
	 * Submenu is placed to the right by default,
	 * to the left if it would go offscreen. (RTL)
	 * @return x where submenu should placed
	 */
	private int getPopupXCoordRTL() {
		return getLeftSubPopupXCord() < 0 ? getRightSubPopupXCord() : getLeftSubPopupXCord();
	}

	private boolean isRightToLeftReadingOrder() {
		return app.getLocalization().isRightToLeftReadingOrder();
	}

	private int alignPopupToBottom() {
		int absTop = Math.max(SUBMENU_VERTICAL_PADDING,
				Window.getClientHeight() + Window.getScrollTop()
				- getSubPopupHeight() - SUBMENU_VERTICAL_PADDING);

		return getRelativeTop(absTop);
	}

	/**
	 *
	 * @param absoluteTop to convert.
	 * @return the relative top within the applet
	 */
	private int getRelativeTop(int absoluteTop) {
		return (int) (Math.round(absoluteTop - getApp().getPanel().getAbsoluteTop()
				/ getScaleY()));
	}

	/**
	 * Adds an expand/collapse item {@link GCollapseMenuItem} to the popup.
	 * 
	 * @param ci
	 *            The collapse item to add.
	 */
	public void addItem(GCollapseMenuItem ci) {
		addItem(ci.getMenuItem(), false);
		popupMenu.addItem(ci);
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
				- app.getPanel().getAbsoluteLeft()) / getScaleX()
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
				- app.getPanel().getAbsoluteLeft()) / getScaleX()
				+ popupPanel.getOffsetWidth());
	}

	/**
	 * @param s
	 *            title
	 * @param c
	 *            command
	 */
	public void addItem(String s, ScheduledCommand c) {
		addItem(new AriaMenuItem(s, false, c));
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
		popupPanel.hide();
		if (anchor != null) {
			anchor.focusIfVisible(true);
		}
	}

	/**
	 * @return popup menu
	 */
	public GMenuBar getPopupMenu() {
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
		return menuShown;
	}

	/**
	 * @param menuShown
	 *            true if menu is shown
	 */
	public void setMenuShown(boolean menuShown) {
		this.menuShown = menuShown;
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
		}
	}

	private class PopupMenuBar extends GMenuBar {

		private GPopupMenuW selectListener;
		private Map<AriaMenuItem, GCollapseMenuItem> expandItems = new HashMap<>();
		private GCollapseMenuItem activeCollapseItem = null;

		public PopupMenuBar(AppW app1) {
			super("", app1);
			setHandleArrows(true);
		}

		public void addItem(GCollapseMenuItem ci) {
			expandItems.put(ci.getMenuItem(), ci);
		}

		public void setParentMenu(GPopupMenuW gPopupMenuW) {
			this.selectListener = gPopupMenuW;
		}

		@Override
		public void removeSubPopup() {
			if (selectListener != null) {
				selectListener.removeSubPopup();
			}
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
					if ("true".equals(
							item.getElement().getAttribute("hasPopup"))) {
						ScheduledCommand cmd = item.getScheduledCommand();
						if (cmd != null) {
							cmd.execute();
						}
					} else {
						removeSubPopup();
					}
				}
			} else if (DOM.eventGetType(event) == Event.ONKEYDOWN) {
				char keyCode = (char) event.getKeyCode();
				if (keyCode == KeyCodes.KEY_ESCAPE) {
					hide();
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
}