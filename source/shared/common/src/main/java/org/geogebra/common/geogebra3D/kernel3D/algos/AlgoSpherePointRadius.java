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
import org.geogebra.common.kernel.algos.AlgoSphereNDPointRadius;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoQuadricND;

/**
 *
 * @author Markus added TYPE_SEGMENT Michael Borcherds 2008-03-14
 */
public class AlgoSpherePointRadius extends AlgoSphereNDPointRadius {

	/**
	 * @param cons
	 *            construction
	 * @param M
	 *            center
	 * @param r
	 *            radius
	 */
	public AlgoSpherePointRadius(Construction cons, GeoPointND M,
			GeoNumberValue r) {
		super(cons, M, r);
	}

	@Override
	protected GeoQuadricND createSphereND(Construction cons1) {
		return new GeoQuadric3D(cons1);
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
		return getLoc().getPlain("SphereWithCenterAandRadiusB",
				getM().getLabel(tpl), getRGeo().getLabel(tpl));
	}

}
