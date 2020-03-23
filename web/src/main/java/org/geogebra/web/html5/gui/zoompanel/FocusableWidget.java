package org.geogebra.web.html5.gui.zoompanel;

import org.geogebra.common.gui.AccessibilityGroup;
import org.geogebra.common.gui.MayHaveFocus;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.Dom;

import com.google.gwt.user.client.ui.Widget;

public class FocusableWidget implements MayHaveFocus {

	private final Widget[] btns;
	private final AccessibilityGroup accessibilityGroup;
	private final int viewId;

	/**
	 * @param btn button
	 * @param accessibilityGroup accessibility group
	 * @param viewId view ID
	 */
	public FocusableWidget(AccessibilityGroup accessibilityGroup, int viewId, Widget... btn) {
		this.btns = btn;
		this.accessibilityGroup = accessibilityGroup;
		this.viewId = viewId;
	}

	@Override
	public boolean focusIfVisible(boolean reverse) {
		Widget btn = btns[0];
		if (btn.isVisible() && btn.isAttached()
				&& !"true".equals(btn.getElement().getAttribute("aria-hidden"))
				&& !btn.getElement().hasClassName("hideButton")) {
			if (reverse) {
				btns[btns.length -1].getElement().focus();
			} else {
				btn.getElement().focus();
			}
			return true;
		}

		return false;
	}

	@Override
	public boolean hasFocus() {
		return findFocus() >= 0;
	}

	@Override
	public boolean focusNext() {
		return moveFocus(1);
	}

	private boolean moveFocus(int offset) {
		int index = findFocus() + offset;
		if (index >= 0 && index < btns.length) {
			btns[index].getElement().focus();
			return true;
		}
		return false;
	}

	private int findFocus() {
		int index = 0;
		for (Widget btn: btns) {
			if (btn.getElement().isOrHasChild(Dom.getActiveElement())) {
				return index;
			}
			index++;
		}
		return -1;
	}

	@Override
	public boolean focusPrevious() {
		return moveFocus(-1);
	}

	@Override
	public AccessibilityGroup getAccessibilityGroup() {
		return accessibilityGroup;
	}

	public void attachTo(AppW app) {
		app.getAccessibilityManager().register(this);
	}

	@Override
	public int getViewId() {
		return viewId;
	}

}
