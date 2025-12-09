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

package org.geogebra.web.full.gui.contextmenu;

import org.geogebra.common.contextmenu.ContextMenuIcon;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.resources.SVGResource;
import org.geogebra.web.shared.SharedResources;

public class ImageMap {

	/**
	 * @param icon icon identifier
	 * @return SVG resource for given identifier, null if no identifier supplied
	 */
	public static SVGResource get(ContextMenuIcon icon) {
		if (icon == null) {
			return null;
		}
		switch (icon) {
		case Expression:
			return MaterialDesignResources.INSTANCE.description();
		case Text:
			return MaterialDesignResources.INSTANCE.icon_quote_black();
		case Image:
			return MaterialDesignResources.INSTANCE.insert_photo_black();
		case Help:
			return SharedResources.INSTANCE.icon_help_black();
		case Delete:
			return MaterialDesignResources.INSTANCE.delete_black();
		}
		return null;
	}
}
