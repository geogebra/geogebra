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

package org.geogebra.common.contextmenu;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.geogebra.common.kernel.geos.GeoElement;

/**
 * Context menu (item) action handler.
 * @param <T> context menu type
 */
@FunctionalInterface
public interface ContextMenuActionHandler<T extends ContextMenuItem> {

	/**
	 * Perform the action for a context menu.
	 * @param geoElement The GeoElement (if applicable, may be {@code null})
	 * @param selectedItem The selected context menu item.
	 */
	void handleItemSelected(@CheckForNull GeoElement geoElement, @Nonnull T selectedItem);
}