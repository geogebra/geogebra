package org.geogebra.web.web.gui.laf;

import org.geogebra.common.GeoGebraConstants.Versions;
import org.geogebra.web.html5.main.AppW;

public class BundleLookAndFeel extends GLookAndFeel {
	@Override
	public Versions getVersion(int dim) {
		return Versions.WEB_FOR_DESKTOP;
	}

	@Override
	public void addWindowClosingHandler(final AppW app) {
		// no handler
	}

}
