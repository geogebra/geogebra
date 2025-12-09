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
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.matrix.CoordSys;
import org.geogebra.common.kernel.matrix.Coords;

/**
 * Compute a circle with axis and through a point
 *
 * @author Mathieu
 */
public class AlgoCircle3DAxisPoint extends AlgoElement3D {

	private GeoLineND axis; // input
	private GeoPointND point; // input
	private GeoConic3D circle; // output
	private CoordSys coordsys;

	private Coords center = Coords.createInhomCoorsInD3();

	static final private Coords O = new Coords(0, 0);

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param axis
	 *            axis
	 * @param point
	 *            center
	 */
	public AlgoCircle3DAxisPoint(Construction cons, String label,
			GeoLineND axis, GeoPointND point) {
		super(cons);

		this.axis = axis;
		this.point = point;
		circle = new GeoConic3D(cons);
		coordsys = new CoordSys(2);
		circle.setCoordSys(coordsys);

		setInputOutput(
				new GeoElement[] { (GeoElement) axis, (GeoElement) point },
				new GeoElement[] { circle });

		// compute line
		compute();
		circle.setLabel(label);
	}

	/**
	 * 
	 * @return circle
	 */
	public GeoConic3D getCircle() {
		return circle;
	}

	@Override
	public final void compute() {

		Coords p = point.getInhomCoordsInD3();
		Coords o = axis.getPointInD(3, 0).getInhomCoordsInSameDimension();
		Coords d = axis.getDirectionInD3();

		// project the point on the axis
		p.projectLine(o, d, center, null);

		Coords v1 = p.sub(center);

		setCircle(circle, coordsys, center, v1, d);

	}

	/**
	 * set conic to circle with center, radius vector, axis direction
	 * 
	 * @param conic
	 *            conic
	 * @param coordsys
	 *            coord sys
	 * @param center
	 *            center
	 * @param v1
	 *            radius vector
	 * @param d
	 *            axis direction
	 */
	static final public void setCircle(GeoConicND conic, CoordSys coordsys,
			Coords center, Coords v1, Coords d) {

		// recompute the coord sys
		coordsys.resetCoordSys();

		coordsys.addPoint(center);
		coordsys.addVector(v1);
		coordsys.addVector(d.crossProduct(v1));

		coordsys.makeOrthoMatrix(false, false);

		// set the circle
		v1.calcNorm();
		conic.setDefined();
		conic.setSphereND(O, v1.getNorm());
	}

	@Override
	public Commands getClassName() {
		return Commands.Circle;
	}

	@Override
	final public String toString(StringTemplate tpl) {
		return getLoc().getPlain("CircleOfAxisAThroughB",
				axis.getLabel(tpl), point.getLabel(tpl));
	}

}
