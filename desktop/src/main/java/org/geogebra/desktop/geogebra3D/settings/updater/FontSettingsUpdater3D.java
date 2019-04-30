package org.geogebra.desktop.geogebra3D.settings.updater;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.geogebra3D.euclidianForPlane.EuclidianViewForPlaneCompanion;
import org.geogebra.common.geogebra3D.main.App3DCompanion;
import org.geogebra.common.main.App;
import org.geogebra.desktop.geogebra3D.App3D;
import org.geogebra.desktop.main.settings.updater.FontSettingsUpdaterD;

/**
 * Updates the font settings for the 3D apps.
 */
class FontSettingsUpdater3D extends FontSettingsUpdaterD {

	FontSettingsUpdater3D(App3D app) {
		super(app);
	}

	@Override
	protected void updateEuclidianViewFonts() {
		super.updateEuclidianViewFonts();

		App app = getApp();

		if (app.isEuclidianView3Dinited()) {
			((EuclidianView) app.getEuclidianView3D()).updateFonts();
		}

		App3DCompanion appCompanion = (App3DCompanion) app.getCompanion();
		EuclidianViewForPlaneCompanion euclidianViewForPlaneCompanion =
				appCompanion.getEuclidianViewForPlaneCompanion();
		if (euclidianViewForPlaneCompanion != null) {
			euclidianViewForPlaneCompanion.getView().updateFonts();
		}
	}
}
