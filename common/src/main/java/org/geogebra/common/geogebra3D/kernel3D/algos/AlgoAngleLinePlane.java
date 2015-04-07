/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * AlgoAngleLines.java
 *
 * Created on 30. August 2001, 21:37
 */

package org.geogebra.common.geogebra3D.kernel3D.algos;

import org.geogebra.common.euclidian.draw.DrawAngle;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoAngle3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPlane3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.Matrix.CoordMatrix4x4;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.algos.AlgoAngle;
import org.geogebra.common.kernel.algos.DrawInformationAlgo;
import org.geogebra.common.kernel.geos.GeoAngle;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoLineND;

/**
 *
 * @author mathieu
 */
public class AlgoAngleLinePlane extends AlgoAngle implements
		DrawInformationAlgo {

	private GeoLineND g; // input
	private GeoPlane3D p; // input
	private GeoAngle angle; // output

	/**
	 * Creates new unlabeled angle between line and plane
	 * 
	 * @param cons
	 *            construction
	 * @param g
	 *            line
	 * @param p
	 *            plane
	 */
	AlgoAngleLinePlane(Construction cons, GeoLineND g, GeoPlane3D p) {
		super(cons);
		this.g = g;
		this.p = p;
		angle = newGeoAngle(cons);
		setInputOutput(); // for AlgoElement

		// compute angle
		compute();

	}

	@Override
	final protected GeoAngle newGeoAngle(Construction cons) {
		GeoAngle ret = new GeoAngle3D(cons);
		ret.setDrawable(true);
		return ret;
	}

	private AlgoAngleLinePlane(GeoLineND g, GeoPlane3D p) {
		super(((GeoElement) g).getConstruction(), false);
		this.g = g;
		this.p = p;
	}

	/**
	 * Creates new labeled angle between line and plane
	 * 
	 * @param cons
	 *            construction
	 * @param label
	 *            angle label
	 * @param g
	 *            line
	 * @param p
	 *            plane
	 */

	public AlgoAngleLinePlane(Construction cons, String label, GeoLineND g,
			GeoPlane3D p) {
		this(cons, g, p);
		angle.setLabel(label);
	}

	public AlgoAngleLinePlane copy() {
		return new AlgoAngleLinePlane(g.copy(), p.copy());
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[2];
		input[0] = (GeoElement) g;
		input[1] = p;

		setOutputLength(1);
		setOutput(0, angle);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * Returns the resulting angle
	 * 
	 * @return resulting angle
	 */
	public GeoAngle getAngle() {
		return angle;
	}

	@Override
	final public String toString(StringTemplate tpl) {
		// Michael Borcherds 2008-03-30
		// simplified to allow better Chinese translation
		return getLoc().getPlain("AngleBetweenAB", g.getLabel(tpl),
				p.getLabel(tpl));

	}

	private Coords vn, o, v1, v2;

	@Override
	protected void initCoords() {
		o = new Coords(4);
	}

	@Override
	public final void compute() {

		// line origin and direction
		Coords o2 = g.getStartInhomCoords();
		v2 = g.getDirectionInD3();

		// plane matrix
		CoordMatrix4x4 pMat = p.getCoordSys().getMatrixOrthonormal();

		// project line origin on the plane
		o2.projectPlaneThruV(pMat, v2, o);
		if (!o.isDefined()) { // line parallel to plane
			getAngle().setValue(0);
			return;
		}

		// project line direction on the plane
		Coords vx = pMat.getVx();
		Coords vy = pMat.getVy();
		v1 = vx.mul(v2.dotproduct(vx)).add(vy.mul(v2.dotproduct(vy)));
		if (v1.isZero()) { // line orthogonal to plane
			getAngle().setValue(Math.PI / 2);
			v1 = vx;
			vn = vy.mul(-1);
			return;
		}

		v1.calcNorm();
		double l1 = v1.getNorm();
		v2.calcNorm();
		double l2 = v2.getNorm();

		double c = v1.dotproduct(v2) / (l1 * l2); // cosinus of the angle

		getAngle().setValue(AlgoAnglePoints3D.acos(c));

		vn = v2.crossProduct4(v1).normalize();

	}

	@Override
	public boolean updateDrawInfo(double[] m, double[] firstVec,
			DrawAngle drawable) {

		if (drawable == null) { // TODO : this is a pgf / asymptote / pstricks
								// call
			return false;
		}

		if (!o.isDefined()) {
			return false;
		}

		Coords ov = drawable.getCoordsInView(o);
		if (!drawable.inView(ov)) {
			return false;
		}

		m[0] = ov.get()[0];
		m[1] = ov.get()[1];

		Coords v1v = drawable.getCoordsInView(v2);
		if (!drawable.inView(v1v)) {
			return false;
		}

		Coords v2v = drawable.getCoordsInView(v1);
		if (!drawable.inView(v2v)) {
			return false;
		}

		firstVec[0] = v1v.get()[0];
		firstVec[1] = v1v.get()[1];

		return true;
	}

	@Override
	public Coords getVn() {
		return vn;
	}

	@Override
	public boolean getCoordsInD3(Coords[] drawCoords) {

		if (!o.isDefined()) {
			return false;
		}

		drawCoords[0] = o;
		drawCoords[1] = v2;
		drawCoords[2] = v1;

		return true;
	}

}
