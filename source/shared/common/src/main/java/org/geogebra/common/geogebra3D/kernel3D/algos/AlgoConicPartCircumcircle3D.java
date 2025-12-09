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

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoConic3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoConicPart3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoCircleThreePoints;
import org.geogebra.common.kernel.algos.AlgoConicPartCircumcircleND;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.kernelND.GeoPointND;

/**
 * Circle arc or sector defined by three points.
 */
public class AlgoConicPartCircumcircle3D extends AlgoConicPartCircumcircleND {

	private AlgoCircle3DThreePoints algo;

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param A
	 *            start point
	 * @param B
	 *            point on arc
	 * @param C
	 *            end point
	 * @param type
	 *            arc or sector
	 */
	public AlgoConicPartCircumcircle3D(Construction cons, String label,
			GeoPointND A, GeoPointND B, GeoPointND C, int type) {
		super(cons, label, A, B, C, type);
	}

	@Override
	protected AlgoCircleThreePoints getAlgo() {
		algo = new AlgoCircle3DThreePoints(cons, A, B, C);
		return algo;
	}

	@Override
	protected GeoConicPart3D createConicPart(Construction cons0, int type0) {
		return new GeoConicPart3D(cons0, type0);
	}

	@Override
	protected void computeSinglePoint() {
		GeoConic3D.setSinglePoint((GeoConic3D) conicPart,
				A.getInhomCoordsInD(3));
		super.computeSinglePoint();
	}

	@Override
	final public GeoPoint getA() {
		return algo.getPoint2D(0);
	}

	/**
	 * Method for LocusEqu.
	 * 
	 * @return second point.
	 */
	@Override
	final public GeoPoint getB() {
		return algo.getPoint2D(1);
	}

	/**
	 * Method for LocusEqu.
	 * 
	 * @return third point.
	 */
	@Override
	final public GeoPoint getC() {
		return algo.getPoint2D(2);
	}

	@Override
	public GeoConicPart3D getConicPart() {
		return (GeoConicPart3D) super.getConicPart();
	}

}
