package org.geogebra.web.full.gui.menubar.action;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.web.full.gui.menubar.DefaultMenuAction;
import org.geogebra.web.full.main.AppWFull;

import elemental2.dom.DomGlobal;

/**
 * Shows license.
 */
public class ShowLicenseAction extends DefaultMenuAction<AppWFull> {

	@Override
	public void execute(AppWFull app) {
		if (app.isByCS()) {
			DomGlobal.window.open(GeoGebraConstants.BYCS_LICENCE_URL, "_blank", "");
		} else {
			DomGlobal.window.open(GeoGebraConstants.GGB_LICENSE_URL,
					"_blank", "");
		}
	}
}
