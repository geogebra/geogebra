package org.geogebra.web.full.gui.components;

import org.geogebra.web.full.javax.swing.GPopupMenuW;
import org.geogebra.web.html5.gui.menu.AriaMenuItem;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.core.client.Scheduler;
import org.gwtproject.dom.client.Element;
import org.gwtproject.dom.client.Style;
import org.gwtproject.dom.style.shared.Unit;
import org.gwtproject.user.client.ui.Widget;

/**
 * Popup menu following the Material Design.
 */
public class ComponentDropDownPopup {
	private static final int OFFSET_X = 0;
	public static final int POPUP_PADDING = 8;
	public static final int MARGIN_FROM_SCREEN = 32;
	private final GPopupMenuW menu;
	private int selectedIndex;
	private final Widget anchor;
	private final int itemHeight;
	private final AppW app;

	/**
	 * Popup constructor for dropdown and combo-box
	 * @param app {@link AppW}
	 * @param itemHeight Height of an item in list
	 * @param anchor to align the selected item.
	 */
	public ComponentDropDownPopup(AppW app, int itemHeight, Widget anchor, Runnable onClose) {
		this.app = app;
		this.itemHeight = itemHeight;
		this.anchor = anchor;
		menu = new GPopupMenuW(app);
		menu.getPopupPanel().addStyleName("dropDownPopup");
		menu.getPopupPanel().addCloseHandler(event -> {
			menu.getPopupPanel().removeStyleName("show");
			if (onClose != null) {
				onClose.run();
			}
		});
	}

	/**
	 * @param item to add
	 */
	public void addItem(AriaMenuItem item) {
		menu.addItem(item);
	}

	/**
	 * add divider
	 */
	public void addDivider() {
		menu.addVerticalSeparator();
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
	public void positionAsDropDown() {
		int popupTop = (int) (getAnchorTop() - getSelectedItemTop() - app.getAbsTop());
		int popupTopWithMargin = Math.max(popupTop, MARGIN_FROM_SCREEN);
		int appBottom = (int) (app.getAbsTop() + app.getHeight());

		if (appBottom <= popupTopWithMargin + 3 * itemHeight + MARGIN_FROM_SCREEN) {
			// not enough space for showing 3 items on bottom
			int spaceOnScreen = (int) app.getHeight() - 2 * MARGIN_FROM_SCREEN - 2 * POPUP_PADDING;
			int popupHeightAdjust = Math.min(getPopupHeight(), spaceOnScreen);
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
				setHeightInPx((int) (appBottom - popupTopWithMargin - MARGIN_FROM_SCREEN
										- 2 * POPUP_PADDING - app.getAbsTop()));
				if (popupTop < MARGIN_FROM_SCREEN) {
					// selected item not on screen, scroll popup
					int diffAnchorPopupTop = getAnchorTop() - popupTopWithMargin;
					setScrollTop(getSelectedItemTop() - diffAnchorPopupTop);
				}
			} else {
				getStyle().setProperty("height", "");
			}
		}
		Scheduler.get().scheduleDeferred(() -> menu.getPopupPanel().addStyleName("show"));
	}

	/**
	 * Opens drop-down or combo-box at the bottom of the anchor if there is enough space,
	 * on top of the anchor otherwise.
	 */
	public void positionAtBottomAnchor() {
		int anchorBottom = (int) (anchor.getElement().getAbsoluteBottom() - app.getAbsTop());
		int spaceBottom = (int) (app.getHeight() - anchorBottom);
		int spaceTop = (int) (anchor.getElement().getAbsoluteTop() - app.getAbsTop()
				- MARGIN_FROM_SCREEN);
		int minSpaceBottom = 3 * getItemHeight() + MARGIN_FROM_SCREEN + POPUP_PADDING;
		int popupHeight = getPopupHeight();

		if (spaceBottom < minSpaceBottom) {
			showAtTopOfAnchor(popupHeight, spaceTop);
		} else {
			showAtBottomOfAnchor(popupHeight, anchorBottom);
		}
	}

	private void showAtTopOfAnchor(int popupHeight, int spaceTop) {
		int popupTop = popupHeight > spaceTop ? MARGIN_FROM_SCREEN
				: (int) (anchor.asWidget().getAbsoluteTop() - app.getAbsTop() - popupHeight);
		showAtPoint(getLeft(), popupTop);

		if (popupHeight > spaceTop) {
			setHeightAndScrollTop(spaceTop);
		}
	}

	private void showAtBottomOfAnchor(int popupHeight, int bottomPos) {
		showAtPoint(getLeft(), bottomPos);
		int spaceBottom = (int) (app.getHeight() - bottomPos);
		if (popupHeight > spaceBottom) {
			setHeightAndScrollTop(spaceBottom - (MARGIN_FROM_SCREEN + POPUP_PADDING));
		}
	}

	private void setHeightAndScrollTop(int height) {
		setHeightInPx(height);
		setScrollTop(getSelectedItemTop());
	}

	private int getSelectedItemTop() {
		return getSelectedIndex() * itemHeight;
	}

	private int getLeft() {
		return (int) (anchor.getAbsoluteLeft() + OFFSET_X - app.getAbsLeft());
	}

	private int getAnchorTop() {
		// (32 - 20)/2 = 6 handle height difference between label and menu item
		return anchor.getAbsoluteTop() - POPUP_PADDING - 6;
	}

	private void setHeightInPx(int height) {
		getStyle().setHeight(height, Unit.PX);
	}

	/**
	 * @param width - of popup
	 */
	public void setWidthInPx(int width) {
		getStyle().setWidth(width, Unit.PX);
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
		menu.getPopupPanel().removeStyleName("show");
		Scheduler.get().scheduleDeferred(() -> menu.getPopupPanel().hide());
	}

	/**
	 * clear menu
	 */
	public void clear() {
		menu.clearItems();
	}

	/**
	 * show popup at x,y position
	 * @param x - horizontal pos
	 * @param y - vertical pos
	 */
	private void showAtPoint(int x, int  y) {
		menu.showAtPoint(x, y);
		Scheduler.get().scheduleDeferred(() -> {
			menu.getPopupPanel().addStyleName("show");
			menu.getPopupMenu().focus();
		});
	}

	private int getItemHeight() {
		return itemHeight;
	}

	private void setScrollTop(int scrollTop) {
		menu.getPopupPanel().getElement().setScrollTop(scrollTop);
	}
}