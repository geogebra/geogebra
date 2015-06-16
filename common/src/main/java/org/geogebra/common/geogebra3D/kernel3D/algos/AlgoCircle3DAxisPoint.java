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
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.Matrix.CoordSys;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoPointND;

/**
 * Compute a circle with axis and through a point
 *
 * @author matthieu
 * @version
 */
public class AlgoCircle3DAxisPoint extends AlgoElement3D {

	private GeoLineND axis; // input
	private GeoPointND point; // input
	private GeoConic3D circle; // output
	private CoordSys coordsys;

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

	private Coords center = Coords.createInhomCoorsInD3();

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

	static final private Coords O = new Coords(0, 0);

	@Override
	public Commands getClassName() {
		return Commands.Circle;
	}

	@Override
	final public String toString(StringTemplate tpl) {
		return getLoc().getPlain("CircleOfAxisAThroughB",
				((GeoElement) axis).getLabel(tpl), point.getLabel(tpl));
	}

	// TODO Consider locusequability
}
