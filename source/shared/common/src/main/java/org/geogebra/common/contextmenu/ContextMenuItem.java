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

import org.geogebra.common.main.Localization;
import org.geogebra.common.util.AttributedString;

/**
 * Items contained in context menus.
 */
public interface ContextMenuItem {
	/**
	 * @param localization Used for translating the title.
	 * @return The title of the item with possible attributes for subscripts.
	 */
	@Nonnull AttributedString getLocalizedTitle(@Nonnull Localization localization);

	/**
	 * @return The item's icon, or null if the context menu item does not have an icon.
	 */
	default @CheckForNull ContextMenuIcon getIcon() {
		return null;
	}
}
