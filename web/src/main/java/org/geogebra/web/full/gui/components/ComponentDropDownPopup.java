package org.geogebra.web.full.gui.components;

import org.geogebra.web.full.javax.swing.GPopupMenuW;
import org.geogebra.web.html5.gui.util.AriaMenuItem;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Widget;

/**
 * Popup menu following the Material Design.
 */
public class ComponentDropDownPopup {
	private static final int OFFSET_X = 0;
	private static final int POPUP_PADDING = 8;
	private static final int MARGIN_FROM_SCREEN = 32;
	private GPopupMenuW menu;
	private int selectedIndex;
	private Widget anchor;
	private int itemHeight;
	private AppW app;

	/**
	 *
	 * @param app        {@link AppW}
	 * @param itemHeight Height of an item in list
	 * @param anchor     to align the selected item.
	 */
	public ComponentDropDownPopup(AppW app, int itemHeight, Widget anchor) {
		this.app = app;
		this.itemHeight = itemHeight;
		this.anchor = anchor;
		menu = new GPopupMenuW(app);
		menu.getPopupPanel().addStyleName("dropDownPopup");
		app.registerAutoclosePopup(menu.getPopupPanel());
	}

	/**
	 *
	 * @param item to add
	 */
	public void addItem(AriaMenuItem item) {
		menu.addItem(item);
	}

	/**
	 * @param element
	 *            element where clicks should not collapse the selection
	 */
	public void addAutoHidePartner(Element element) {
		menu.getPopupPanel().addAutoHidePartner(element);
	}

	/**
	 *
	 * @return the index of the selected item.
	 */
	public int getSelectedIndex() {
		return selectedIndex;
	}

	/**
	 * Set item selected at the given index.
	 *
	 * @param index to select.
	 */
	public void setSelectedIndex(int index) {
		this.selectedIndex = index;
	}

	/**
	 * Opens DropDown at the top of the widget positioning selected item at the
	 * center.
	 */
	void show() {
		int popupTop = getAnchorTop() - getSelectedItemTop();
		int popupTopWithMargin = Math.max(popupTop, MARGIN_FROM_SCREEN);
		int appBottom = (int) (app.getAbsTop() + app.getHeight());

		if (appBottom <= popupTopWithMargin + 3 * itemHeight + MARGIN_FROM_SCREEN) {
			// not enough space for showing 3 items on bottom
			int spaceOnScreen = (int) app.getHeight() - 2 * MARGIN_FROM_SCREEN - 2 * POPUP_PADDING;
			int popupHeightAdjust = getPopupHeight() < spaceOnScreen
					? getPopupHeight() : spaceOnScreen;
			int popupTopAdjust = getPopupHeight() < spaceOnScreen
					? appBottom - getPopupHeight() - MARGIN_FROM_SCREEN : MARGIN_FROM_SCREEN;
			// if less space than popup height, show popup on top with app height
			// otherwise popup with full height aligned to the bottom
			menu.showAtPoint(getLeft(), popupTopAdjust);
			setHeightInPx(popupHeightAdjust);
		} else {
			menu.showAtPoint(getLeft(), popupTopWithMargin);
			if (appBottom < popupTopWithMargin + getPopupHeight()) {
				// popup bottom overflow, use available space and make scrollable
				setHeightInPx(
						appBottom - popupTopWithMargin - MARGIN_FROM_SCREEN - 2 * POPUP_PADDING);
				if (popupTop < MARGIN_FROM_SCREEN) {
					// selected item not on screen, scroll popup
					int diffAnchorPopupTop = getAnchorTop() - popupTopWithMargin;
					menu.getPopupPanel().getElement().setScrollTop(getSelectedItemTop()
							- diffAnchorPopupTop);
				}
			}
		}
	}

	private int getSelectedItemTop() {
		return getSelectedIndex() * itemHeight;
	}

	private int getLeft() {
		return anchor.getAbsoluteLeft() + OFFSET_X;
	}

	private int getAnchorTop() {
		// (32 - 20)/2 = 6 handle height difference between label and menu item
		return anchor.getAbsoluteTop() - POPUP_PADDING - 6;
	}

	private void setHeightInPx(int height) {
		getStyle().setHeight(height, Unit.PX);
	}

	private Style getStyle() {
		return menu.getPopupPanel().getElement().getStyle();
	}

	private int getPopupHeight() {
		return menu.getComponentCount() * itemHeight;
	}

	/**
	 * @return whether the dropdown is currently open or not
	 */
	public boolean isOpened() {
		return menu.isMenuShown();
	}

	/**
	 * Hide the material dropdown popup
	 */
	public void close() {
		menu.hideMenu();
	}

	/**
	 * clear menu
	 */
	public void clear() {
		menu.clearItems();
	}
}