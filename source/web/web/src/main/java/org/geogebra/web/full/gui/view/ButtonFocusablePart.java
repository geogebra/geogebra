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
import org.geogebra.common.gui.FocusableComponent;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.gwtproject.core.client.Scheduler;
import org.gwtproject.user.client.ui.Widget;

/**
 * Focusable part implementation for buttons that require native browser focus.
 *
 * <p>This wrapper delegates focus to the underlying DOM element and integrates
 * with the accessibility manager to establish the correct focus anchor.
 * It is used for interactive controls (e.g. toggle buttons) inside a composite
 * focus container.</p>
 */
public final class ButtonFocusablePart extends FocusablePartW {
	private final AccessibilityManagerInterface am;

	/**
	 * Creates a focusable part for a standard button.
	 * @param button the underlying button widget
	 * @param focusKey stable key used to preserve selection across rebuilds
	 * @param label the aria label for the button
	 * @param am accessibility manager used to manage focus anchoring
	 */
	public ButtonFocusablePart(StandardButton button, String focusKey,
			String label, AccessibilityManagerInterface am) {
		super(button, focusKey, label);
		this.am = am;

	}

	@Override
	public void focus() {
		Widget button = getWidget();
		if (button instanceof FocusableComponent) {
			am.setAnchor((FocusableComponent) button);
		}
		Scheduler.get().scheduleFinally(() -> {
			button.getElement().focus();
		});
	}

	@Override
	public void blur() {
		getWidget().getElement().blur();
	}

	@Override
	public boolean handlesEnterKey() {
		return true;
	}

}
