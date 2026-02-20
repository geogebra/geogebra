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

package org.geogebra.web.full.gui.view.algebra;

import java.util.function.BooleanSupplier;

import org.geogebra.common.gui.AccessibilityManagerInterface;
import org.geogebra.common.gui.compositefocus.AbstractFocusableComposite;
import org.geogebra.common.main.ScreenReader;
import org.geogebra.web.full.gui.view.FocusablePartW;

/**
 * Web-specific implementation of a composite focus controller.
 *
 * <p>This class manages focus traversal among a set of focusable parts
 * that together form a single logical focusable component. It delegates
 * traversal mechanics to {@link AbstractFocusableComposite} and integrates
 * with the web accessibility manager.</p>
 *
 * <p>The composite is considered active based on an externally provided
 * focus predicate.</p>
 */
public class FocusableCompositeW extends AbstractFocusableComposite<FocusablePartW> {
	private final BooleanSupplier isRowFocused;
	private Runnable enterCompositeHandler;

	/**
	 * Creates a composite focus controller.
	 * @param am accessibility manager used to register this composite focus container
	 * @param isRowFocused predicate determining whether this composite is currently focused
	 */
	public FocusableCompositeW(AccessibilityManagerInterface am,
			BooleanSupplier isRowFocused) {
		super(am);
		this.isRowFocused = isRowFocused;
		am.registerCompositeFocusContainer(this);
	}

	@Override
	public boolean isFocused() {
		return isRowFocused.getAsBoolean();
	}

	/**
	 * Registers a handler invoked when focus enters this composite.
	 *
	 * <p>The handler can be used by the owning component to synchronize
	 * its own focus or selection state when composite traversal begins.</p>
	 *
	 * @param enterCompositeHandler callback invoked on composite focus entry
	 */
	public void addEnterCompositeHandler(Runnable enterCompositeHandler) {
		this.enterCompositeHandler = enterCompositeHandler;
	}

	@Override
	protected void onGainFocus() {
		if (enterCompositeHandler != null) {
			enterCompositeHandler.run();
		}
	}

	@Override
	protected void readDebug(String text) {
		ScreenReader.debug(text);
	}
}
