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

package org.geogebra.common.kernel.algos;

import org.geogebra.common.annotation.MissingDoc;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoPointND;

/**
 * interface to merge AlgoJointPoints and AlgoJointPoints3D
 *
 */
public interface AlgoJoinPointsSegmentInterface {

	/**
	 * 
	 * @return polygon/polyhedron of this algo (or null)
	 */
	public GeoElement getPoly();

	/**
	 * modify input points
	 * 
	 * @param A
	 *            first point
	 * @param B
	 *            second point
	 */
	public void modifyInputPoints(GeoPointND A, GeoPointND B);

	@MissingDoc
	public void compute();

}
