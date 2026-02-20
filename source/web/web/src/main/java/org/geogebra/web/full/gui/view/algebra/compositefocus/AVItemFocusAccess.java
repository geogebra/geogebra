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

import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Widget;

/**
 * Provides access to the focus-relevant widgets of an algebra view item.
 *
 * <p>This interface exposes only the parts required for building composite
 * focus traversal, decoupling focus logic from concrete item implementations.</p>
 */
public interface AVItemFocusAccess {

	/**
	 * @return the main content canvas of the item, if present
	 */
	Widget canvas();

	/**
	 * @return the panel displaying definition and/or value, if present
	 */
	FlowPanel definitionValuePanel();

	/**
	 * @return the "more" (overflow) control of the item, if present
	 */
	Widget moreButton();

	/**
	 * @return the output format control of the item, if present
	 */
	Widget outputFormatButton();

	/**
	 * @return whether this item represents a checkbox item
	 */
	boolean isCheckboxItem();

	/**
	 * @return the checkbox widget, if this item is a checkbox; null otherwise
	 */
	Widget checkbox();

	/**
	 * @return whether the item is rendered using two content rows
	 */
	boolean hasTwoRows();

	/**
	 * @return the editable input row of the item, if present
	 */
	Widget inputRow();

	/**
	 * @return the output row of the item, if present
	 */
	Widget outputRow();

	/**
	 * @return the geo element represented by this item
	 */
	GeoElementND geo();

	/**
	 * @return whether this item represents an editable input entry
	 */
	boolean isInputItem();

	/**
	 * @return whether the item is rendered using linear notation
	 */
	boolean isLinearNotation();
}