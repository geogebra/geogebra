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

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoVector3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoDirectionND;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.matrix.Coords;

/**
 *
 * @author ggb3D
 * 
 *         Calculate the ortho vector of a plane (or polygon, ...)
 * 
 */
public class AlgoOrthoVectorLineDirection extends AlgoElement3D {

	// input
	private GeoLineND line;
	private GeoDirectionND direction;

	// output
	/** ortho vector */
	private GeoVector3D vector;

	/**
	 * Creates new AlgoIntersectLinePlane
	 * 
	 * @param cons
	 *            the construction
	 * @param label
	 *            name of point
	 */
	AlgoOrthoVectorLineDirection(Construction cons, String label,
			GeoLineND line, GeoDirectionND direction) {

		super(cons);

		this.line = line;
		this.direction = direction;

		vector = new GeoVector3D(cons);

		setInputOutput(
				new GeoElement[] { (GeoElement) line, (GeoElement) direction },
				new GeoElement[] { vector });

		vector.setLabel(label);

	}

	/**
	 * return the ortho vector
	 * 
	 * @return the ortho vector
	 */
	public GeoVector3D getVector() {
		return vector;
	}

	// /////////////////////////////////////////////
	// COMPUTE

	@Override
	public void compute() {

		if (!line.isDefined()
				|| !direction.isDefined()) {
			vector.setUndefined();
			return;
		}

		Coords d1 = line.getDirectionInD3();
		Coords d2 = direction.getDirectionInD3();

		// this way to be consistent with 2D when d2 is xOy plane
		vector.setCoords(d2.crossProduct4(d1));

	}

	@Override
	public Commands getClassName() {

		return Commands.OrthogonalVector;
	}

	/*
	 * @Override final public String toString(StringTemplate tpl) { if
	 * (direction instanceof GeoCoordSys2D){ return
	 * loc.getPlain("VectorPerpendicularToAParallelToB", ((GeoElement)
	 * line).getLabel(tpl), ((GeoElement) direction).getLabel(tpl)); }
	 * 
	 * return loc.getPlain("VectorPerpendicularToAB", ((GeoElement)
	 * line).getLabel(tpl), ((GeoElement) direction).getLabel(tpl));
	 * 
	 * }
	 */

}
