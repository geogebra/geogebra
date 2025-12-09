/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.common.main;

import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.common.move.ggtapi.models.Material.MaterialType;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.common.util.TextObject;

/**
 * Handles materials save.
 * 
 * @author laszlo
 *
 */
public interface SaveController {

	/**
	 * Replace current material with one of the appropriate type
	 */
	void ensureTypeOtherThan(Material.MaterialType type);

	/**
	 * @param saveCallback callback, gets a flag depending on whether material was saved or not
	 * @param addTempCheckBox true if checkbox for template should be added
	 */
	void showDialogIfNeeded(AsyncOperation<Boolean> saveCallback, boolean addTempCheckBox);

	/**
	 * Show dialog for local saving.
	 * @param afterSave callback
	 */
	void showLocalSaveDialog(Runnable afterSave);

	/**
	 * Listener interface to communicate with caller GUI.
	 * 
	 * @author laszlo
	 *
	 */
	interface SaveListener {
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
	void saveActiveMaterial(AsyncOperation<Boolean> autoSaveCallback);

	/**
	 * Cancel saving.
	 */
	void cancel();

	/**
	 * Don't save the construction.
	 */
	void dontSave();

	/**
	 * @return true if the MaterialType is ggb
	 */
	boolean isWorksheet();

	/**
	 * @param saveType
	 *            set the saveType.
	 */
	void setSaveType(MaterialType saveType);

	/**
	 * @return type of material it should be saved (e.g. ggb/ggs/ggsTemplate)
	 */
	MaterialType getSaveType();

	/**
	 * @return true if the material is saved as template
	 */
	boolean savedAsTemplate();

	/**
	 * Sets the callback that needs to be run after saving material.
	 * 
	 * @param runAfterSave
	 *            the callback.
	 */
	void setRunAfterSave(AsyncOperation<Boolean> runAfterSave);

	/**
	 * Run callback after save.
	 * 
	 * @param activeMaterial
	 *            active material
	 */
	void runAfterSaveCallback(boolean activeMaterial);

	/**
	 * @param title
	 *            title component
	 * @param fallback
	 *            fallback if title is empty
	 */
	void updateSaveTitle(TextObject title, String fallback);
}
