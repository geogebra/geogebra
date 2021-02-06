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
 *
 * @author laszlo
 */
public class ComponentDropDownPopup {

	// for checking item position. It is 0 in normal case.
	private static final int OFFSET_X = 0;
	private GPopupMenuW menu;
	private int selectedIndex;
	private Widget anchor;
	private int itemHeight;
	private AppW app;

	private enum RelativePosition {
		HIGH, CENTER, LOW
	}

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
		menu.setMenuShown(true);
		restoreHeight();
		RelativePosition pos = getRelativePosition();
		if (pos == RelativePosition.HIGH) {
			showHigh();
		} else if (pos == RelativePosition.CENTER) {
			showCenter();
		} else if (pos == RelativePosition.LOW) {
			showLow();
		}
	}

	private RelativePosition getRelativePosition() {
		int itemTop = getSelectedItemTop();
		int top = getTop();
		if (itemTop < top) {
			return RelativePosition.HIGH;
		} else if (getPopupHeightRemaining() < getMaxHeight() / 2) {
			return RelativePosition.LOW;
		}
		return RelativePosition.CENTER;
	}

	private void showHigh() {
		int top = getTop() - getSelectedItemTop();
		double spaceToBottom = app.getHeight() - top - 3d * itemHeight / 2;
		if (spaceToBottom < getPopupHeightRemaining()) {
			setMaxHeightInPx(spaceToBottom);
		} else {
			setHeightInPx(getAllItemsHeight());
		}
		menu.showAtPoint(getLeft(), top);
	}

	private void showCenter() {
		int h2 = Math.min(getPopupHeightRemaining(), getTop() - itemHeight);
		setMaxHeightInPx(2 * h2);
		openAndScrollTo(getTop() - h2, getSelectedItemTop() - h2);
	}

	private void showLow() {
		int itemTop = getSelectedItemTop();
		int top = getTop();
		int h2 = getMaxHeight();
		int diff = getPopupHeightRemaining();
		if (diff < h2) {
			if (top < getMaxHeight() + diff) {
				setHeightInPx(top);
			}
			openAndScrollTo(diff, itemTop);
		}
	}

	private int getPopupHeightRemaining() {
		return getAllItemsHeight() - getSelectedItemTop();
	}

	private int getSelectedItemTop() {
		return getSelectedIndex() * itemHeight;
	}

	private int getLeft() {
		return anchor.getAbsoluteLeft() + OFFSET_X;
	}

	private int getTop() {
		return anchor.getAbsoluteTop() - anchor.getOffsetHeight() / 2
				- (int) app.getAbsTop();
	}

	private int getMaxHeight() {
		return (int) (app.getHeight());
	}

	private void openAndScrollTo(int top, int position) {
		menu.showAtPoint(getLeft(), top);
		menu.getPopupPanel().getElement().setScrollTop(position);
	}

	private void setHeightInPx(int height) {
		getStyle().setHeight(height, Unit.PX);
	}

	private void setMaxHeightInPx(double height) {
		getStyle().setProperty("maxHeight", height, Unit.PX);
	}

	private void restoreHeight() {
		setHeightInPx(getMaxHeight());
		getStyle().setProperty("maxHeight", "");
	}

	private Style getStyle() {
		return menu.getPopupPanel().getElement().getStyle();
	}

	private int getAllItemsHeight() {
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
}