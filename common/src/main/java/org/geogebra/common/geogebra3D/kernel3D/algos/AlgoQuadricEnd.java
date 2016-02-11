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
import org.geogebra.common.kernel.Matrix.CoordMatrix;
import org.geogebra.common.kernel.Matrix.CoordSys;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoQuadricNDConstants;
import org.geogebra.common.util.debug.Log;

/**
 * Compute one end of a limited quadric
 *
 * @author matthieu
 */
public abstract class AlgoQuadricEnd extends AlgoElement3D {

	private GeoQuadric3DLimited quadric; // input
	private GeoConic3D section; // output
	private CoordSys coordsys;

	// private GeoPoint3D help;
	/**
	 * 
	 * @param cons
	 *            construction
	 * @param quadric
	 *            quadric
	 */
	public AlgoQuadricEnd(Construction cons, GeoQuadric3DLimited quadric) {
		super(cons);

		this.quadric = quadric;
		section = new GeoConic3D(cons);
		coordsys = new CoordSys(2);
		section.setCoordSys(coordsys);
		section.setIsEndOfQuadric(true);
		// help = new GeoPoint3D(cons);
		// help.setLabel("help");
		setInputOutput(new GeoElement[] { quadric },
				new GeoElement[] { section });

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

	private CoordMatrix pm = new CoordMatrix(4, 3);
	private CoordMatrix pmt = new CoordMatrix(3, 4);

	@Override
	public final void compute() {

		if (!quadric.isDefined()) {
			section.setUndefined();
			return;
		}

		section.setDefined();
		if (quadric.getType() == GeoQuadricNDConstants.QUADRIC_PARABOLIC_CYLINDER) {
			CoordMatrix qm = quadric.getSymetricMatrix();
			Coords d = quadric.getEigenvec3D(1);
			Coords o1 = quadric.getMidpoint3D().add(
					d.mul(quadric.getBottomParameter()));
			Coords o2 = quadric.getMidpoint3D().add(
					d.mul(quadric.getTopParameter()));
			pm.setOrigin(getOrigin(o1, o2));
		//	help.setCoords(o2.getX(), o2.getY(), o2.getZ(),
			// 1);
		//	help.update();
			Coords[] v = new Coords[3];// d.completeOrthonormal();
			v[2] = d;
			v[0] = quadric.getEigenvec3D(0).normalize();
			v[1] = quadric.getEigenvec3D(2).normalize();
			pm.setVx(v[0]);
			pm.setVy(v[1]);
			/*
			 * Coords[] v = d.completeOrthonormal();
			 */
			pm.transposeCopy(pmt);

			// sets the conic matrix from plane and quadric matrix

			CoordMatrix cm = pmt.mul(qm).mul(pm);

			// Application.debug("pm=\n"+pm+"\nqm=\n"+qm+"\ncm=\n"+cm);

			coordsys.resetCoordSys();
			coordsys.addPoint(getOrigin(o1, o2));
			coordsys.addVector(v[0]);
			coordsys.addVector(getV1(v[1]).mul(-1));
			coordsys.makeOrthoMatrix(false, false);
			section.setMatrix(cm);
			if (quadric.getBottom() != null) {
				double dd = quadric.getBottom().p;
				Log.debug(dd + "," + section.p);
				// qm.set(3, 1, dd);
				// qm.set(1, 3, dd);
			}
		} else {
		CoordMatrix qm = quadric.getSymetricMatrix();
		Coords d = quadric.getEigenvec3D(2);
		Coords o1 = quadric.getMidpoint3D().add(
				d.mul(quadric.getBottomParameter()));
		Coords o2 = quadric.getMidpoint3D().add(
				d.mul(quadric.getTopParameter()));
		pm.setOrigin(getOrigin(o1, o2));
		Coords[] v = new Coords[3];// d.completeOrthonormal();
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
		CoordMatrix cm = pmt.mul(qm).mul(pm);

		// Application.debug("pm=\n"+pm+"\nqm=\n"+qm+"\ncm=\n"+cm);

		coordsys.resetCoordSys();
			coordsys.addPoint(getOrigin(o1, o2));
			coordsys.addVector(v[0]);
			coordsys.addVector(getV1(v[1].mul(0.5)));
		coordsys.makeOrthoMatrix(false, false);

		section.setMatrix(cm);
		}
		// areas
		// section.calcArea();

	}

	private boolean isHelperAlgo = false;

	/**
	 * says that it's an helper algo for quadric limites (cone/cylinder)
	 */
	public void setIsHelperAlgo() {
		isHelperAlgo = true;
	}

	@Override
	public void remove() {
		if (removed)
			return;
		super.remove();
		// if is helper algo for a quadric, remove it
		if (isHelperAlgo)
			quadric.remove();
	}

	abstract protected Coords getOrigin(Coords o1, Coords o2);

	// orientation out of the quadric
	abstract protected Coords getV1(Coords v1);

}
