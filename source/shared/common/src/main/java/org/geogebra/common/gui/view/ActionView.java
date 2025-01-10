package org.geogebra.common.gui.view;

/**
 * A view that executes an action.
 */
public interface ActionView {

	/**
	 * Sets the action.
	 * @param action This action is going to be executed when the view is triggered.
	 */
	void setAction(Runnable action);

	/**
	 * Enables or disables the view.
	 * @param enabled Enables the view if true, disables the view if false.
	 */
	void setEnabled(boolean enabled);
}
