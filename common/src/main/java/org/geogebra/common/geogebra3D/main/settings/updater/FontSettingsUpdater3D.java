package org.geogebra.common.geogebra3D.main.settings.updater;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.geogebra3D.euclidianForPlane.EuclidianViewForPlaneCompanion;
import org.geogebra.common.geogebra3D.main.App3DCompanion;
import org.geogebra.common.main.App;
import org.geogebra.common.main.settings.updater.FontSettingsUpdater;

class FontSettingsUpdater3D extends FontSettingsUpdater {

	FontSettingsUpdater3D(App app) {
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
