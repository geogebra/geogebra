/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.common.jre.plugin;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.io.file.ByteArrayZipFile;
import org.geogebra.common.jre.io.MyXMLioJre;
import org.geogebra.common.jre.util.Base64;
import org.geogebra.common.main.App;
import org.geogebra.common.plugin.GgbAPI;
import org.geogebra.common.util.debug.Log;

/**
 * Api for desktop and Android
 */
public abstract class GgbAPIJre extends GgbAPI {

	/**
	 * @param app
	 *            application
	 */
	public GgbAPIJre(App app) {
		this.app = app;
		this.kernel = app.getKernel();
		this.algebraprocessor = kernel.getAlgebraProcessor();
		construction = kernel.getConstruction();
	}

	/**
	 * @param exportScale
	 *            scale factor
	 * @param transparent
	 *            true to make background color of EV transparent
	 * @param DPI
	 *            DPI, does not affect bitmap pixel size
	 * @param greyscale
	 *            true for monochrome
	 * @return base64 encoded PNG
	 */
	final public synchronized String getPNGBase64(double exportScale,
			boolean transparent, double DPI, boolean greyscale) {
		return getPNGBase64(exportScale, transparent, DPI, false, greyscale);
	}

	/*
	 * returns a String (base-64 encoded PNG file of the Graphics View)
	 */
	@Override
	final public synchronized String getPNGBase64(double preferredScale,
			boolean transparent, double DPI, boolean copyToClipboard,
			boolean greyscale) {
		double exportScale = preferredScale;
		EuclidianView ev = app.getActiveEuclidianView();

		if (copyToClipboard) {

			if (DPI == 0 || Double.isNaN(DPI)) {
				exportPNGClipboardDPIisNaN(transparent, exportScale, ev);
			} else {

				if (exportScale == 0 || Double.isNaN(exportScale)) {
					// calculate so that we get 1:1 scale
					exportScale = ev.getPrintingScale() * DPI / 2.54
							/ ev.getXscale();

				}

				exportPNGClipboard(transparent, (int) DPI, exportScale, ev);
			}
			return "";

		}

		return base64encodePNG(transparent, DPI, exportScale, ev);
	}

	/**
	 * Opens construction given in XML format. May be used for loading
	 * constructions.
	 */

	@Override
	public void setBase64(String base64) {
		byte[] zipFile;
		try {
			zipFile = Base64.decode(base64);
		} catch (Exception e) {
			Log.debug(e);
			return;
		}
		app.loadXML(new ByteArrayZipFile(zipFile));

	}

	/**
	 * Returns current construction in Base64 format. May be used for saving.
	 */
	@Override
	public String getBase64(boolean includeThumbnail) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			((MyXMLioJre) app.getXMLio()).writeGeoGebraFile(baos,
					includeThumbnail);
			return Base64.encodeToString(baos.toByteArray(), false);
		} catch (IOException e) {
			Log.debug(e);
			return null;
		}
	}

	@Override
	public void selectPage(String pageIdx) {
		// stub
	}

	@Override
	public void previewRefresh() {
		// stub
	}

	/**
	 * @param exportScale
	 *            scale factor
	 * @param transparent
	 *            true to make background color of EV transparent
	 * @param DPI
	 *            DPI, does not affect bitmap pixel size
	 * @param ev
	 *            view
	 */
	abstract protected void exportPNGClipboard(boolean transparent, int DPI,
			double exportScale, EuclidianView ev);

	/**
	 * @param exportScale
	 *            scale factor
	 * @param transparent
	 *            true to make background color of EV transparent
	 * @param ev
	 *            view
	 */
	abstract protected void exportPNGClipboardDPIisNaN(boolean transparent,
			double exportScale, EuclidianView ev);

	/**
	 * @param exportScale
	 *            scale factor
	 * @param transparent
	 *            true to make background color of EV transparent
	 * @param DPI
	 *            DPI, does not affect bitmap pixel size
	 * @param ev
	 *            view
	 * @return base64 encoded PNG
	 */
	abstract protected String base64encodePNG(boolean transparent, double DPI,
			double exportScale, EuclidianView ev);

	/**
	 * pops up message dialog with "OK" and "Stop Script"
	 *
	 * @param message
	 *            to display
	 */
	public void alert(String message) {
		// overridden in desktop
	}

	/**
	 * JavaScript-like prompt
	 *
	 * @param promptText
	 *            prompt text
	 * @param initValue
	 *            default value
	 * @return user's response
	 */
	public String prompt(Object promptText, Object initValue) {
		return "";
	}
}
