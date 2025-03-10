package org.geogebra.web.full.gui.menubar;

import java.util.ArrayList;

import org.geogebra.common.exam.ExamController;
import org.geogebra.common.move.events.BaseEvent;
import org.geogebra.common.move.ggtapi.events.LogOutEvent;
import org.geogebra.common.move.ggtapi.events.LoginEvent;
import org.geogebra.common.move.views.BooleanRenderable;
import org.geogebra.common.move.views.EventRenderable;
import org.geogebra.common.ownership.GlobalScope;
import org.geogebra.common.plugin.EventType;
import org.geogebra.web.full.css.GuiResources;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.images.AppResources;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.gui.menu.AriaMenuBar;
import org.geogebra.web.html5.gui.menu.AriaMenuItem;
import org.geogebra.web.html5.gui.util.NoDragImage;
import org.geogebra.web.html5.gui.view.IconSpec;
import org.geogebra.web.html5.util.TestHarness;
import org.geogebra.web.resources.SVGResource;
import org.gwtproject.core.client.Scheduler;
import org.gwtproject.dom.client.Element;
import org.gwtproject.event.dom.client.KeyCodes;
import org.gwtproject.event.dom.client.KeyDownEvent;
import org.gwtproject.event.dom.client.KeyDownHandler;
import org.gwtproject.resources.client.ResourcePrototype;
import org.gwtproject.safehtml.shared.annotations.IsSafeHtml;
import org.gwtproject.user.client.DOM;
import org.gwtproject.user.client.Event;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Widget;

/**
 * Sidebar menu for SMART
 *
 *
 */
public class MainMenu extends FlowPanel
		implements EventRenderable, BooleanRenderable, KeyDownHandler {

	AppWFull app;

	/**
	 * Panel with menus
	 */
	AriaStackPanel menuPanel;

	// private boolean leftSide = false;
	/** whether to use small screen layout (includes logo) */
	public boolean smallScreen = false;
	/**
	 * Menus
	 */
	ArrayList<Submenu> menus;
	/** user menu */
	Submenu userMenu;
	/** sign in menu */
	final SignInMenu signInMenu;

	private final ClassicMenuItemProvider actionProvider;
	private final ExamController examController = GlobalScope.examController;

	/**
	 * Constructs the menubar
	 *
	 * @param app
	 *            application
	 */
	public MainMenu(AppWFull app) {
		if (!app.isUnbundledOrWhiteboard()) {
			this.addStyleName("menuBarClassic");
		}
		this.actionProvider = new ClassicMenuItemProvider(app);
		signInMenu = new SignInMenu(app);
		this.app = app;
		init();
	}

	private void init() {
		app.ensureLoginOperation();
		this.app.getLoginOperation().getView().add(this);
		final boolean exam = !examController.isIdle();

		this.menus = new ArrayList<>();
		this.userMenu = new UserSubmenu(app);
		actionProvider.addMenus(menus);

		initAriaStackPanel();

		this.menuPanel.addStyleName("menuPanel");

		for (Submenu menu : menus) {
			addSubmenu(menu);
		}

		if (!exam) {

			if (app.getNetworkOperation().isOnline()) {
				render(true);
			}
			app.getNetworkOperation().getView().add(this);
		}
		add(menuPanel);
	}

	private void addSubmenu(Submenu submenu) {
		if (app.isUnbundledOrWhiteboard() && !submenu.isEmpty()) {
			this.menuPanel.add(submenu, getExpandCollapseHTML(submenu), true);
		} else {
			addSimple(submenu);
		}
	}

	private void initAriaStackPanel() {
		this.menuPanel = new AriaStackPanel() {
			@Override
			public void showStack(int index) {
				if (app.isUnbundledOrWhiteboard()) {
					int selected = getSelectedIndex();
					collapseStack(getSelectedIndex());
					if (selected == index) {
						closeAll();
						return;
					}
					expandStack(index);
				}
				super.showStack(index);

				dispatchOpenEvent();

				if (index >= 0 && index < menus.size()) {
					app.getGuiManager().setDraggingViews(
							menus.get(index).isViewDraggingMenu(), false);
				}
			}

			@Override
			protected void setStackVisible(int index, boolean visible) {
				if (!visible || !getMenuAt(index).isEmpty()) {
					super.setStackVisible(index, visible);
				}
			}

			@Override
			public void onBrowserEvent(Event event) {
				int eventType = DOM.eventGetType(event);
				Element target = DOM.eventGetTarget(event);
				int index = findDividerIndex(target);
				if (examController.isIdle() && eventType == Event.ONMOUSEOUT) {
					if (index != getSelectedIndex()) {
						getMenuAt(getSelectedIndex()).selectItem(null);
					}
				} else if (eventType == Event.ONCLICK) {
					// check if SignIn was clicked
					// if we are offline, the last item is actually Help
					Widget clicked = index >= 0 ? this.getWidget(index) : null;
					if (clicked instanceof Submenu
							&& ((Submenu) clicked).isEmpty()) {
						((Submenu) clicked).handleHeaderClick();
						app.hideMenu();
						return;
					}
					if (index != -1) {
						showStack(index);
					}
				}
				super.onBrowserEvent(event);
			}

			private void setExpandStyles(int index) {
				Submenu mi = getMenuAt(index);
				mi.getElement().removeClassName("collapse");
				mi.getElement().addClassName("expand");
			}

			private void setCollapseStyles(int index) {
				Submenu mi = getMenuAt(index);
				mi.getElement().removeClassName("expand");
				mi.getElement().addClassName("collapse");
			}

			private void setStackText(int index, boolean expand) {
				if (index < 0 || index >= menuPanel.getWidgetCount()) {
					return;
				}
				// SVGResource img = menuImgs.get(index - step);
				Submenu menu = getMenuAt(index);

				String title = menu.getTitle(app.getLocalization());

				if (menu.isEmpty()) {
					setStackText(index, getHTML(menu), title, expand);
					return;
				}

				String menuText = expand ? getHTMLExpand(menu.getImage(), title)
						: getHTMLCollapse(menu.getImage(), title);

				setStackText(index, menuText, title, expand);

				if (expand) {
					setExpandStyles(index);
				} else {
					setCollapseStyles(index);
				}
			}

			private void expandStack(int index) {
				setStackText(index, false);
			}

			private void collapseStack(int index) {
				setStackText(index, true);
			}

			/**
			 * @param ariaLabel
			 *            for compatibility with AriaStackPanel
			 * @param expanded
			 *            for compatibility with AriaStackPanel
			 */
			public void setStackText(int index, @IsSafeHtml String text, String ariaLabel,
					Boolean expanded) {
				super.setStackText(index, text);
				setAriaLabel(index, ariaLabel, expanded);
			}

			@Override
			public void add(Widget w, @IsSafeHtml String stackText, boolean asHTML) {
				add(w);
				int index = getWidgetCount() - 1;
				setStackText(index, stackText, getMenuAt(index).getTitle(app.getLocalization()),
						null);
				TestHarness.setAttr(w,
						"menu_" + getMenuAt(index).getTitleTranslationKey());
			}

			@Override
			public void reset() {
				collapseStack(getSelectedIndex());
				for (int i = 1; i < menuPanel.getWidgetCount(); i++) {
					getMenuAt(i).selectItem(null);
				}
			}
		};

		menuPanel.addDomHandler(this, KeyDownEvent.getType());
	}

	@Override
	public void render(boolean online) {
		if (!hasLoginButton()) {
			return;
		}
		removeUserSignIn();
		if (online && app.getLoginOperation().isLoggedIn()) {
			addUserMenu();
		} else if (online) {
			addSignInMenu();
		}
	}

	private boolean hasLoginButton() {
		return app.enableOnlineFileFeatures();
	}

	private void removeUserSignIn() {
		if (this.signInMenu != null) {
			menus.remove(signInMenu);
			menuPanel.removeStack(signInMenu);
		}
		if (this.userMenu != null) {
			menus.remove(userMenu);
			menuPanel.removeStack(userMenu);
		}
	}

	/**
	 * @param subMenu
	 *            submenu
	 * @return HTML for menu heading
	 */
	String getHTML(Submenu subMenu) {
		return "<img src=\"" + subMenu.getImage().getSafeUri().asString()
				+ "\" draggable=\"false\"><span>" + subMenu.getTitle(app.getLocalization())
				+ "</span>";
	}

	/**
	 * @param img
	 *            - menu item image
	 * @param s
	 *            - menu item title
	 * @return html code for an expandable menu item
	 */
	String getHTMLExpand(SVGResource img, String s) {
		return "<img src=\"" + img.getSafeUri().asString()
				+ "\" draggable=\"false\" aria-hidden=\"true\"><span>"
				+ app.getLocalization().getMenu(s) + "</span>" + "<img src=\""
				+ MaterialDesignResources.INSTANCE.expand_black().getSafeUri().asString()
				+ "\" class=\"expandImg\" draggable=\"false\""
				+ " aria-label=\"expand\" role=\"button\">";
	}

	/**
	 * @param img
	 *            - menu item img
	 * @param s
	 *            - menu item title
	 * @return html code for menu item
	 */
	String getHTMLCollapse(SVGResource img, String s) {
		return "<img src=\"" + (img == null ? "-" : img.getSafeUri().asString())
				+ "\" draggable=\"false\" aria-hidden=\"true\"><span>"
				+ app.getLocalization().getMenu(s) + "</span>" + "<img src=\""
				+ MaterialDesignResources.INSTANCE.collapse_black().getSafeUri().asString()
				+ "\" class=\"collapseImg\" draggable=\"false\""
				+ " aria-label=\"collapse\" role=\"button\">";
	}

	private String getExpandCollapseHTML(Submenu submenu) {
		String title = submenu.getTitle(app.getLocalization());
		return getHTMLExpand(submenu.getImage(), title);
	}

	/**
	 * Update all submenus that depend on file content
	 */
	public void updateMenubar() {
		for (Submenu submenu : menus) {
			submenu.update();
		}
	}

	/**
	 * Update on selection change
	 */
	public void updateSelection() {
		for (Submenu menu : menus) {
			// TODO use listener
			if (menu instanceof EditMenuW) {
				((EditMenuW) menu).invalidate();
			}
		}
	}

	/**
	 * Focus a submenu (the last selected one if possible)
	 */
	public void focus() {
		int index = Math.max(menuPanel.getSelectedIndex(), 0);
		if (this.menus.get(index) != null) {
			this.menus.get(index).focus();
		}
	}

	/**
	 * @param w
	 *            submenu
	 * @param left
	 *            arrow direction
	 */
	public static void addSubmenuArrow(AriaMenuBar w, boolean left) {
		w.addStyleName(left ? "subMenuRightSide" : "subMenuLeftSide");
		FlowPanel arrowSubmenu = new FlowPanel();
		arrowSubmenu.addStyleName("arrowSubmenu");
		NoDragImage arrow = left
				? new NoDragImage(
						GuiResources.INSTANCE.arrow_submenu_left().getSafeUri().asString())
				: new NoDragImage(
						GuiResources.INSTANCE.arrow_submenu_right().getSafeUri().asString());
		arrowSubmenu.add(arrow);
		w.getElement().appendChild(arrowSubmenu.getElement());
	}

	/**
	 * @param icon - icon
	 * @return image of icon
	 */
	public static Element getImage(ResourcePrototype icon) {
		NoDragImage img = new NoDragImage(icon, 20, 20);
		return img.getElement();
	}

	@Override
	public void renderEvent(final BaseEvent event) {
		if (!hasLoginButton()) {
			return;
		}

		if (event instanceof LoginEvent && ((LoginEvent) event).isSuccessful()) {
			removeUserSignIn();
			addUserMenu();
			this.userMenu.setVisible(false);
		} else if (event instanceof LogOutEvent) {
			removeUserSignIn();
			addSignInMenu();
			this.signInMenu.setVisible(false);
		}
	}

	private void addSignInMenu() {
		menus.add(signInMenu);
		addSubmenu(signInMenu);
	}

	private void addSimple(Submenu submenu) {
		this.menuPanel.add(submenu, getHTML(submenu), true);
	}

	private void addUserMenu() {
		menus.add(userMenu);
		addSubmenu(userMenu);
	}

	/**
	 * Inform client listener about opening the menu
	 */
	public void dispatchOpenEvent() {
		if (menuPanel != null) {
			int index = menuPanel.getSelectedIndex();
			if (app.isUnbundledOrWhiteboard()) {
				index--;
			}
			if (index < 0 || index > menus.size() - 1) {
				index = 0;
			}
			app.dispatchEvent(new org.geogebra.common.plugin.Event(EventType.OPEN_MENU, null,
					menus.get(index).getMenuTitle()));
		}
	}

	/**
	 * Focuses the first item of the Main Menu
	 */
	public void focusFirst() {
		if (menuPanel.getSelectedIndex() != 0) {
			menuPanel.showStack(0);
			getMenuAt(0).focus();
		}
	}

	private void focusStack(int index) {
		if (menuPanel != null) {
			menuPanel.focusHeader(index);
		}
	}

	/**
	 * Selects the next item of the menu.
	 *
	 * @param menu
	 *            to select in.
	 * @return true if the next item is not the same as it is already selected.
	 *
	 */
	boolean selectNextItem(Submenu menu) {
		if (menu == null) {
			return false;
		}

		if (menu.isLastItemSelected() || menu.isEmpty() || menuPanel.isCollapsed()) {
			menu.selectItem(null);
			int nextIdx = menuPanel.getLastSelectedIndex() + 1;
			if (nextIdx < menuPanel.getWidgetCount()) {
				menuPanel.showStack(nextIdx);
				focusStack(nextIdx);
			} else {
				return false;
			}
		} else {
			menu.moveSelectionDown();
		}
		return true;
	}

	/**
	 * Selects the previous item of the menu.
	 *
	 * @param menu
	 *            to select in.
	 * @return true if the previous item is not the same as it is already
	 *         selected.
	 */
	boolean selectPreviousItem(Submenu menu) {
		if (menu == null) {
			return false;
		}

		if (menu.isFirstItemSelected() || menu.isEmpty() || menuPanel.isCollapsed()) {
			menu.selectItem(null);
			int prevIdx = menuPanel.getLastSelectedIndex() - 1;
			if (prevIdx != -1) {
				menuPanel.showStack(prevIdx);
				focusStack(prevIdx);
			} else {
				return false;
			}
		} else {
			menu.moveSelectionUp();
		}
		return true;
	}

	/**
	 * Gets the menu at given id from StackPanel
	 *
	 * @param stackIdx
	 *            the index
	 * @return the widget at given index if it is Submenu instance or null
	 *         otherwise.
	 */
	Submenu getMenuAt(int stackIdx) {
		int idx = stackIdx > -1 && stackIdx < menuPanel.getWidgetCount() ? stackIdx : 0;
		Widget w = menuPanel.getWidget(idx);
		if (w instanceof Submenu) {
			return (Submenu) w;
		}
		return null;
	}

	@Override
	public void onKeyDown(KeyDownEvent event) {
		int key = event.getNativeKeyCode();
		Submenu mi = getMenuAt(menuPanel.getLastSelectedIndex());

		if (key == KeyCodes.KEY_UP) {
			selectPreviousItem(mi);
		} else if (key == KeyCodes.KEY_DOWN) {
			selectNextItem(mi);
		}
	}

	@Override
	public void setVisible(boolean visible) {
		if (!visible) {
			menuPanel.reset();
		}
		super.setVisible(visible);
	}

	/**
	 * @param imgRes
	 *            image, can be null (in that case no icon HTML is included)
	 * @param name
	 *            localized text
	 * @return HTML
	 */
	public static AriaMenuItem getMenuBarItem(final ResourcePrototype imgRes,
			String name, Scheduler.ScheduledCommand cmd) {
		return new AriaMenuItem(name, imgRes, cmd);
	}

	/**
	 * @param imgRes
	 *            image, can be null (in that case no icon HTML is included)
	 * @param name
	 *            localized text
	 * @return HTML
	 */
	public static AriaMenuItem getMenuBarItem(final IconSpec imgRes,
			String name, Scheduler.ScheduledCommand cmd) {
		return new AriaMenuItem(name, cmd, imgRes);
	}

	/**
	 * @param name
	 *            manu item localized name
	 * @return item HTML
	 */
	public static AriaMenuItem getMenuBarHtmlEmptyIcon(String name,
			Scheduler.ScheduledCommand cmd) {
		return MainMenu.getMenuBarItem(AppResources.INSTANCE.empty(), name, cmd);
	}
}
