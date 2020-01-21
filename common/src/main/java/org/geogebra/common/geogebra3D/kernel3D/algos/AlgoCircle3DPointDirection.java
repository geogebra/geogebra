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
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoDirectionND;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.matrix.CoordSys;
import org.geogebra.common.kernel.matrix.Coords;

/**
 * Compute a circle with point and radius (missing direction)
 *
 * @author matthieu
 */
public abstract class AlgoCircle3DPointDirection extends AlgoElement3D {

	protected GeoPointND point; // input
	protected GeoElementND secondInput; // input
	protected GeoElementND forAxis; // input
	private GeoConic3D circle; // output
	protected CoordSys coordsys;

	/**
	 * 
	 * @param cons
	 *            construction
	 * @param point
	 *            center
	 * @param secondInput
	 *            point, radius or direction
	 * @param forAxis
	 *            point in plane
	 */
	protected AlgoCircle3DPointDirection(Construction cons, GeoPointND point,
			GeoElementND secondInput, GeoElementND forAxis) {
		super(cons);

		this.point = point;
		this.forAxis = forAxis;
		this.secondInput = secondInput;
		circle = new GeoConic3D(cons);
		coordsys = new CoordSys(2);
		circle.setCoordSys(coordsys);

		setInputOutput(new GeoElement[] { (GeoElement) point,
				(GeoElement) secondInput, (GeoElement) forAxis },
				new GeoElement[] { circle });

		// compute line
		compute();
	}

	/**
	 * 
	 * @return the circle
	 */
	public GeoConic3D getCircle() {
		return circle;
	}

	@Override
	public final void compute() {

		if (setCoordSys()) {
			// set the circle
			circle.setDefined();
			circle.setSphereND(new Coords(0, 0), getRadius());
		} else {
			circle.setUndefined();
		}

	}

	/**
	 * reset the coord sys
	 * 
	 * @return true if coord sys can be set
	 */
	protected boolean setCoordSys() {

		coordsys.resetCoordSys();

		coordsys.addPoint(point.getInhomCoordsInD3());
		Coords[] v = ((GeoDirectionND) forAxis).getDirectionInD3()
				.completeOrthonormal();
		coordsys.addVector(v[0]);
		coordsys.addVector(v[1]);

		coordsys.makeOrthoMatrix(false, false);

		return true;
	}

	/**
	 * 
	 * @return the radius
	 */
	protected abstract double getRadius();

	/**
	 * 
	 * @return center
	 */
	protected GeoPointND getCenter() {
		return point;
	}

	/**
	 * 
	 * @return direction
	 */
	protected Coords getDirection() {
		return ((GeoDirectionND) forAxis).getDirectionInD3();
	}

	/**
	 * 
	 * @return second input (radius or point)
	 */
	protected GeoElementND getSecondInput() {
		return secondInput;
	}

	/**
	 * 
	 * @return direction of the axis
	 */
	protected GeoElementND getForAxis() {
		return forAxis;
	}

	@Override
	public Commands getClassName() {
		return Commands.Circle;
	}

	@Override
	final public String toString(StringTemplate tpl) {
		return getLoc().getPlain(getCommandString(),
				point.getLabel(tpl), secondInput.getLabel(tpl),
				forAxis.getLabel(tpl));
	}

	/**
	 * 
	 * @return command string
	 */
	abstract protected String getCommandString();

	/*
	 * This should apply to every subclass. In case it does not, a case per case
	 * should be used.
	 */

}
