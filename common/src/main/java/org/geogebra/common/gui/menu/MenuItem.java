package org.geogebra.common.gui.menu;

import javax.annotation.Nullable;

/**
 * A model describing a single menu item.
 * Visually a menu item consists of an icon and a label.
 */
public interface MenuItem {

	/**
	 * Get the icon of the menu item. Can be null.
	 *
	 * @return icon
	 */
	@Nullable Icon getIcon();

	/**
	 * Get the translation key of the label of them menu item.
	 *
	 * @return label translation key
	 */
	String getLabel();
}
