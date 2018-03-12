/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * AlgoCircleTwoPoints.java
 *
 * Created on 15. November 2001, 21:37
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
