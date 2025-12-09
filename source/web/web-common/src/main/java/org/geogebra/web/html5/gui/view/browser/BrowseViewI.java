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

package org.geogebra.web.html5.gui.view.browser;

import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.common.util.AsyncOperation;

/**
 * Open file view.
 */
public interface BrowseViewI {

	/**
	 * Update the material list if empty.
	 */
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

	/**
	 * Close the view.
	 */
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

	/**
	 * Set localized labels.
	 */
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
