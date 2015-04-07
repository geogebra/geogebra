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
import org.geogebra.common.kernel.kernelND.GeoSegmentND;

/**
 * Compute the segment bisector, and parallel to a plane
 *
 * @author mathieu
 * @version
 */
public class AlgoLineBisectorSegmentDirection3D extends AlgoElement3D {

	private GeoSegmentND segment; // input
	private GeoDirectionND direction; // input

	private GeoLine3D line; // output

	public AlgoLineBisectorSegmentDirection3D(Construction cons, String label,
			GeoSegmentND segment, GeoDirectionND direction) {
		super(cons);
		this.segment = segment;
		this.direction = direction;
		line = new GeoLine3D(cons);

		setInputOutput(new GeoElement[] { (GeoElement) segment,
				(GeoElement) direction }, new GeoElement[] { line });

		// compute line
		compute();
		line.setLabel(label);
	}

	public GeoLine3D getLine() {
		return line;
	}

	@Override
	public Commands getClassName() {
		return Commands.LineBisector;
	}

	@Override
	public void compute() {

		if (direction == kernel.getSpace()) {
			line.setUndefined();
			return;
		}

		Coords d = segment.getDirectionInD3().crossProduct(
				direction.getDirectionInD3());
		if (d.isZero()) {
			line.setUndefined();
		} else {
			Coords midpoint = segment.getStartInhomCoords()
					.add(segment.getEndInhomCoords()).mul(0.5);
			line.setCoord(midpoint, d);
		}

	}

	@Override
	public String toString(StringTemplate tpl) {
		// direction is plane
		if (direction instanceof GeoCoordSys2D)
			return getLoc().getPlain("LineBisectorOfAParallelToB",
					segment.getLabel(tpl), direction.getLabel(tpl));
		// direction is line
		return getLoc().getPlain("LineBisectorOfAPerpendicularToB",
				segment.getLabel(tpl), direction.getLabel(tpl));

	}

}
