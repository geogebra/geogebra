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

package org.geogebra.web.html5.kernel;

import org.geogebra.common.awt.MyImage;
import org.geogebra.common.main.App;
import org.geogebra.common.util.FileExtensions;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;

/**
 * Connects geoelements to images in Web
 */
public class GeoElementGraphicsAdapterW extends
        org.geogebra.common.kernel.geos.GeoElementGraphicsAdapter {

	private final App app;

	/**
	 * @param app
	 *            application
	 */
	public GeoElementGraphicsAdapterW(App app) {
		this.app = app;
	}

	@Override
	public MyImage getFillImage() {
		if (image == null) {
			image = app.getExternalImageAdapter(imageFileName, 0, 0);
		}

		return image;
	}

	@Override
	public void setImageFileName(String fileName) {
		if (fileName == null || fileName.equals(this.imageFileName)) {
			return;
		}

		setImageFileNameOnly(fileName);
		image = app.getExternalImageAdapter(imageFileName, 0, 0);
	}

	@Override
	public void convertToSaveableFormat() {
		if ("".equals(imageFileName)) {
			return;
		}

		String oldFn = imageFileName;
		FileExtensions ext = StringUtil.getFileExtension(imageFileName);

		if (ext.isAllowedImage()) {
			// allowed already, nothing to do
			return;
		}

		String fn = StringUtil.changeFileExtension(imageFileName,
				FileExtensions.PNG);

		imageFileName = app.md5Encrypt(fn) + "/" + fn;
		Log.debug("Converted:" + oldFn + "->" + imageFileName);
	}

	@Override
	public String toLaTeXStringBase64() {
		return image.toLaTeXStringBase64();
	}
}
