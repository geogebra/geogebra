package org.geogebra.web.full.gui.menubar.action;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.web.full.gui.menubar.DefaultMenuAction;
import org.geogebra.web.full.main.AppWFull;

import elemental2.dom.DomGlobal;

public class ShowPrivacyPolicyAction extends DefaultMenuAction<AppWFull> {

	@Override
	public void execute(AppWFull app) {
		DomGlobal.window.open(GeoGebraConstants.PRIVACY_POLICY_URL, "_blank", "");
	}
}
