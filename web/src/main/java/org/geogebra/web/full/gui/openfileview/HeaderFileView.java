package org.geogebra.web.full.gui.openfileview;

import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.web.full.gui.layout.panels.AnimatingPanel;
import org.geogebra.web.html5.gui.view.browser.BrowseViewI;

public abstract class HeaderFileView implements BrowseViewI {
	public abstract AnimatingPanel getPanel();

	@Override
	public void setMaterialsDefaultStyle() {
		// not used
	}

	@Override
	public void clearMaterials() {
		// not used
	}

	@Override
	public void close() {
		getPanel().close();
	}

	@Override
	public void displaySearchResults(String query) {
		// not used
	}

	@Override
	public void refreshMaterial(Material material, boolean isLocal) {
		// not used
	}

	@Override
	public void removeMaterial(Material material) {
		// not used
	}

	@Override
	public void closeAndSave(AsyncOperation<Boolean> callback) {
		// not used
	}
}
