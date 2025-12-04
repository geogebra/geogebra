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