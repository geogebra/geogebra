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

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPlane3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoPointND;

/**
 * Compute a plane through a point and:
 * <ul>
 * <li>parallel to another plane (or polygon)
 * <li>through a line (or segment, ...) TODO
 * </ul>
 *
 * @author matthieu
 */
public abstract class AlgoPlaneThroughPoint extends AlgoElement3D {

	private GeoPointND point; // input

	private GeoPlane3D plane; // output

	/**
	 * @param cons
	 *            construction
	 * @param point
	 *            point
	 */
	public AlgoPlaneThroughPoint(Construction cons, GeoPointND point) {
		super(cons);
		this.point = point;
		plane = new GeoPlane3D(cons);

	}

	@Override
	public Commands getClassName() {
		return Commands.Plane;
	}

	public GeoPlane3D getPlane() {
		return plane;
	}

	protected GeoPointND getPoint() {
		return point;
	}

	abstract protected GeoElement getSecondInput();

	@Override
	public String toString(StringTemplate tpl) {
		return getLoc().getPlain("PlaneThroughAParallelToB",
				point.getLabel(tpl), getSecondInput().getLabel(tpl));

	}
}
