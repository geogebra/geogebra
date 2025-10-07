package org.geogebra.web.full.gui.components;

import org.geogebra.web.full.javax.swing.GPopupMenuW;
import org.geogebra.web.html5.gui.menu.AriaMenuItem;
import org.geogebra.web.html5.gui.util.AriaHelper;
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
	 * @param labelKey label
	 * @param anchor to align the selected item.
	 */
	public ComponentDropDownPopup(AppW app, int itemHeight, Widget anchor, String labelKey,
			Runnable onClose) {
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
		setAccessibilityProperties(labelKey);
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
		menu.getPopupMenu().unselect();
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

	private void setAccessibilityProperties(String labelKey) {
		AriaHelper.setRole(menu.getPopupPanel(), "listbox");
		AriaHelper.setLabel(menu.getPopupPanel(), app.getLocalization().getMenu(labelKey));
		menu.getPopupPanel().setMayMoveFocus(true);
	}
}