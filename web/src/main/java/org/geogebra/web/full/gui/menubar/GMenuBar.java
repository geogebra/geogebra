package org.geogebra.web.full.gui.menubar;

import java.util.List;

import org.geogebra.web.html5.gui.GPopupPanel;
import org.geogebra.web.html5.gui.util.AriaMenuBar;
import org.geogebra.web.html5.gui.util.AriaMenuItem;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;

/**
 * Menubar with some extra functionality (popups, event logging)
 *
 */
public class GMenuBar extends AriaMenuBar {
	private int separators = 0;
	private String menuTitle;
	private AppW app;

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
	public AriaMenuItem addItem(String itemtext, boolean textishtml,
			final AriaMenuBar submenupopup, final boolean subleft) {

		// this works, but it is still different from addItem in
		// not following mouse movement, only following mouse clicks, etc
		final Object[] ait = new Object[2];
		final AppW app1 = app;
		ait[1] = null; // means the popup assigned to this MenuItem
		ait[0] = addItem(itemtext, textishtml, new ScheduledCommand() {
			@Override
			public void execute() {

				if (ait[0] != null) {
					selectItem((AriaMenuItem) ait[0]);
					// Note that another menu item might have an open popup
					// here, with a different submenupopup, and that disappears
					// because its popuppanel is modal, but its ait[1]
					// variable still remains filled, so it is necessary to
					// check it here, and make it null if necessary
					if ((ait[1] != null) && (ait[1] instanceof GPopupPanel)
							&& !((GPopupPanel) ait[1]).isShowing()
							&& !((GPopupPanel) ait[1]).isAttached()) {
						// but here we should exclude the case where the same
						// menuitem is clicked as the present one!!
						ait[1] = null;
					}

					if (ait[1] == null) {
						// popuppanel still not present
						final GPopupPanel pp = new GPopupPanel(true, false,
								app1.getPanel(), app1);
						pp.addAutoHidePartner(
								((AriaMenuItem) ait[0]).getElement());
						submenupopup.addStyleName(subleft ? "subMenuLeftSide2"
								: "subMenuRightSide2");
						submenupopup.selectItem(null);
						if (subleft) {
							pp.addStyleName("subMenuLeftSidePopup");
						} else {
							pp.addStyleName(
									"GeoGebraMenuBar.subMenuRightSidePopup");
						}

						pp.add(submenupopup);
						AriaMenuItem mi0 = (AriaMenuItem) ait[0];
						int left = (int) ((getAbsoluteHorizontalPos(mi0,
								subleft) - (int) app1.getAbsLeft())
								/ app1.getGeoGebraElement().getScaleX());
						int top = (int) ((mi0.getAbsoluteTop()
								- app1.getAbsTop())
								/ app1.getGeoGebraElement().getScaleY());

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

						if (ait[1] instanceof GPopupPanel) {
							((GPopupPanel) ait[1]).hide();
						}

						// if popuppanel is present, it will be hidden
						// due to autoHide, and no new one is created instead
						// let's forget about it
						ait[1] = null;
					}
				}
			}
		});

		return (AriaMenuItem) ait[0];
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

	/**
	 * focus menu in a deferred way.
	 */
	public void focusDeferred() {
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				getElement().focus();
			}
		});
	}
}
