package org.geogebra.web.html5.gui.zoompanel;

import org.geogebra.common.gui.AccessibilityGroup;
import org.geogebra.common.gui.MayHaveFocus;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.Dom;

import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Widget;

public class FocusableWidget implements MayHaveFocus {

	private final Widget btn;
	private final AccessibilityGroup accessibilityGroup;
	private final int viewId;

	/**
	 * @param btn button
	 * @param accessibilityGroup accessibility group
	 * @param viewId view ID
	 */
	public FocusableWidget(Widget btn, AccessibilityGroup accessibilityGroup, int viewId) {
		this.btn = btn;
		this.accessibilityGroup = accessibilityGroup;
		this.viewId = viewId;
	}

	@Override
	public boolean focusIfVisible() {
		if (btn.isVisible() && btn.isAttached()
				&& !"true".equals(btn.getElement().getAttribute("aria-hidden"))
				&& !btn.getElement().hasClassName("hideButton")) {
			btn.getElement().focus();
			return true;
		}

		return false;
	}

	@Override
	public boolean hasFocus() {
		return btn.getElement().isOrHasChild(Dom.getActiveElement());
	}

	@Override
	public boolean focusNext() {
		return false; // no subcomponents
	}

	@Override
	public boolean focusPrevious() {
		return false; // no subcomponents
	}

	@Override
	public AccessibilityGroup getAccessibilityGroup() {
		return accessibilityGroup;
	}

	public void attachTo(AppW app) {
		app.getAccessibilityManager().register(this);
	}

	public int getViewId() {
		return viewId;
	}

	public Element getElement() {
		return btn.getElement();
	}
}
