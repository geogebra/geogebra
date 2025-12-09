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

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoQuadric3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.AlgoSphereNDTwoPoints;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoQuadricND;

/**
 *
 * @author Mathieu
 */
public class AlgoSphereTwoPoints extends AlgoSphereNDTwoPoints {

	/**
	 * @param cons
	 *            construction
	 * @param M
	 *            center
	 * @param P
	 *            point on sphere
	 */
	public AlgoSphereTwoPoints(Construction cons, GeoPointND M, GeoPointND P) {
		super(cons, M, P);
	}

	@Override
	protected GeoQuadricND createSphereND(Construction cons1) {
		GeoQuadric3D sphere = new GeoQuadric3D(cons1);
		// circle.addPointOnConic((GeoPoint) getP()); //TODO do this in
		// AlgoSphereNDTwoPoints
		return sphere;
	}

	@Override
	public Commands getClassName() {
		return Commands.Sphere;
	}

	/**
	 * @return resulting sphere
	 */
	public GeoQuadric3D getSphere() {
		return (GeoQuadric3D) getSphereND();
	}

	@Override
	final public String toString(StringTemplate tpl) {
		return getLoc().getPlain("SphereThroughAwithCenterB",
				getP().getLabel(tpl), getM().getLabel(tpl));
	}

}
