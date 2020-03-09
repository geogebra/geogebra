package org.geogebra.web.full.gui.menubar;

import org.geogebra.web.full.main.AppWFull;

public interface MenuAction<T> {

	/**
	 * @param item reference item used for context menu
	 * @return whether action is active (grey out otherwise)
	 */
	boolean isAvailable(T item);

	/**
	 * Executes the action.
	 *
	 * @param item reference item
	 * @param app  app
	 */
	void execute(T item, AppWFull app);
}
