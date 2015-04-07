/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.geogebra3D.kernel3D.algos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoPointND;

/**
 * Compute a plane through a point and orthogonal to ...
 *
 * @author matthieu
 * @version
 */
public abstract class AlgoOrthoPlanePoint extends AlgoOrthoPlane {

	private GeoPointND point; // input
	private GeoElement secondInput; // input

	/**
	 * 
	 * @param cons
	 * @param label
	 * @param point
	 * @param secondInput
	 */
	public AlgoOrthoPlanePoint(Construction cons, String label,
			GeoPointND point, GeoElement secondInput) {
		super(cons);
		this.point = point;
		this.secondInput = secondInput;

		setInputOutput(new GeoElement[] { (GeoElement) point, secondInput },
				new GeoElement[] { getPlane() });

		// compute plane
		compute();
		getPlane().setLabel(label);
	}

	@Override
	public Commands getClassName() {
		return Commands.OrthogonalPlane;
	}

	@Override
	protected Coords getPoint() {
		return point.getInhomCoordsInD3();
	}

	/**
	 * 
	 * @return second input
	 */
	protected GeoElement getSecondInput() {
		return secondInput;
	}

	@Override
	final public String toString(StringTemplate tpl) {
		return getLoc().getPlain("PlaneThroughAPerpendicularToB",
				point.getLabel(tpl), secondInput.getLabel(tpl));

	}
}
