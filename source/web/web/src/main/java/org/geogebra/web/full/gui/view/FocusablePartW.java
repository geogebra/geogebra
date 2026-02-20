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

package org.geogebra.web.full.gui.view;

import org.geogebra.common.gui.AccessibilityManagerInterface;
import org.geogebra.common.gui.compositefocus.FocusablePart;
import org.geogebra.web.full.gui.TextFieldFocusablePart;
import org.geogebra.web.html5.gui.inputfield.AutoCompleteTextFieldW;
import org.geogebra.web.html5.gui.util.AriaHelper;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.gwtproject.user.client.ui.Widget;

/**
 * Web-specific base class for focusable parts used in composite focus traversal.
 *
 * <p>This class wraps a {@link Widget} and provides a stable focus key used to
 * preserve logical selection across UI rebuilds. Subclasses define how focus
 * and blur are applied (synthetic vs native browser focus).</p>
 *
 * <p>All wrapped widgets are marked with a common CSS class to enable
 * consistent visual focus styling.</p>
 */
public class FocusablePartW implements FocusablePart {
	private final Widget widget;
	private final String focusKey;
	private final String accessibleLabel;

	/**
	 * Creates a focusable part for the given widget.
	 * @param widget the underlying widget to be focused
	 * @param focusKey stable semantic key identifying this part
	 * @param accessibleLabel the aria label for the widget
	 */
	public FocusablePartW(Widget widget, String focusKey, String accessibleLabel) {
		this.widget = widget;
		this.focusKey = focusKey;
		this.accessibleLabel = accessibleLabel;
		AriaHelper.setLabel(widget, accessibleLabel);
		widget.addStyleName("av-focusablePart");
	}

	/**
	 * Factory method creating an appropriate focusable part wrapper
	 * for the given widget.
	 * @param widget the widget to wrap
	 * @param focusKey stable semantic key identifying the part
	 * @param accessibleLabel the aria label for the widget
	 * @param am accessibility manager used when native focus is required
	 * @return a focusable part instance, or {@code null} if the widget is {@code null}
	 */
	public static FocusablePartW create(Widget widget, String focusKey,
			String accessibleLabel, AccessibilityManagerInterface am) {
		if (widget == null) {
			return null;
		}

		if (widget instanceof AutoCompleteTextFieldW textField) {
			return new TextFieldFocusablePart(textField, focusKey, accessibleLabel);
		}

		if (widget instanceof StandardButton button) {
			return new ButtonFocusablePart(button, focusKey, accessibleLabel, am);
		}

		return new FocusablePartW(widget, focusKey, accessibleLabel);
	}

	@Override
	public String getAccessibleLabel() {
		return accessibleLabel;
	}

	/**
	 * @return the underlying widget wrapped by this focusable part
	 */
	protected Widget getWidget() {
		return widget;
	}

	@Override
	public String getFocusKey() {
		return focusKey;
	}

	@Override
	public void focus() {
		widget.getElement().focus();
	}

	@Override
	public void blur() {
		widget.getElement().blur();
	}

	@Override
	public boolean handlesEnterKey() {
		return false;
	}
}
