/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * AlgoCirclePointRadius.java
 *
 * Created on 15. November 2001, 21:37
 */

package org.geogebra.common.geogebra3D.kernel3D.algos;

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoQuadric3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.AlgoSphereNDPointRadius;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoQuadricND;

/**
 *
 * @author Markus added TYPE_SEGMENT Michael Borcherds 2008-03-14
 * @version
 */
public class AlgoSpherePointRadius extends AlgoSphereNDPointRadius {

	public AlgoSpherePointRadius(Construction cons, String label, GeoPointND M,
			NumberValue r) {

		super(cons, label, M, r);
	}

	public AlgoSpherePointRadius(Construction cons, GeoPointND M, NumberValue r) {

		super(cons, M, r);
	}

	@Override
	protected GeoQuadricND createSphereND(Construction cons) {
		return new GeoQuadric3D(cons);
	}

	@Override
	public Commands getClassName() {
		return Commands.Sphere;
	}

	public GeoQuadric3D getSphere() {
		return (GeoQuadric3D) getSphereND();
	}

	@Override
	final public String toString(StringTemplate tpl) {
		return getLoc().getPlain("SphereWithCenterAandRadiusB",
				getM().getLabel(tpl), getRGeo().getLabel(tpl));
	}

	// TODO Consider locusequability
}
