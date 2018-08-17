package org.geogebra.common.main;

import org.geogebra.common.move.ggtapi.models.Material.MaterialType;

/**
 * Handles materials save.
 * 
 * @author laszlo
 *
 */
public interface SaveController {
	/**
	 * Listener interface to communicate with caller GUI.
	 * 
	 * @author laszlo
	 *
	 */
	public interface SaveListener {
		/**
		 * Hides listener GUI
		 * (dialog)
		 */
		void hide();
	}
	
	/**
	 * Save material with a given name.
	 * 
	 * @param fileName
	 *            material file name.
	 * 
	 * @param visibility
	 *            material visibility.
	 * @param listener
	 *            to communicate with caller.
	 */
	void saveAs(String fileName, MaterialVisibility visibility, SaveListener listener);

	/**
	 * Saves the currently active material
	 * 
	 * @param autoSaveCallback
	 *            to run after saving was successful.
	 * 
	 */
	void saveActiveMaterial(Runnable autoSaveCallback);

	/**
	 * Cancel saving.
	 */
	void cancel();

	/**
	 * @return true if the MaterialType is ggb
	 */
	boolean isWorksheet();

	/**
	 * @return true if the MaterialType is ggb
	 */
	boolean isMacro();

	/**
	 * @param saveType
	 *            set the saveType.
	 */
	void setSaveType(MaterialType saveType);

	/**
	 * Sets the callback that needs to be run after saving material.
	 * 
	 * @param runAfterSave
	 *            the callback.
	 */
	void setRunAfterSave(Runnable runAfterSave);

	/**
	 * Run callback after save.
	 */
	void runAfterSaveCallback();
}
