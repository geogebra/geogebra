/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.geogebra3D.kernel3D.algos;

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoSpace;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoPointND;

/**
 * Compute a plane through a point and orthogonal to a line (or segment, ...)
 *
 * @author mathieu
 */
public class AlgoOrthoLinePointLine3D extends AlgoOrtho {

	public AlgoOrthoLinePointLine3D(Construction cons, String label,
			GeoPointND point, GeoLineND line) {
		super(cons, label, point, (GeoElement) line);
	}

	@Override
	protected void setSpecificInputOutput() {
		setInputOutput(new GeoElement[] { (GeoElement) point, inputOrtho,
				(GeoSpace) cons.getSpace() }, new GeoElement[] { line });
	}

	@Override
	public Commands getClassName() {
		return Commands.OrthogonalLine;
	}

	private GeoLineND getInputLine() {
		return (GeoLineND) getInputOrtho();
	}

	@Override
	public final void compute() {

		GeoLineND line = getInputLine();
		Coords o = line.getPointInD(3, 0).getInhomCoordsInSameDimension();
		Coords v1 = line.getPointInD(3, 1).getInhomCoordsInSameDimension()
				.sub(o);
		Coords o2 = getPoint().getInhomCoordsInD3();
		Coords v2 = o2.sub(o);

		Coords v3 = v1.crossProduct(v2);
		Coords v = v3.crossProduct(v1);

		if (v.equalsForKernel(0, Kernel.STANDARD_PRECISION))
			getLine().setUndefined();
		else
			getLine().setCoord(getPoint().getInhomCoordsInD3(), v.normalize());

	}

	@Override
	final public String toString(StringTemplate tpl) {
		return getLoc().getPlain("LineThroughAPerpendicularToBinSpace",
				point.getLabel(tpl), inputOrtho.getLabel(tpl));
	}

	

}
