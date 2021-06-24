package org.geogebra.web.full.gui.menubar.action;

import org.geogebra.web.full.gui.ShareControllerW;
import org.geogebra.web.full.gui.bridge.GeoGebraJSNativeBridge;
import org.geogebra.web.full.gui.menubar.DefaultMenuAction;
import org.geogebra.web.full.main.AppWFull;

/**
 * Shares the material.
 */
public class ShareAction extends DefaultMenuAction<Void> {

	@Override
	public void execute(Void item, AppWFull app) {
		ShareControllerW shareController = (ShareControllerW) app.getShareController();
		if (GeoGebraJSNativeBridge.get() != null) {
			shareController.getBase64();
		} else {
			shareController.setAnchor(null);
			shareController.share();
		}
	}
}
