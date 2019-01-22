package org.geogebra.web.full.gui.components;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.web.full.javax.swing.GPopupMenuW;
import org.geogebra.web.html5.gui.util.AriaMenuItem;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Widget;

public class MaterialDropDown {
	/**
	 * Popup menu following the Material Design.
	 *
	 * @author laszlo
	 */

	// for checking item position. It is 0 in normal case.
	private static final int OFFSET_X = 0;
	private EuclidianView view;
	private GPopupMenuW menu;
	private int selectedIndex;
	private Widget parent;
	private int itemHeight;

	public MaterialDropDown(AppW app, int itemHeight, Widget parent) {
		this.itemHeight = itemHeight;
		this.parent = parent;
		this.view = app.getActiveEuclidianView();
		menu = new GPopupMenuW(app);
		menu.getPopupPanel().addStyleName("matMenu");
		menu.getPopupPanel().addStyleName("dropDownPopup");
	}

	public int getLeft() {
		return parent.getAbsoluteLeft() + OFFSET_X;
	}

	public int getTop() {
		return parent.getAbsoluteTop() - parent.getOffsetHeight() / 2 - view.getAbsoluteTop();
	}

	public int getMaxHeight() {
		return view.getHeight() / 2;
	}

	/**
	 * Opens DropDown at the top of the widget positioning selected item at the
	 * center.
	 */
	void open() {
		int itemTop = getSelectedIndex() * itemHeight;
		if (itemTop < getTop()) {
			// everything fits fine, no scrollbar
			menu.showAtPoint(getLeft(), getTop() - itemTop);
		} else {
			openAsScrollable(itemTop);
		}
	}

	private void openAsScrollable(int itemTop) {
		int allHeight = getAllItemsHeight();
		int left = getLeft();
		int top = getTop();
		int h2 = getMaxHeight() / 2;
		int scrollTop = itemTop - h2;
		int diff = allHeight - itemTop;
		setHeight(50, Unit.PCT);
		if (diff < h2) {
			if (diff + h2 < top) {
				// many items, but there is space to go up;
				menu.showAtPoint(left, top - h2 - diff);
			} else {
				// no space: put at 0, shrink and scroll
				setHeight(top, Unit.PX);
				openAndScrollTo(left, 0, itemTop);
			}
		} else {
			// center popup and scroll;
			openAndScrollTo(left, top - h2, scrollTop);
		}

	}

	private void openAndScrollTo(int left, int top, int position) {
		menu.showAtPoint(left, top);
		setVerticalScrollPosition(position);
	}

	private void setVerticalScrollPosition(int pos) {
		menu.getPopupPanel().getElement().setScrollTop(pos);
	}

	private void setHeight(int height, Style.Unit unit) {
		menu.getPopupPanel().setHeight(height + unit.name());
	}

	private int getAllItemsHeight() {
		return menu.getComponentCount() * itemHeight;
	}

	public void addItem(AriaMenuItem item) {
		menu.addItem(item);
	}

	public int getSelectedIndex() {
		return selectedIndex;
	}

	public void setSelectedIndex(int selectedIndex) {
		this.selectedIndex = selectedIndex;
	}
}