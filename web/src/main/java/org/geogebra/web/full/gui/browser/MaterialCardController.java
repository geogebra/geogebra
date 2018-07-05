package org.geogebra.web.full.gui.browser;

import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.web.html5.main.AppW;

public class MaterialCardController {

	private AppW app;

	/**
	 * @param app
	 *            application
	 */
	public MaterialCardController(AppW app) {
		this.app = app;
	}

	/**
	 * @param material
	 *            selected material
	 */
	public void load(Material material) {
		app.getViewW().processFileName(material.getFileName());
		app.setActiveMaterial(material);
		app.getGuiManager().getBrowseView().close();
	}

}
