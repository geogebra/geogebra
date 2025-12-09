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

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoLine3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoRay3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoVectorND;

/**
 * Compute a line through a point and parallel to a vector
 *
 * @author Mathieu
 */
public class AlgoRayPointVector3D extends AlgoLinePointVector3D {

	/**
	 * @param cons
	 *            construction
	 * @param point
	 *            start point
	 * @param v
	 *            direction vector
	 */
	public AlgoRayPointVector3D(Construction cons,
			GeoPointND point, GeoVectorND v) {
		super(cons,  point, v);
	}

	@Override
	public Commands getClassName() {
		return Commands.Ray;
	}

	@Override
	protected GeoLine3D createLine(Construction cons1) {
		return new GeoRay3D(cons1, getPoint());
	}

	@Override
	final public String toString(StringTemplate tpl) {
		return getLoc().getPlain("RayThroughAWithDirectionB",
				getPoint().getLabel(tpl), getInputParallel().getLabel(tpl));
	}

}
