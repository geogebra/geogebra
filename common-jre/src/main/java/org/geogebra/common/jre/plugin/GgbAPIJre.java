package org.geogebra.common.jre.plugin;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoImage;
import org.geogebra.common.main.App;
import org.geogebra.common.plugin.GgbAPI;

public abstract class GgbAPIJre extends GgbAPI {

	public GgbAPIJre(App app) {
		this.app = app;
		this.kernel = app.getKernel();
	}
	final public synchronized String getPNGBase64(double exportScale,
			boolean transparent, double DPI) {
		return getPNGBase64(exportScale, transparent, DPI, false);
	}

	/*
	 * returns a String (base-64 encoded PNG file of the Graphics View)
	 */
	final public synchronized String getPNGBase64(double exportScale,
			boolean transparent, double DPI, boolean copyToClipboard) {

		EuclidianView ev = (EuclidianView) app.getActiveEuclidianView();

		if (copyToClipboard) {

			if (DPI == 0 || Double.isNaN(DPI)) {
				exportPNGClipboardDPIisNaN(transparent, exportScale, ev);
			} else {

				if (exportScale == 0 || Double.isNaN(exportScale)) {
					// calculate so that we get 1:1 scale
					exportScale = (ev.getPrintingScale() * DPI) / 2.54
							/ ev.getXscale();

				}

				exportPNGClipboard(transparent, (int) DPI, exportScale, ev);
			}
			return "";

		}

		return base64encodePNG(transparent, DPI, exportScale, ev);
	}

	@Override
	public void drawToImage(String label, double[] x, double[] y) {
		GeoElement ge = kernel.lookupLabel(label);

		if (ge == null) {
			ge = new GeoImage(kernel.getConstruction());
			if (label == null || label.length() == 0) {
				ge.setLabel(null);
			} else {
				ge.setLabel(label);
			}
		}
		if (!ge.isGeoImage()) {
			debug("Bad drawToImage arguments");
			return;
		}

		app.getEuclidianView1().drawPoints((GeoImage) ge, x, y);

	}

	abstract protected void exportPNGClipboard(boolean transparent, int DPI,
			double exportScale, EuclidianView ev);

	abstract protected void exportPNGClipboardDPIisNaN(boolean transparent,
			double exportScale, EuclidianView ev);

	abstract protected String base64encodePNG(boolean transparent, double DPI,
			double exportScale, EuclidianView ev);

}
