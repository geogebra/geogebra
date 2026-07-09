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

package org.geogebra.web.full.gui.toolbarpanel.spreadsheet;

import java.util.function.BooleanSupplier;

import javax.annotation.CheckForNull;

import org.geogebra.common.gui.AccessibilityGroup;
import org.geogebra.common.gui.FocusableComponent;

/**
 * Focusable adapter for spreadsheet implementations.
 */
public class SpreadsheetFocusableAdapter implements FocusableComponent {

	private final BooleanSupplier isVisible;
	private final BooleanSupplier hasFocus;
	private final Runnable focus;

	/**
	 * @param isVisible Whether the spreadsheet is currently visible and can therefore be focused
	 * by tabbing.
	 * @param hasFocus Whether keyboard focus is currently inside the spreadsheet.
	 * @param focus Focuses the spreadsheet element.
	 */
	public SpreadsheetFocusableAdapter(BooleanSupplier isVisible,
			BooleanSupplier hasFocus, Runnable focus) {
		this.isVisible = isVisible;
		this.hasFocus = hasFocus;
		this.focus = focus;
	}

	@Override
	public boolean focusIfVisible(boolean reverse) {
		if (!isVisible.getAsBoolean()) {
			return false;
		}
		focus.run();
		return true;
	}

	@Override
	public boolean hasFocus() {
		return hasFocus.getAsBoolean();
	}

	@Override
	public AccessibilityGroup getAccessibilityGroup() {
		return AccessibilityGroup.SPREADSHEET;
	}

	@Override
	public boolean focusNext() {
		return false;
	}

	@Override
	public boolean focusPrevious() {
		return false;
	}

	@Override
	public @CheckForNull AccessibilityGroup.ViewControlId getViewControlId() {
		return null;
	}
}
