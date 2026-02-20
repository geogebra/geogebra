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

package org.geogebra.web.full.gui.view.algebra.compositefocus;

import java.util.function.Supplier;

import org.geogebra.common.gui.AccessibilityManagerInterface;
import org.geogebra.web.full.gui.view.FocusablePartW;
import org.geogebra.web.full.gui.view.algebra.FocusableCompositeW;
import org.gwtproject.user.client.ui.Widget;

/**
 * Utility for conditionally adding focusable parts to a composite focus.
 *
 * <p>Creates and registers a focusable part only when a corresponding widget
 * is present, applying a stable focus key and optional screen-reader label.</p>
 */
public final class FocusPartAdder {
	private FocusPartAdder() {
		// utility class
	}

	/**
	 * Adds a focusable part to the composite if the given widget exists.
	 *
	 * @param focus the composite focus container to add the part to
	 * @param am the accessibility manager used for focus handling
	 * @param widget the widget to wrap as a focusable part
	 * @param key a stable key identifying the focusable part
	 * @param ariaLabel supplier of a screen-reader label, or {@code null} if none
	 * @return the widget that was added to the composite, or {@code null} if no widget
	 * was present
	 */
	public static Widget addIfExists(FocusableCompositeW focus, AccessibilityManagerInterface am,
			Widget widget, String key, Supplier<String> ariaLabel) {
		if (widget == null) {
			return null;
		}

		String label = ariaLabel != null ? ariaLabel.get() : null;

		FocusablePartW part = FocusablePartW.create(widget, key, label, am);
		if (part != null) {
			focus.addPart(part);
		}
		return widget;
	}
}
