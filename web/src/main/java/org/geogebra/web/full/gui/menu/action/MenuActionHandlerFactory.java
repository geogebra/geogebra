package org.geogebra.web.full.gui.menu.action;

/**
 * Creates menu action handlers.
 */
public interface MenuActionHandlerFactory {

	/**
	 * @return MenuActionHandler instance
	 */
	MenuActionHandler create();
}
