package org.geogebra.web.html5.gui.zoompanel;

import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.gui.AccessibilityGroup;
import org.geogebra.common.gui.MayHaveFocus;
import org.geogebra.gwtutil.NavigatorUtil;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.gui.util.Dom;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.user.client.ui.Widget;

public class FocusableWidget implements MayHaveFocus {

	private final Widget[] btns;
	private final AccessibilityGroup accessibilityGroup;
	private final AccessibilityGroup.ViewControlId subgroup;

	/**
	 * @param btns button
	 * @param accessibilityGroup accessibility group
	 * @param subgroup subgroup
	 */
	public FocusableWidget(AccessibilityGroup accessibilityGroup,
						   AccessibilityGroup.ViewControlId subgroup, Widget... btns) {
		this.btns = btns;
		this.accessibilityGroup = accessibilityGroup;
		this.subgroup = subgroup;
		if (NavigatorUtil.isMobile()) {
			int subgroupOrdinal = subgroup == null ? 0 : subgroup.ordinal();
			int maxGroupSize = AccessibilityGroup.ViewControlId.values().length;
			int tabIndex = 1 + accessibilityGroup.ordinal() * maxGroupSize + subgroupOrdinal;
			for (Widget btn: btns) {
				btn.getElement().setTabIndex(tabIndex);
			}
		}
	}

	@Override
	public boolean focusIfVisible(boolean reverse) {
		Widget btn = btns[0];
		if (btn.isVisible() && btn.isAttached()
				&& notAriaHidderOrAriaDisabled(btn)
				&& isButtonNotHidden(btn)
				&& isParentVisible(btn)) {
			if (reverse) {
				focus(btns[btns.length - 1]);
			} else {
				focus(btn);
			}
			return true;
		}

		return false;
	}

	private boolean notAriaHidderOrAriaDisabled(Widget btn) {
		return !"true".equals(btn.getElement().getAttribute("aria-hidden"))
				&& !"true".equals(btn.getElement().getAttribute("aria-disabled"));
	}

	private boolean isButtonNotHidden(Widget btn) {
		return !btn.getElement().hasClassName("hideButton")
				&& !btn.getElement().getStyle().getVisibility().equals("hidden");
	}

	private boolean isParentVisible(Widget btn) {
		if (btn.getParent() == null) {
			return true;
		} else {
			return !btn.getParent().getElement().getStyle().getVisibility().equals("hidden");
		}
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

	/**
	 * Register this component to enable tabbing
	 * @param app Application
	 */
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

	/**
	 * Unregister this component to disable tabbing
	 * @param app Application
	 */
	public void detachFrom(AppW app) {
		app.getAccessibilityManager().unregister(this);
	}

	@Override
	public AccessibilityGroup.ViewControlId getViewControlId() {
		return subgroup;
	}

}
