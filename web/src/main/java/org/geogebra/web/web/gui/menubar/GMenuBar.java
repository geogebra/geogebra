package org.geogebra.web.web.gui.menubar;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.main.Feature;
import org.geogebra.web.html5.gui.TabHandler;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.PopupPanel;

/**
 * Menubar with some extra functionality (popups, event logging)
 *
 */
public class GMenuBar extends AriaMenuBar {
	private int separators = 0;
	private String menuTitle;
	private AppW app;
	private List<TabHandler> tabHandlers;

	/**
	 * @param menuTitle
	 *            title for event logger
	 * @param app
	 *            parent app
	 */
	public GMenuBar(String menuTitle, AppW app) {
		super();
		this.menuTitle = menuTitle;
		this.app = app;
		tabHandlers = new ArrayList<TabHandler>();
	}

	/**
	 * Adds a handler for TAB key.
	 * 
	 * @param handler
	 *            to add.
	 */
	public void addTabHandler(TabHandler handler) {
		tabHandlers.add(handler);
	}

	/**
	 * @return whether first item is selected
	 */
	public boolean isFirstItemSelected(){
		return this.getItemIndex(this.getSelectedItem()) == 0;
	}

	/**
	 * @return whether last item is selected
	 */
	public boolean isLastItemSelected() {
		return this.getItemIndex(this.getSelectedItem()) == this.getItems().size() + separators - 1;
		
    }
	
	@Override
	public void addSeparator() {
		if (app.isUnbundledOrWhiteboard()) {
			return;
		}
		this.separators  ++;
		super.addSeparator();
	}

	/**
	 * As far as GWT is buggy in the implementation of this, it is necessary to
	 * have a method to correct that
	 * 
	 * @param itemtext
	 *            heading text
	 * @param textishtml
	 *            whether to use text as HTML
	 * @param submenupopup
	 *            popup menu
	 *
	 * @param subleft
	 *            specifies if submenu should open to the left.
	 * @return MenuItem
	 */
	public MenuItem addItem(String itemtext, boolean textishtml,
			final MenuBar submenupopup, final boolean subleft) {

		// this works, but it is still different from addItem in
		// not following mouse movement, only following mouse clicks, etc
		final Object[] ait = new Object[2];
		ait[1] = null;// means the popup assigned to this MenuItem
		ait[0] = addItem(itemtext, textishtml, new ScheduledCommand() {
			@Override
			public void execute() {

				if (ait[0] != null) {
					selectItem((MenuItem) ait[0]);
					// Note that another menu item might have an open popup
					// here, with a different submenupopup, and that disappears
					// because its popuppanel is modal, but its ait[1]
					// variable still remains filled, so it is necessary to
					// check it here, and make it null if necessary
					if ((ait[1] != null) && (ait[1] instanceof PopupPanel)
					        && !((PopupPanel) ait[1]).isShowing()
					        && !((PopupPanel) ait[1]).isAttached()) {
						// but here we should exclude the case where the same
						// menuitem is clicked as the present one!!
						ait[1] = null;
					}

					if (ait[1] == null) {
						// popuppanel still not present
						final PopupPanel pp = new PopupPanel(true, false);
						pp.addAutoHidePartner(((MenuItem)ait[0]).getElement());
						submenupopup.addStyleName(subleft ? "subMenuLeftSide2"
								: "subMenuRightSide2");
						submenupopup.selectItem(null);
						if (subleft) {
							pp.addStyleName("subMenuLeftSidePopup");
						} else {
							pp.addStyleName(
									"GeoGebraMenuBar.subMenuRightSidePopup");
						}

						if (getApp().isUnbundled()) {
							pp.addStyleName("floatingSubMenu");
						}
						pp.add(submenupopup);
						MenuItem mi0 = (MenuItem) ait[0];
						int left = getAbsoluteHorizontalPos(mi0, subleft);
						int top = getAbsoluteTop(mi0);

						pp.setPopupPosition(left, top);

						if (submenupopup instanceof RadioButtonMenuBarW) {
							((RadioButtonMenuBarW) submenupopup)
									.registerItemSideEffect(
											new Scheduler.ScheduledCommand() {
												@Override
												public void execute() {
													// this should only run if
													// some item
													// is selected and clicked
													ait[1] = null;
													submenupopup
															.selectItem(null);
													pp.hide();
												}
											});
						}

						// TODO: more difficult to solve autoOpen
						// sadly, the following thing can only work together
						// with submenus created by the other method, in theory
						// parentMenu.setAutoOpen(true);

						ait[1] = pp;
						pp.show();
					} else {
						submenupopup.selectItem(null);

						if (ait[1] instanceof PopupPanel) {
							((PopupPanel) ait[1]).hide();
						}

						// if popuppanel is present, it will be hidden
						// due to autoHide, and no new one is created instead
						// let's forget about it
						ait[1] = null;
					}
				}
			}
		});

		return (MenuItem) ait[0];
	}

	/**
	 * @param itemtext
	 *            item content
	 * @param textishtml
	 *            whether to treat text as html
	 * @param submenupopup
	 *            submenu
	 * @return the menu item
	 */
	public MenuItem addItem(String itemtext, boolean textishtml,
			final MenuBar submenupopup) {
		return addItem(itemtext, textishtml, submenupopup, true);
	}

	/**
	 * @return title for event logger
	 */
	public String getMenuTitle() {
		return menuTitle;
	}

	/**
	 * @return application
	 */
	protected AppW getApp() {
		return app;
	}

	@Override
	public void onBrowserEvent(Event event) {
		if (!app.has(Feature.TAB_ON_MENU)) {
			super.onBrowserEvent(event);
			return;
		}

		int eventGetType = DOM.eventGetType(event);
		if (eventGetType == Event.ONKEYDOWN) {
			int keyCode = event.getKeyCode();
			if (keyCode == KeyCodes.KEY_TAB && hasTabHandlers()) {
				event.preventDefault();
				event.stopPropagation();
				for (TabHandler handler : tabHandlers) {
					handler.onTab(this, event.getShiftKey());
				}
				return;
			}
		}
		if (eventGetType != Event.ONFOCUS) {
			super.onBrowserEvent(event);
		}

	}

	private boolean hasTabHandlers() {
		return !tabHandlers.isEmpty();
	}

	/**
	 * 
	 * @return true if menu has no items.
	 */
	public boolean isEmpty() {
		return getItems().isEmpty();
	}

	/**
	 * Selects the last menu item.
	 */
	public void selectLastItem() {
		List<MenuItem> list = getItems();
		if (!list.isEmpty()) {
			selectItem(list.get(list.size() - 1));
		}
	}
}
