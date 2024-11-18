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
