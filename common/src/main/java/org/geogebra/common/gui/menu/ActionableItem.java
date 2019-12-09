package org.geogebra.common.gui.menu;

/**
 * A menu item that has an action associated with it.
 */
public interface ActionableItem extends MenuItem {

	/**
	 * Get the action of the menu item.
	 *
	 * @return action
	 */
	Action getAction();
}
