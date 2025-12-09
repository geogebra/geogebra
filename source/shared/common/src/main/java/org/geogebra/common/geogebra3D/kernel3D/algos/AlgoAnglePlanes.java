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

import org.geogebra.common.euclidian.draw.DrawAngle;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoAngle3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPlane3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.AlgoAngle;
import org.geogebra.common.kernel.algos.DrawInformationAlgo;
import org.geogebra.common.kernel.geos.GeoAngle;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.matrix.Coords;

/**
 *
 * @author mathieu
 */
public class AlgoAnglePlanes extends AlgoAngle implements DrawInformationAlgo {

	private GeoPlane3D p; // input
	private GeoPlane3D q; // input
	private GeoAngle angle; // output

	private Coords vn;
	private Coords o;
	private Coords v1;
	private Coords v2;

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
	final protected GeoAngle newGeoAngle(Construction cons1) {
		GeoAngle ret = new GeoAngle3D(cons1);
		ret.setDrawableNoSlider();
		return ret;
	}

	private AlgoAnglePlanes(GeoPlane3D p, GeoPlane3D q) {
		super(p.getConstruction(), false);
		this.p = p;
		this.q = q;
	}

	@Override
	public AlgoAnglePlanes copy() {
		return new AlgoAnglePlanes(p.copy(), q.copy());
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[2];
		input[0] = p;
		input[1] = q;

		setOnlyOutput(angle);
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
		// direction orthogonal to v and collinear to first plane
		p.getCoordSys().getMatrixOrthonormal().getOrigin().projectPlaneThruV(
				q.getCoordSys().getMatrixOrthonormal(), v2, o);

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
