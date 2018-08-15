package org.geogebra.common.main;

import org.geogebra.common.move.ggtapi.models.Material.MaterialType;
import org.geogebra.common.move.ggtapi.requests.MaterialCallbackI;
import org.geogebra.common.util.AsyncOperation;

/**
 * Handles materials save.
 * 
 * @author laszlo
 *
 */
public interface SaveController {
	public interface SaveListener {
		AsyncOperation<String> base64Callback();

		void hide();

		void runAfterSaveCallback();

		MaterialCallbackI initMaterialCB(final String base64, final boolean forked);
	}
	
	/**
	 * Save material
	 * 
	 * @param fileName
	 *            material file name.
	 * 
	 * @param visibility
	 *            material visibility.
	 * @param listener
	 *            to communicate with caller.
	 */
	void save(String fileName, MaterialVisibility visibility, SaveListener listener);

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
	public void setSaveType(MaterialType saveType);
}
