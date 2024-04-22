package org.geogebra.web.html5.gui.view.browser;

import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.common.util.AsyncOperation;

public interface BrowseViewI {

	void setMaterialsDefaultStyle();

	void loadAllMaterials(int offset);

	void clearMaterials();

	void close();

	void displaySearchResults(String query);

	void refreshMaterial(Material material, boolean isLocal);

	void setLabels();

	void addMaterial(Material material);

	void removeMaterial(Material material);

	void closeAndSave(AsyncOperation<Boolean> callback);

}
