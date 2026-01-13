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

package org.geogebra.common.jre.headless;

import org.geogebra.common.awt.MyImage;

/**
 * Application interface for desktop.
 */
public interface AppDI {

	/**
	 * Add user-supplied image.
	 * @param name filename
	 * @param img image
	 */
	void addExternalImage(String name, MyImage img);

	/**
	 * Export active graphics view into an image
	 * @param thumbnailPixelsX width
	 * @param thumbnailPixelsY height
	 * @return exported image
	 */
	MyImage getExportImage(double thumbnailPixelsX, double thumbnailPixelsY);

	/**
	 * Get user-supplied image by filename.
	 * @param fileName filename
	 * @return image
	 */
	MyImage getExternalImage(String fileName);

}
