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

package org.geogebra.web.full.gui.openfileview;

import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.web.full.gui.layout.panels.AnimatingPanel;
import org.geogebra.web.html5.gui.view.browser.BrowseViewI;

/**
 * UI element for opening files, consists of a header and the list of materials.
 */
public abstract class HeaderFileView implements BrowseViewI {

	/**
	 * @return content panel
	 */
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
