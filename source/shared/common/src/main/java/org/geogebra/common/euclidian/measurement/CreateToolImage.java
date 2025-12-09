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

package org.geogebra.common.euclidian.measurement;

import org.geogebra.common.kernel.geos.GeoImage;

/**
 * Interface to create the tool image.
 */
public interface CreateToolImage {

	/**
	 * Create image belongs to mode and stored on internal name.
	 * @param mode of the tool
	 * @param internalName of the tool
	 * @return the image of the tool
	 */
	GeoImage create(int mode, String internalName);
}
