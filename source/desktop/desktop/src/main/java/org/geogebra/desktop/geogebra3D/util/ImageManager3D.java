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

package org.geogebra.desktop.geogebra3D.util;

import java.awt.Component;
import java.awt.Image;

import org.geogebra.desktop.util.ImageManagerD;
import org.geogebra.desktop.util.ImageResourceD;

/**
 * Class extending ImageManager for 3D.
 * 
 * @author mathieu
 *
 */
public class ImageManager3D extends ImageManagerD {

	/**
	 * default constructor
	 * 
	 * @param comp main app component
	 */
	public ImageManager3D(Component comp) {
		super(comp);
	}

	@Override
	public Image getImageResourceGeoGebra(ImageResourceD name) {

		Image img = getImageResource(
				"/org/geogebra/desktop/geogebra3D" + name.getFilename());

		if (img == null) {
			return super.getImageResourceGeoGebra(name);
		}
		return img;
	}

}
