package org.geogebra.web.full.gui.menubar.action;

import org.geogebra.web.full.gui.ShareControllerW;
import org.geogebra.web.full.gui.menubar.DefaultMenuAction;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.bridge.GeoGebraJSNativeBridge;

/**
 * Shares the material.
 */
public class ShareAction extends DefaultMenuAction<AppWFull> {

	@Override
	public void execute(AppWFull app) {
		ShareControllerW shareController = (ShareControllerW) app.getShareController();
		if (GeoGebraJSNativeBridge.get() != null) {
			shareController.getBase64();
		} else {
			shareController.setAnchor(null);
			shareController.share();
		}
	}
}
