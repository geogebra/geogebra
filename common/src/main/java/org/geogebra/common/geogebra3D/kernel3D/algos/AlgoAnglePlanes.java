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
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.algos.AlgoAngle;
import org.geogebra.common.kernel.algos.DrawInformationAlgo;
import org.geogebra.common.kernel.geos.GeoAngle;
import org.geogebra.common.kernel.geos.GeoElement;

/**
 *
 * @author mathieu
 */
public class AlgoAnglePlanes extends AlgoAngle implements DrawInformationAlgo {

	private GeoPlane3D p, q; // input
	private GeoAngle angle; // output

	/**
	 * Creates new unlabeled angle between line and plane
	 * 
	 * @param cons
	 *            construction
	 * @param p
	 *            plane
	 * @param q
	 *            plane
	 */
	AlgoAnglePlanes(Construction cons, GeoPlane3D p, GeoPlane3D q) {
		super(cons);
		this.p = p;
		this.q = q;
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

	private AlgoAnglePlanes(GeoPlane3D p, GeoPlane3D q) {
		super(p.getConstruction(), false);
		this.p = p;
		this.q = q;
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

	public AlgoAnglePlanes(Construction cons, String label, GeoPlane3D p,
			GeoPlane3D q) {
		this(cons, p, q);
		angle.setLabel(label);
	}

	public AlgoAnglePlanes copy() {
		return new AlgoAnglePlanes(p.copy(), q.copy());
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[2];
		input[0] = p;
		input[1] = q;

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
		return getLoc().getPlain("AngleBetweenAB", p.getLabel(tpl),
				q.getLabel(tpl));

	}

	private Coords vn, o, v1, v2;

	@Override
	protected void initCoords() {
		o = new Coords(4);
	}

	@Override
	public final void compute() {

		Coords vn1 = p.getDirectionInD3();
		Coords vn2 = q.getDirectionInD3();

		vn = vn1.crossProduct4(vn2).normalize();

		// compute origin
		if (vn.isZero()) { // parallel planes
			getAngle().setValue(0);
			o = Coords.UNDEFINED;
			return;
		}

		getAngle().setValue(AlgoAnglePoints3D.acos(vn1.dotproduct(vn2)));

		v2 = vn1.crossProduct4(vn);
		v1 = vn2.crossProduct4(vn);

		// projection of first plane origin on second plane
		// direction orthogonal to v and colinear to first plane
		p.getCoordSys()
				.getMatrixOrthonormal()
				.getOrigin()
				.projectPlaneThruV(q.getCoordSys().getMatrixOrthonormal(), v2,
						o);

	}

	@Override
	public boolean updateDrawInfo(double[] m, double[] firstVec,
			DrawAngle drawable) {
		return false;
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
