package org.geogebra.web.html5.gui.view.browser;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.move.ggtapi.models.Chapter;
import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.common.util.AsyncOperation;

import elemental2.dom.File;

public interface BrowseViewI {

	void setMaterialsDefaultStyle();

	void loadAllMaterials();

	void clearMaterials();

	void disableMaterials();

	void onSearchResults(final List<Material> response,
	        final ArrayList<Chapter> chapters);

	void close();

	void displaySearchResults(final String query);

	void refreshMaterial(final Material material, final boolean isLocal);

	void rememberSelected(final MaterialListElementI materialElement);

	void setLabels();

	void addMaterial(Material material);

	void removeMaterial(Material material);

	void closeAndSave(AsyncOperation<Boolean> callback);

	/**
	 * @param fileToHandle
	 *            JS file object
	 */
	void openFile(File fileToHandle);

}
