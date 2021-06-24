package org.geogebra.web.tablet.gui.browser;

import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.web.full.gui.bridge.GeoGebraJSNativeBridge;
import org.geogebra.web.full.gui.browser.MaterialListElement;
import org.geogebra.web.html5.main.AppW;

public class TabletMaterialElement extends MaterialListElement {

	/**
	 * @param m material
	 * @param app app
	 * @param isLocal whether the material exists only on the device
	 */
	public TabletMaterialElement(final Material m, final AppW app,
			final boolean isLocal) {
		super(m, app, isLocal);
	}

	@Override
	public void onView() {
		app.getGuiManager().getBrowseView().setMaterialsDefaultStyle();
		if (!localMaterial) {
			loadNative(getMaterial().getId(), getMaterial().getTitle(), app
					.getLoginOperation().getModel().getLoginToken());
		}

	}

	private void loadNative(int id, String materialTitle, String token) {
		if (GeoGebraJSNativeBridge.get() != null) {
			GeoGebraJSNativeBridge.get().open(id, materialTitle, token);
		}
	}
}
