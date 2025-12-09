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

package org.geogebra.common.geogebra3D.kernel3D.algos;

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPoint3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Path;
import org.geogebra.common.kernel.algos.AlgoPointOnPath;
import org.geogebra.common.kernel.geos.GeoNumberValue;

/**
 * Algo for 3D point on path.
 */
public class AlgoPoint3DOnPath extends AlgoPointOnPath {

	/**
	 * @param cons
	 *            construction
	 * @param path
	 *            path
	 * @param x
	 *            close point x-coord
	 * @param y
	 *            close point y-coord
	 * @param z
	 *            close point z-coord
	 */
	public AlgoPoint3DOnPath(Construction cons, Path path,
			double x, double y, double z) {
		super(cons, path, x, y, z, true);
	}

	/**
	 * @param cons
	 *            construction
	 * @param path
	 *            path
	 * @param param
	 *            path parameter
	 */
	public AlgoPoint3DOnPath(Construction cons, Path path,
			GeoNumberValue param) {
		super(cons, path, param);
	}

	@Override
	protected void createPoint(Path path, double x, double y, double z) {

		P = new GeoPoint3D(cons, path);
		P.setCoords(x, y, z, 1.0);

	}

}
