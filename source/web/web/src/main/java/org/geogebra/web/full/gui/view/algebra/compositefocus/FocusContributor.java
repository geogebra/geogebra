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

import org.geogebra.common.gui.AccessibilityManagerInterface;
import org.geogebra.web.full.gui.view.algebra.FocusableCompositeW;
import org.geogebra.web.full.gui.view.algebra.RadioTreeItemFocusAccess;

/**
 * Contributes focusable parts to a composite focus container.
 *
 * <p>Implementations add one or more focusable parts based on the
 * current state of an algebra view item.</p>
 */
public interface FocusContributor {

	/**
	 * Adds focusable parts to the given composite focus.
	 *
	 * @param item access to the algebra item's focus-relevant widgets
	 * @param focus the composite focus container to populate
	 * @param am the accessibility manager used for focus handling
	 */
	void contribute(RadioTreeItemFocusAccess item,
			FocusableCompositeW focus,
			AccessibilityManagerInterface am);
}
