package org.geogebra.common.gui.dialog;

/**
 * interface for 3D export dialogs
 */
public interface Export3dDialogInterface {

	/**
	 * show dialog with action
	 * 
	 * @param exportAction
	 *            action for export button pressed
	 */
	void show(Runnable exportAction);
}
