package org.geogebra.web.full.gui.menubar;

import java.util.List;

import org.geogebra.web.html5.gui.GPopupPanel;
import org.geogebra.web.html5.gui.menu.AriaMenuBar;
import org.geogebra.web.html5.gui.menu.AriaMenuItem;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.core.client.Scheduler;

/**
 * Menubar with some extra functionality (popups, event logging)
 *
 */
public class GMenuBar extends AriaMenuBar {
	private int separators = 0;
	private final String menuTitle;
	private final AppW app;

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
		setHandleArrows(false);
	}

	/**
	 * @return whether first item is selected
	 */
	public boolean isFirstItemSelected() {
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
		this.separators  ++;
		super.addSeparator();
	}

	/**
	 * As far as GWT is buggy in the implementation of this, it is necessary to
	 * have a method to correct that
	 * 
	 * @param parent
	 *            top level element
	 * @param showLeft
	 *            specifies if submenu should open to the left.
	 * @return MenuItem
	 */
	public Scheduler.ScheduledCommand getSubmenuCommand(AriaMenuItem parent,
			final boolean showLeft) {

		return new Scheduler.ScheduledCommand() {
			private GPopupPanel popup;

			@Override
			public void execute() {
				selectItem(parent);
				// Note that another menu item might have an open popup
				// here, with a different submenupopup, and that disappears
				// because its popuppanel is modal, but its ait[1]
				// variable still remains filled, so it is necessary to
				// check it here, and make it null if necessary
				if (popup != null && !popup.isShowing() && !popup.isAttached()) {
					// but here we should exclude the case where the same
					// menuitem is clicked as the present one!!
					popup = null;
				}
				AriaMenuBar submenu = parent.getSubMenu();
				if (popup == null) {
					// popuppanel still not present
					popup = new GPopupPanel(true, false,
							app.getAppletFrame(), app);
					popup.addAutoHidePartner(
							parent.getElement());

					submenu.addStyleName(showLeft ? "subMenuLeftSide2"
							: "subMenuRightSide2");
					submenu.selectItem(null);
					if (showLeft) {
						popup.addStyleName("subMenuLeftSidePopup");
					} else {
						popup.addStyleName(
								"GeoGebraMenuBar.subMenuRightSidePopup");
					}

					popup.add(submenu);
					int left = (int) ((getAbsoluteHorizontalPos(parent,
							showLeft) - (int) app.getAbsLeft())
							/ app.getGeoGebraElement().getScaleX());
					int top = (int) ((parent.getAbsoluteTop()
							- app.getAbsTop())
							/ app.getGeoGebraElement().getScaleY());

					popup.setPopupPosition(left, top);
					popup.show();
				} else {
					submenu.selectItem(null);
					popup.hide();
					popup = null;
				}
			}
		};
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
	@Override
	protected AppW getApp() {
		return app;
	}

	/**
	 * 
	 * @return true if menu has no items.
	 */
	public boolean isEmpty() {
		return getItems().isEmpty();
	}

	/**
	 * Selects the first menu item.
	 */
	public void selectFirstItem() {
		List<AriaMenuItem> list = getItems();
		if (!list.isEmpty()) {
			selectItem(list.get(0));
		}
	}

	/**
	 * Selects the last menu item.
	 */
	@Override
	public void selectLastItem() {
		List<AriaMenuItem> list = getItems();
		if (!list.isEmpty()) {
			selectItem(list.get(list.size() - 1));
		}
	}

}
