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

import java.util.List;

import org.geogebra.common.gui.AccessibilityManagerInterface;
import org.geogebra.web.full.gui.view.algebra.FocusableCompositeW;
import org.geogebra.web.full.gui.view.algebra.RadioTreeItemFocusAccess;

/**
 * Assembles and rebuilds composite focus parts for an algebra view item.
 *
 * <p>This class coordinates {@link FocusContributor}s to populate a
 * {@link FocusableCompositeW} with focusable parts derived from the
 * current state of a {@link RadioTreeItemFocusAccess}. Previously
 * selected focus is restored using stable focus keys, allowing focus
 * to survive UI rebuilds.</p>
 */
public final class AVCompositeFocusAssembler {
	private final FocusableCompositeW focus;
	private final RadioTreeItemFocusAccess item;
	private final AccessibilityManagerInterface am;

	/**
	 * Creates a new assembler for the given focus container and item access.
	 *
	 * @param focus the composite focus container to populate
	 * @param item access to the algebra item's focusable widgets
	 * @param am the accessibility manager used for registration and focus handling
	 */
	public AVCompositeFocusAssembler(FocusableCompositeW focus, RadioTreeItemFocusAccess item,
			AccessibilityManagerInterface am) {
		this.focus = focus;
		this.item = item;
		this.am = am;
	}

	/**
	 * Rebuilds the composite focus using the provided contributors.
	 *
	 * <p>All existing parts are cleared, contributors are invoked in order,
	 * and previously selected focus is restored if possible.</p>
	 *
	 * @param contributors the focus contributors defining which parts to add
	 */
	public void rebuild(List<FocusContributor> contributors) {
		String previousKey = focus.getSelectedKey();
		focus.clearParts();

		for (FocusContributor c : contributors) {
			c.contribute(item, focus, am);
		}

		if (previousKey != null) {
			focus.restoreSelection(previousKey);
		}
	}
}
