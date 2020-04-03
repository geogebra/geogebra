package org.geogebra.web.html5.gui.zoompanel;

import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.gui.AccessibilityGroup;
import org.geogebra.common.gui.MayHaveFocus;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.Dom;

import com.google.gwt.user.client.ui.Widget;

public class FocusableWidget implements MayHaveFocus {

	private final Widget[] btns;
	private final AccessibilityGroup accessibilityGroup;
	private final int viewId;

	/**
	 * @param btns button
	 * @param accessibilityGroup accessibility group
	 * @param viewId view ID
	 */
	public FocusableWidget(AccessibilityGroup accessibilityGroup, int viewId, Widget... btns) {
		this.btns = btns;
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
				focus(btns[btns.length - 1]);
			} else {
				focus(btn);
			}
			return true;
		}

		return false;
	}

	protected void focus(Widget btn) {
		btn.getElement().focus();
		btn.addStyleName("keyboardFocus");
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
			focus(btns[index]);
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
		for (Widget btn: btns) {
			final Widget current = btn;
			ClickStartHandler.init(btn, new ClickStartHandler() {
				@Override
				public void onClickStart(int x, int y, PointerEventType type) {
					current.removeStyleName("keyboardFocus");
				}
			});
		}
	}

	@Override
	public int getViewId() {
		return viewId;
	}

}
