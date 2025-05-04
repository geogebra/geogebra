package org.geogebra.web.html5.gui.view.browser;

import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.common.util.AsyncOperation;

/**
 * Open file view.
 */
public interface BrowseViewI {

	void setMaterialsDefaultStyle();

	/**
	 * Load the default set of materials (user's own or featured)
	 * @param offset number of materials to skip
	 */
	void loadAllMaterials(int offset);

	/**
	 * Remove all material cards.
	 */
	void clearMaterials();

	void close();

	/**
	 * Show cards for all materials matching a search query.
	 * @param query search query
	 */
	void displaySearchResults(String query);

	/**
	 * Refresh a material card
	 * TODO remove
	 */
	void refreshMaterial(Material material, boolean isLocal);

	void setLabels();

	/**
	 * Add card for a single material.
	 * @param material material
	 */
	void addMaterial(Material material);

	/**
	 * Remove card for a single material.
	 * @param material material
	 */
	void removeMaterial(Material material);

	/**
	 * Close the view and make sure current construction is saved.
	 * @param callback called after saving is finished (or canceled)
	 */
	void closeAndSave(AsyncOperation<Boolean> callback);

}
