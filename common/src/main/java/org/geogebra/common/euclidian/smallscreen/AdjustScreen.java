package org.geogebra.common.euclidian.smallscreen;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Feature;
import org.geogebra.common.util.debug.Log;

public class AdjustScreen {
	private EuclidianView view;
	private App app;
	private Kernel kernel;
	public AdjustScreen(EuclidianView view) {
		this.view = view;
		app = view.getApplication();
		kernel = app.getKernel();
		collectSliders();
	}

	private void collectSliders() {
		Log.debug("[AS] collectSliders()");
		for (GeoElement geo : kernel.getConstruction().getGeoTable().values()) {
			if (geo instanceof GeoNumeric) {
				GeoNumeric num = (GeoNumeric) geo;
				if (num.isSlider()) {
					needsAdjusted(num);
					Log.debug("[AS] a slider: " + num.getLabelSimple());
				}
			}
		}
	}

	protected boolean needsAdjusted(GeoElement geo) {
		App app = view.getApplication();
		int fileWidth = app.getSettings()
				.getEuclidian(view.getEuclidianViewNo()).getFileWidth();
		int fileHeight = app.getSettings()
				.getEuclidian(view.getEuclidianViewNo()).getFileHeight();

		Log.debug("[AS] file: " + fileWidth + "x" + fileHeight);

		if (!app.has(Feature.ADJUST_WIDGETS) || fileWidth == 0
				|| fileHeight == 0) {
			return false;
		}

		double w = app.getWidth();
		double h = app.getHeight();
		Log.debug("[AS] app: " + w + "x" + h);
		if ((w == fileWidth && h == fileHeight) || w == 0 || h == 0) {
			return false;
		}

		view.ensureGeoOnScreen(geo);

		return true;
	}
}
