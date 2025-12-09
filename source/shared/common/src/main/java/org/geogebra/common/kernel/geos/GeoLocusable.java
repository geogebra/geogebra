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

package org.geogebra.common.kernel.geos;

import java.util.ArrayList;

import org.geogebra.common.kernel.MyPoint;
import org.geogebra.common.kernel.kernelND.GeoElementND;

/**
 * Allow Freehand Functions to behave like GeoLocus for some things eg
 * Length[f], First[f, n]
 * 
 * @author Michael
 *
 */
public interface GeoLocusable extends GeoElementND {
	/**
	 * @return number of points
	 */
	public int getPointLength();

	/**
	 * @return list of points
	 */
	public ArrayList<? extends MyPoint> getPoints();
}
