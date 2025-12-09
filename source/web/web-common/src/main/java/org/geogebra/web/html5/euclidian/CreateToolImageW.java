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

package org.geogebra.web.html5.euclidian;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.measurement.CreateToolImage;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.geos.GeoImage;
import org.geogebra.web.html5.css.GuiResourcesSimple;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.SafeGeoImageFactory;
import org.geogebra.web.html5.util.ImageManagerW;
import org.geogebra.web.resources.SVGResource;

public class CreateToolImageW implements CreateToolImage {
	private final AppW app;
	private final Construction cons;

	/**
	 *
	 * @param app {@link AppW}
	 */
	public CreateToolImageW(AppW app) {
		this.app = app;
		cons = app.getKernel().getConstruction();
	}

	@Override
	public GeoImage create(int mode, String internalName) {
		GeoImage toolImage = new GeoImage(cons);
		toolImage.setMeasurementTool(true);
		SVGResource toolSVG = getMeasurementToolSVG(mode);
		SafeGeoImageFactory factory = new SafeGeoImageFactory(app, toolImage);
		String path = ImageManagerW.getMD5FileName(internalName, toolSVG.getSafeUri().asString());
		factory.createInternalFile(path, toolSVG.getSafeUri().asString());
		return toolImage;

	}

	private static SVGResource getMeasurementToolSVG(int mode) {
		switch (mode) {
		case EuclidianConstants.MODE_RULER:
			return GuiResourcesSimple.INSTANCE.ruler();
		case EuclidianConstants.MODE_PROTRACTOR:
			return GuiResourcesSimple.INSTANCE.protractor();
		case EuclidianConstants.MODE_TRIANGLE_PROTRACTOR:
			return GuiResourcesSimple.INSTANCE.triangle_protractor();
		}

		return null;
	}

}
