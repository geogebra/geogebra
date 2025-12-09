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

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPlane3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoVector3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoCoordSys2D;
import org.geogebra.common.kernel.matrix.Coords;

/**
 *
 * @author ggb3D
 * 
 *         Calculate the ortho vector of a plane (or polygon, ...)
 * 
 */
public class AlgoOrthoVectorPlane extends AlgoElement3D {

	/** plane (input) */
	protected GeoCoordSys2D plane;

	/** ortho vector (output) */
	private GeoVector3D vector;

	/**
	 * output coords
	 */
	protected Coords vCoords;

	/**
	 * Creates new AlgoIntersectLinePlane
	 * 
	 * @param cons
	 *            the construction
	 * @param label
	 *            name of point
	 * @param plane
	 *            orthogonal plane
	 */
	AlgoOrthoVectorPlane(Construction cons, String label, GeoCoordSys2D plane) {
		super(cons);
		vCoords = new Coords(4);
		this.plane = plane;
		vector = new GeoVector3D(cons);
		setInputOutput(new GeoElement[] { (GeoElement) plane },
				new GeoElement[] { vector });

		compute();
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
		if (!plane.isDefined()) {
			vector.setUndefined();
			return;
		}

		updateCoords();
		vector.setCoords(vCoords);
	}

	/**
	 * Compute coords of the vector
	 */
	protected void updateCoords() {
		if (plane instanceof GeoPlane3D) {
			// get (a, b, c) from ax+by+cz+d=0
			vCoords.setValues(plane.getCoordSys().getEquationVector(), 3);
		} else {
			vCoords = plane.getCoordSys().getVz();
		}
	}

	@Override
	public Commands getClassName() {

		return Commands.OrthogonalVector;
	}

	@Override
	final public String toString(StringTemplate tpl) {
		return getLoc().getPlain("VectorPerpendicularToA",
				plane.getLabel(tpl));

	}

}
