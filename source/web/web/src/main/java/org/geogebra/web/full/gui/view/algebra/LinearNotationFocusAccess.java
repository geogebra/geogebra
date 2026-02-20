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

import org.gwtproject.user.client.ui.Widget;

/**
 * Focus access adapter for algebra items rendered in linear notation.
 *
 * <p>Provides access to the focusable parts specific to linear notation,
 * such as the editable input row.</p>
 */
public class LinearNotationFocusAccess extends RadioTreeItemFocusAccess {
	private final LinearNotationTreeItem item;

	/**
	 * Creates focus access for the given linear-notation tree item.
	 *
	 * @param item the linear-notation algebra item
	 */
	public LinearNotationFocusAccess(LinearNotationTreeItem item) {
		super(item);
		this.item = item;
	}

	@Override
	public Widget inputRow() {
		return item.textField;
	}

	@Override
	public boolean isLinearNotation() {
		return true;
	}
}
