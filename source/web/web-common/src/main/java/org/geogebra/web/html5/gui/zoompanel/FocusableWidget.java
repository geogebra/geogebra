/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.web.html5.gui.zoompanel;

import java.util.List;

import javax.annotation.CheckForNull;

import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.gui.AccessibilityGroup;
import org.geogebra.common.gui.FocusableComponent;
import org.geogebra.gwtutil.NavigatorUtil;
import org.geogebra.web.html5.gui.accessibility.HasFocus;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.gui.util.Dom;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.dom.client.Element;
import org.gwtproject.user.client.ui.Widget;

import elemental2.dom.CSSStyleDeclaration;
import elemental2.dom.DomGlobal;
import elemental2.dom.ViewCSS;
import jsinterop.base.Js;

public class FocusableWidget implements FocusableComponent {

	private final List<Widget> btns;
	private final AccessibilityGroup accessibilityGroup;
	private final @CheckForNull AccessibilityGroup.ViewControlId subgroup;

	/**
	 * @param btns button
	 * @param accessibilityGroup accessibility group
	 * @param subgroup subgroup
	 */
	public FocusableWidget(AccessibilityGroup accessibilityGroup,
			@CheckForNull AccessibilityGroup.ViewControlId subgroup, Widget... btns) {
		this(accessibilityGroup, subgroup, List.of(btns));
	}

	/**
	 * @param btns button
	 * @param accessibilityGroup accessibility group
	 * @param subgroup subgroup
	 */
	public FocusableWidget(AccessibilityGroup accessibilityGroup,
			@CheckForNull AccessibilityGroup.ViewControlId subgroup, List<Widget> btns) {
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
		Widget btn;
		if (reverse) {
			btn = getLastFocusableWidget();
		} else {
			btn = getFirstFocusableWidget();
		}
		if (btn != null) {
			focus(btn);
			return true;
		}
		return false;
	}

	private CSSStyleDeclaration getComputedStyle(Element element) {
		ViewCSS view = Js.cast(DomGlobal.window);
		return view.getComputedStyle(Js.uncheckedCast(element));
	}

	private boolean notAriaHiddenOrAriaDisabled(Widget btn) {
		return !"true".equals(btn.getElement().getAttribute("aria-hidden"))
				&& !"true".equals(btn.getElement().getAttribute("aria-disabled"));
	}

	private boolean isButtonNotHidden(Widget btn) {
		return !btn.getElement().hasClassName("hideButton")
				&& !btn.getElement().getStyle().getVisibility().equals("hidden")
				&& !getComputedStyle(btn.getElement()).visibility.equals("hidden")
				&& !btn.getElement().getStyle().getDisplay().equals("none")
				&& !getComputedStyle(btn.getElement()).display.equals("none");
	}

	private boolean isParentVisible(Widget btn) {
		return btn.getParent() == null || isButtonNotHidden(btn.getParent());
	}

	private boolean isVisibleAndFocusable(Widget btn) {
		return Dom.isAttachedAndVisible(btn)
				&& notAriaHiddenOrAriaDisabled(btn)
				&& isButtonNotHidden(btn)
				&& isParentVisible(btn)
				&& isFocusable(btn);
	}

	private @CheckForNull Widget getFirstFocusableWidget() {
		for (Widget w : btns) {
			if (isVisibleAndFocusable(w)) {
				return w;
			}
		}
		return null;
	}

	private @CheckForNull Widget getLastFocusableWidget() {
		for (int i = btns.size() - 1; i >= 0; i--) {
			Widget widget = btns.get(i);
			if (isVisibleAndFocusable(widget)) {
				return widget;
			}
		}
		return null;
	}

	protected void focus(Widget btn) {
		if (btn instanceof HasFocus focusable) {
			focusable.focus();
		} else {
			btn.getElement().focus();
		}
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
		while (index >= 0 && index < btns.size()) {
			if (isVisibleAndFocusable(btns.get(index))) {
				focus(btns.get(index));
				return true;
			}
			index += offset;
		}
		return false;
	}

	private boolean isFocusable(Widget btn) {
		return btn.getElement().getTabIndex() >= 0
				|| (btn instanceof HasFocus);
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
	public @CheckForNull AccessibilityGroup.ViewControlId getViewControlId() {
		return subgroup;
	}

}
