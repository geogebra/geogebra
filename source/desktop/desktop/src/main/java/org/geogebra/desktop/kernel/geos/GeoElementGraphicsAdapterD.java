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

package org.geogebra.desktop.kernel.geos;

import org.geogebra.common.awt.MyImage;
import org.geogebra.common.kernel.geos.GeoElementGraphicsAdapter;
import org.geogebra.common.main.App;
import org.geogebra.common.util.debug.Log;
import org.geogebra.desktop.gui.MyImageD;

public class GeoElementGraphicsAdapterD extends GeoElementGraphicsAdapter {
	/** application */
	protected App app;

	/**
	 * @param appl
	 *            application
	 */
	public GeoElementGraphicsAdapterD(App appl) {
		app = appl;
	}

	@Override
	public MyImage getFillImage() {
		if (image != null) {
			return image;
		}

		if ("".equals(imageFileName)) {
			return null;
		}

		if (imageFileName.startsWith("/org/geogebra")) {
			image = MyImageD.loadAsSvg(imageFileName);
		} else {
			image = app.getExternalImageAdapter(imageFileName, 0, 0);
		}

		return image;
	}

	@Override
	public void setImageFileName(String fileName) {
		if (fileName.equals(this.imageFileName)) {
			return;
		}

		setImageFileNameOnly(fileName);

		if (fileName.startsWith("/org/geogebra")) { // internal image
			image = MyImageD.loadAsSvg(imageFileName);
		} else {
			image = app.getExternalImageAdapter(fileName, 0, 0);
		}
	}

	@Override
	public void convertToSaveableFormat() {
		// all openable formats are saveable in Desktop
	}

	@Override
	public String toLaTeXStringBase64() {
		if (image.isSVG()) {
			Log.debug("SVG not supported in LaTeX");
			return "";
		}

		return image.toLaTeXStringBase64();
	}

}
