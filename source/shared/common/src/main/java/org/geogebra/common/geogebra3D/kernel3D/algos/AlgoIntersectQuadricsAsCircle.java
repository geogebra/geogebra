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
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoQuadricND;
import org.geogebra.common.kernel.kernelND.GeoQuadricNDConstants;
import org.geogebra.common.kernel.matrix.CoordSys;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.util.DoubleUtil;

/**
 *
 * @author ggb3D
 * 
 */
public class AlgoIntersectQuadricsAsCircle extends AlgoElement3D {

	// inputs
	private GeoQuadricND quadric1;
	private GeoQuadricND quadric2;

	// output
	/** intersection */
	protected GeoConic3D circle;
	private Coords o = new Coords(3);
	private Coords v = new Coords(3);
	private Coords vn1 = new Coords(3);
	private Coords vn2 = new Coords(3);

	/**
	 * Creates new AlgoIntersectLinePlane
	 * 
	 * @param cons
	 *            the construction
	 * @param labels
	 *            names
	 * @param quadric1
	 *            first quadric
	 * @param quadric2
	 *            second quadric
	 */
	AlgoIntersectQuadricsAsCircle(Construction cons, String[] labels,
			GeoQuadricND quadric1, GeoQuadricND quadric2) {

		this(cons, quadric1, quadric2);
		if (labels != null && labels.length > 0) {
			circle.setLabel(labels[0]);
		} else {
			circle.setLabel(null);
		}

	}

	/**
	 * Creates new AlgoIntersectLinePlane
	 * 
	 * @param cons
	 *            the construction
	 * @param quadric1
	 *            first quadric
	 * @param quadric2
	 *            second quadric
	 */
	AlgoIntersectQuadricsAsCircle(Construction cons, GeoQuadricND quadric1,
			GeoQuadricND quadric2) {

		super(cons);

		this.quadric1 = quadric1;
		this.quadric2 = quadric2;

		circle = new GeoConic3D(cons, true);
		circle.setCoordSys(new CoordSys(2));

		setInputOutput(new GeoElement[] { quadric1, quadric2 },
				new GeoElement[] { circle });

		compute();

	}

	/**
	 * return the intersection
	 * 
	 * @return the intersection
	 */
	public GeoConic3D getConic() {
		return circle;
	}

	// /////////////////////////////////////////////
	// COMPUTE

	@Override
	public void compute() {

		if (!quadric1.isDefined() || !quadric2.isDefined()) {
			circle.setUndefined();
			return;
		}

		circle.setDefined();

		if (quadric1.getType() == GeoQuadricNDConstants.QUADRIC_SPHERE) {
			if (quadric2.getType() == GeoQuadricNDConstants.QUADRIC_SPHERE) {
				// intersect sphere / sphere
				Coords o1 = quadric1.getMidpoint3D();
				double r1 = quadric1.getHalfAxis(0);
				Coords o2 = quadric2.getMidpoint3D();
				double r2 = quadric2.getHalfAxis(0);

				// same center
				if (o1.equalsForKernel(o2)) {
					if (DoubleUtil.isZero(r1) && DoubleUtil.isZero(r2)) {
						// single point
						GeoConic3D.setSinglePoint(circle, o1);
						return;
					}

					if (DoubleUtil.isEqual(r1, r2)) {
						// undefined
						circle.setUndefined();
						return;
					}

					// empty conic
					circle.empty();
					return;
				}

				// different centers
				v.setSub3(o2, o1);
				v.calcNorm();
				double d = v.getNorm();

				if (DoubleUtil.isGreater(d, r1 + r2)) {
					// no intersection : empty
					circle.empty();
					return;
				}

				v.mulInside3(1 / d);
				double x = (d + (r1 * r1 - r2 * r2) / d) / 2;
				o.setAdd3(o1, o.setMul(v, x));

				v.completeOrthonormal3(vn1, vn2);
				CoordSys coordSys = circle.getCoordSys();
				coordSys.resetCoordSys();
				coordSys.addPoint(o);
				coordSys.addVector(vn1);
				coordSys.addVector(vn2);
				coordSys.makeOrthoMatrix(false, false);
				circle.setSphereND(new Coords(0, 0),
						Math.sqrt(r1 * r1 - x * x));

				return;

			}
		}

		// other cases
		circle.setUndefined();

	}

	@Override
	public Commands getClassName() {
		return Commands.IntersectConic;
	}

	@Override
	final public String toString(StringTemplate tpl) {
		StringBuilder sb = new StringBuilder();

		sb.append(getLoc().getPlain("IntersectionCircleOfAB",
				quadric1.getLabel(tpl), quadric2.getLabel(tpl)));

		return sb.toString();
	}

}
