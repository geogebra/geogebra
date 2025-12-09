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

import org.geogebra.common.euclidian.EuclidianView;

/**
 * API delegate
 */
public interface ApiDelegate {

	/**
	 * @param transparent whether to use transparency
	 * @param dpi dots per inch (metadata in PNG)
	 * @param exportScale export pixels:screen pixels
	 * @param ev view
	 * @return base64 envcoded PNG
	 */
	String base64encodePNG(boolean transparent,
			double dpi, double exportScale, EuclidianView ev);

	/**
	 * Opens a .ggb file.
	 * @param strURL URL of a .ggb file
	 */
	void openFile(String strURL);
}
