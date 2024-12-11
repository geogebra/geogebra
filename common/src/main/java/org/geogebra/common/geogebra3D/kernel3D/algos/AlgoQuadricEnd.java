/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.geogebra3D.kernel3D.algos;

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoConic3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoQuadric3DLimited;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoQuadricNDConstants;
import org.geogebra.common.kernel.matrix.CoordMatrix;
import org.geogebra.common.kernel.matrix.CoordSys;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.util.DoubleUtil;

/**
 * Compute one end of a limited quadric
 *
 * @author Mathieu
 */
public abstract class AlgoQuadricEnd extends AlgoElement3D {

	private GeoQuadric3DLimited quadric; // input
	private GeoConic3D section; // output
	private CoordSys coordsys;
	private CoordMatrix pm = new CoordMatrix(4, 3);
	private CoordMatrix pmt = new CoordMatrix(3, 4);
	private boolean isHelperAlgo = false;

	// private GeoPoint3D help;
	/**
	 * 
	 * @param cons
	 *            construction
	 * @param quadric
	 *            quadric
	 */
	public AlgoQuadricEnd(Construction cons, GeoQuadric3DLimited quadric) {
		this(cons, quadric, false);
	}

	/**
	 * @param cons
	 *            construction
	 * @param quadric
	 *            quadric
	 * @param helper
	 *            whether this is helper (don't add to construction)
	 */
	public AlgoQuadricEnd(Construction cons, GeoQuadric3DLimited quadric,
			boolean helper) {
		super(cons, !helper);

		this.quadric = quadric;
		section = new GeoConic3D(cons);
		coordsys = new CoordSys(2);
		section.setCoordSys(coordsys);
		section.setIsEndOfQuadric(true);
		if (!helper) {
			setInputOutput(new GeoElement[] { quadric },
					new GeoElement[] { section });
		}

		compute();
	}

	/**
	 * 
	 * @param cons
	 *            construction
	 * @param label
	 *            label
	 * @param quadric
	 *            quadric
	 */
	public AlgoQuadricEnd(Construction cons, String label,
			GeoQuadric3DLimited quadric) {
		this(cons, quadric);
		section.setLabel(label);
	}

	/**
	 * 
	 * @return section
	 */
	public GeoConic3D getSection() {
		return section;
	}

	@Override
	public final void compute() {

		if (!quadric.isDefined()) {
			section.setUndefined();
			return;
		}

		section.setDefined();
		if (quadric
				.getType() == GeoQuadricNDConstants.QUADRIC_PARABOLIC_CYLINDER) {

			Coords d = quadric.getEigenvec3D(1).normalize();

			Coords o1 = quadric.getMidpoint3D().copy().addInsideMul(d,
					quadric.getBottomParameter());
			Coords o2 = quadric.getMidpoint3D().copy().addInsideMul(d,
					quadric.getTopParameter());
			pm.setOrigin(getOrigin(o1, o2));
			Coords[] v = new Coords[3]; // d.completeOrthonormal();
			v[2] = quadric.getEigenvec3D(2).normalize();
			v[0] = quadric.getEigenvec3D(0).normalize();
			v[1] = d;

			pm.setVx(v[0]);
			pm.setVy(v[2]);
			/*
			 * Coords[] v = d.completeOrthonormal();
			 */
			pm.transposeCopy(pmt);

			// sets the conic matrix from plane and quadric matrix
			CoordMatrix qm = quadric.getSymmetricMatrix();
			CoordMatrix cm = pmt.mul(qm).mul(pm);

			coordsys.resetCoordSys();
			coordsys.addPoint(getOrigin(o1, o2));
			coordsys.addVector(v[0]);
			coordsys.addVector(getV1(v[2]));
			coordsys.makeOrthoMatrix(false, false);
			section.setMatrix(cm);
		} else {

			Coords d = quadric.getEigenvec3D(2);
			Coords o1 = quadric.getMidpoint3D().copy().addInsideMul(d,
					quadric.getBottomParameter());
			Coords o2 = quadric.getMidpoint3D().copy().addInsideMul(d,
					quadric.getTopParameter());

			if (quadric.getType() == GeoQuadricNDConstants.QUADRIC_CYLINDER
					|| quadric
							.getType() == GeoQuadricNDConstants.QUADRIC_CONE) {
				// cylinder or cone equal to a segment
				if (DoubleUtil.isZero(quadric.getHalfAxis(0))
						&& (Double.isNaN(quadric.getHalfAxis(1))
								|| DoubleUtil.isZero(quadric.getHalfAxis(1)))) {
					section.setSinglePoint(getOrigin(o1, o2));
					return;
				}
			}

			pm.setOrigin(getOrigin(o1, o2));
			Coords[] v = new Coords[3]; // d.completeOrthonormal();
			v[2] = d;
			v[0] = quadric.getEigenvec3D(0).normalize();
			v[1] = quadric.getEigenvec3D(1).normalize();
			pm.setVx(v[0]);
			pm.setVy(v[1]);
			/*
			 * Coords[] v = d.completeOrthonormal();
			 */
			pm.transposeCopy(pmt);

			// sets the conic matrix from plane and quadric matrix
			CoordMatrix qm = quadric.getSymmetricMatrix();
			CoordMatrix cm = pmt.mul(qm).mul(pm);

			coordsys.resetCoordSys();
			coordsys.addPoint(getOrigin(o1, o2));
			coordsys.addVector(v[0]);
			coordsys.addVector(getV1(v[1]));
			coordsys.makeOrthoMatrix(false, false);

			section.setMatrix(cm);
		}
		// areas
		// section.calcArea();

	}

	/**
	 * says that it's an helper algo for quadric limites (cone/cylinder)
	 */
	public void setIsHelperAlgo() {
		isHelperAlgo = true;
	}

	@Override
	public void remove() {
		if (removed) {
			return;
		}
		super.remove();
		// if is helper algo for a quadric, remove it
		if (isHelperAlgo) {
			quadric.remove();
		}
	}

	/**
	 * @param o1
	 *            bottom origin
	 * @param o2
	 *            top origin
	 * @return origin of given end
	 */
	abstract protected Coords getOrigin(Coords o1, Coords o2);

	/**
	 * 
	 * @param v1
	 *            orientation out of the quadric
	 * @return orientation out of the end
	 */
	abstract protected Coords getV1(Coords v1);

}
