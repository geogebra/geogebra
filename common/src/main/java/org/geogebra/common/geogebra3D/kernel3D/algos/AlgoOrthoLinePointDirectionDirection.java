/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.geogebra3D.kernel3D.algos;

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoLine3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoCoordSys2D;
import org.geogebra.common.kernel.kernelND.GeoDirectionND;
import org.geogebra.common.kernel.kernelND.GeoPointND;

/**
 * Compute a line orthogonal to a line, through a point, and parallel to a plane
 *
 * @author matthieu
 * @version
 */
public class AlgoOrthoLinePointDirectionDirection extends AlgoElement3D {

	private GeoPointND point; // input
	private GeoDirectionND direction1, direction2; // input

	private GeoLine3D line; // output

	public AlgoOrthoLinePointDirectionDirection(Construction cons,
			String label, GeoPointND point, GeoDirectionND direction1,
			GeoDirectionND direction2) {
		super(cons);
		this.point = point;
		this.direction1 = direction1;
		this.direction2 = direction2;
		line = new GeoLine3D(cons);

		setInputOutput(new GeoElement[] { (GeoElement) point,
				(GeoElement) direction1, (GeoElement) direction2 },
				new GeoElement[] { getLine() });

		// compute line
		compute();
		getLine().setLabel(label);
	}

	public GeoLine3D getLine() {
		return line;
	}

	@Override
	public Commands getClassName() {
		return Commands.OrthogonalLine;
	}

	@Override
	public void compute() {

		Coords direction = direction1.getDirectionInD3().crossProduct(
				direction2.getDirectionInD3());
		if (direction.isZero())
			line.setUndefined();
		else
			line.setCoord(point.getInhomCoordsInD3(), direction);

	}

	// TODO Consider locusequability

	@Override
	public String toString(StringTemplate tpl) {
		// point, plane, line
		if (direction1 instanceof GeoCoordSys2D)
			return getLoc().getPlain("LineThroughAParallelToBPerpendicularToC",
					point.getLabel(tpl), direction1.getLabel(tpl),
					direction2.getLabel(tpl));
		// point, line, plane
		if (direction2 instanceof GeoCoordSys2D)
			return getLoc().getPlain("LineThroughAPerpendicularToBParallelToC",
					point.getLabel(tpl), direction1.getLabel(tpl),
					direction2.getLabel(tpl));
		// point, line, line
		return getLoc().getPlain("LineThroughAPerpendicularToBAndC",
				point.getLabel(tpl), direction1.getLabel(tpl),
				direction2.getLabel(tpl));

	}

}
