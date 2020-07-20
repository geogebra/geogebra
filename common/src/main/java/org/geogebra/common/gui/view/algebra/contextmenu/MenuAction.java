package org.geogebra.common.gui.view.algebra.contextmenu;

/** Describes a context menu action */
public interface MenuAction<T> {

	/**
	 * Check whether the action is available.
	 * @param element reference element used for context menu
	 * @return whether action is active
	 */
	boolean isAvailable(T element);

	/**
	 * Executes the action.
	 * @param element reference element
	 */
	void execute(T element);
}
