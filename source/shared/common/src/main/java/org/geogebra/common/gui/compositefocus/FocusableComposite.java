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

package org.geogebra.common.gui.compositefocus;

import org.geogebra.common.gui.FocusTraversal;

/**
 * Represents a focusable component composed of multiple internal focusable parts.
 *
 * <p>A composite focus allows traversal among its internal parts using dedicated
 * navigation shortcuts, while the composite itself participates as a single
 * focusable unit in higher-level focus traversal.</p>
 */
public interface FocusableComposite extends FocusTraversal {

	/**
	 * @return whether this composite currently has active internal focus
	 */
	boolean isFocused();

	/**
	 * Removes any active internal focus within this composite.
	 */
	void blur();

	/**
	 * @return whether the selected part consumes the Enter key when selected, so the container
	 *         should not treat Enter as "edit/enter content".
	 */
	boolean handlesEnterKeyForSelectedPart();

	/**
	 * Focuses the first internal part of this composite.
	 *
	 * @return {@code true} if focus was applied; {@code false} if no parts are available
	 */
	boolean focusFirst();

	/**
	 * Focuses the last internal part of this composite.
	 *
	 * @return {@code true} if focus was applied; {@code false} if no parts are available
	 */
	boolean focusLast();

}
