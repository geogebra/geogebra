package org.geogebra.web.web.javax.swing;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.web.html5.gui.GPopupPanel;
import org.geogebra.web.html5.gui.util.AriaMenuBar;
import org.geogebra.web.html5.gui.util.AriaMenuItem;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.css.MaterialDesignResources;
import org.geogebra.web.web.gui.menubar.GMenuBar;
import org.geogebra.web.web.html5.AttachedToDOM;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.impl.ImageResourcePrototype;
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

	/**
	 * @param app
	 * 
	 *            Creates a popup menu. App needed for get environment style
	 */
	public GPopupMenuW(AppW app) {
		this.app = app;
		popupPanel = new GPopupPanel(app.getPanel(), app);
		popupMenu = new PopupMenuBar(app);
		popupMenu.setAutoOpen(true);
		popupPanel.add(popupMenu);

		popupPanel.addCloseHandler(new CloseHandler<GPopupPanel>() {

			@Override
			public void onClose(CloseEvent<GPopupPanel> event) {
				if (subPopup != null) {
					subPopup.removeFromDOM();
					subPopup = null;
				}
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
	public GPopupMenuW(AriaMenuBar mb, AppW app) {
		popupPanel = new GPopupPanel(app.getPanel(), app);
		popupPanel.add(mb);
		if (app.isUnbundledOrWhiteboard()) {
			popupPanel.addStyleName("matSubMenu");
		}
	}

	// public void add(MenuItem mi) {
	// impl.addItem(mi);
	//
	// }

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
	 * @param p
	 *            point to show popup
	 */
	public final void show(GPoint p) {
		double yOffset =  app.getPanel().getAbsoluteTop()
				/ getScaleY();
		double xOffset = app.getPanel().getAbsoluteLeft()
				/ getScaleX();
		int top = (int) (p.getY() - yOffset);
		int left = (int) (p.getX() - xOffset);
		boolean newPoz = false;
		showAtPoint(left, top);
		if ((p.getX() + popupPanel.getOffsetWidth())
				* getScaleX() > Window.getClientWidth()
						+ Window.getScrollLeft()) {
			left = (int) ((Window.getClientWidth() + Window.getScrollLeft())
					/ getScaleX() - xOffset
					- popupPanel.getOffsetWidth());
			newPoz = true;
		}
		if ((p.getY() + popupPanel.getOffsetHeight())
				* getScaleY() > Window.getClientHeight()
						+ Window.getScrollTop()) {
			top = (int) (((Window.getClientHeight() + Window.getScrollTop()))
					/ getScaleY() - yOffset
					- popupPanel.getOffsetHeight());
			newPoz = true;
		}

		if (newPoz || !Kernel.isEqual(1, getScaleX())) {
			popupPanel.setPopupPosition(left, top);
			// App.debug(left + "x" + top);
		}
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
	public void show(Canvas c, int x, int y) {
		show(new GPoint(
				(int) (c.getAbsoluteLeft() / getScaleX() + x),
				(int) (c.getAbsoluteTop() / getScaleY() + y)));
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
		show(new GPoint(c.getAbsoluteLeft() + x, c.getAbsoluteTop() + y));
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
			ScheduledCommand cmd = new ScheduledCommand() {
				@Override
				public void execute() {
					if (oldCmd != null) {
						oldCmd.execute();
					}
					hideMenu();
				}
			};
			item.setScheduledCommand(cmd);
		} else {
			// CloseHandler<PopupPanel> closehandler = new
			// CloseHandler<PopupPanel>(){
			// public void onClose(CloseEvent<PopupPanel> event) {
			// App.debug("popuppanel closed");
			// }
			// };
			// submenu.addCloseHandler(closehandler);
			// submenu.addHandler(closehandler, CloseEvent.getType());

			submenu.addHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					popupPanel.hide();
				}
			}, ClickEvent.getType());
		}
	}

	public void hideMenu() {
		setMenuShown(false);
		popupPanel.hide();
		hide();
	}

	private static ImageResource getSubMenuIcon(boolean isRTL) {
		return isRTL
					? new ImageResourcePrototype(null,
							MaterialDesignResources.INSTANCE
									.arrow_drop_left_black().getSafeUri(),
							0, 0, 24, 24, false, false)
					: new ImageResourcePrototype(null,
							MaterialDesignResources.INSTANCE
									.arrow_drop_right_black().getSafeUri(),
							0, 0, 24, 24, false, false);

	}

	// public void addItem(final MenuItem item) {
	// addHideCommandFor(item);
	// popupMenu.addItem(item);
	// popupMenuSize++;
	// }

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
			itemCommand = new ScheduledCommand() {
				@Override
				public void execute() {
					int xCord, yCord;
					if (subPopup != null) {
						subPopup.removeFromDOM();
					}
					subPopup = new GPopupMenuW(subMenu, getApp());
					subPopup.setVisible(true);
					// Calculate the position of the "submenu", and show it
					if (getApp().getLocalization()
							.isRightToLeftReadingOrder()) {
						xCord = getLeftSubPopupXCord();
						if (xCord < 0) {
							xCord = getRightSubPopupXCord();
						}
					} else {
						xCord = getRightSubPopupXCord();
						if (xCord + getSubPopupWidth() > Window
						        .getClientWidth()) {
							xCord = getLeftSubPopupXCord();
						}
					}
					yCord = (int) Math.min(
							(newItem.getAbsoluteTop()
									- getApp().getPanel().getAbsoluteTop())
									/ getScaleY(),
							(Window.getClientHeight() + Window.getScrollTop()
									- getApp().getPanel().getAbsoluteTop())
									/ getScaleY()
									- getSubPopupHeight());
					subPopup.showAtPoint(xCord, yCord);
				}
			};
			newItem.setScheduledCommand(itemCommand);

			// adding arrow for the menuitem
			ImageResource imgRes = getSubMenuIcon(
					app.getLocalization().isRightToLeftReadingOrder());
			popupMenu.setParentMenu(this);

			popupMenu.appendSubmenu(newItem, imgRes);

		}
		popupMenuSize++;

		item.addStyleName("gPopupMenu_item");
	}

	/**
	 * @return app scale (vertical)
	 */
	protected double getScaleY() {
		return app.getArticleElement().getScaleY();
	}

	/**
	 * @return app scale (horizontal)
	 */
	protected double getScaleX() {
		return app.getArticleElement().getScaleX();
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
	public int getLeftSubPopupXCord() {
		int xCord;
		xCord = (int) ((popupPanel.getAbsoluteLeft()
				- app.getPanel().getAbsoluteLeft())
				/ getScaleX() - getSubPopupWidth());
		return xCord;
	}

	/**
	 * Gets the submenu's suggested absolute left position in pixels, as
	 * measured from the browser window's client area, in case of the submenu is
	 * on the right side of its parent menu.
	 * 
	 * @return submenu's left position in pixels
	 */
	public int getRightSubPopupXCord() {
		return (int) ((popupPanel.getAbsoluteLeft()
				- app.getPanel().getAbsoluteLeft())
				/ getScaleX()
				+ popupPanel.getOffsetWidth());
	}

	/**
	 * @param item
	 *            check box menu item
	 */
	public void addItem(GCheckBoxMenuItem item) {
		addItem(item.getMenuItem());

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
	public void hide() {
		popupPanel.hide();
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

		public PopupMenuBar(AppW app1) {
			super("", app1);
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
					popupPanel.hide();
				}
			}
			super.onBrowserEvent(event);
		}
	}

}
