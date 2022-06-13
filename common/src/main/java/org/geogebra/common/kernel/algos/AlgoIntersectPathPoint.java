/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel.algos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.ConstructionDefaults;
import org.geogebra.common.kernel.FixedPathRegionAlgo;
import org.geogebra.common.kernel.Path;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.Function;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.matrix.Coords;

/**
 * @author csilla
 *
 *         Algo for intersection of path with point
 */
public class AlgoIntersectPathPoint extends AlgoElement
		implements FixedPathRegionAlgo {

	private Path path; // input
	private GeoPointND point; // input
	private GeoPointND P; // output

	/**
	 * @param cons
	 *            construction
	 * @param path
	 *            path
	 * @param point
	 *            point
	 */
	public AlgoIntersectPathPoint(Construction cons, Path path,
			GeoPointND point) {
		super(cons);
		this.path = path;
		this.point = point;

		// create point on path and compute current location
		createOutputPoint(cons, path);

		// for AlgoElement
		setInputOutput();
		P.setVisualStyle(cons.getConstructionDefaults()
				.getDefaultGeo(ConstructionDefaults.DEFAULT_POINT_DEPENDENT));
		compute();
	}

	/**
	 * create the output point
	 * 
	 * @param cons1
	 *            construction
	 * @param path1
	 *            path
	 */
	protected void createOutputPoint(Construction cons1, Path path1) {
		P = point.copy();
		P.setPath(path1);
	}

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            label
	 * @param path
	 *            path
	 * @param point
	 *            point
	 */
	public AlgoIntersectPathPoint(Construction cons, String label, Path path,
			GeoPointND point) {
		this(cons, path, point);

		P.setLabel(label);
	}

	@Override
	public Commands getClassName() {
		return Commands.Intersect;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[2];
		input[0] = path.toGeoElement();
		input[1] = point.toGeoElement();
		setOutputLength(1);
		setOutput(0, (GeoElement) P);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * 
	 * @return resulting point (same coords as input, may be undefined)
	 */
	public GeoPointND getP() {
		return P;
	}

	/**
	 * set coords of closest point to input point coords
	 */
	protected void setCoords() {
		P.set(point);
	}

	@Override
	public final void compute() {
		// if path and point are defined
		if (input[0].isDefined() && point.isDefined()) {
			// get the closest point on path to input point
			if (path instanceof GeoFunction) {
				Function fun = ((GeoFunction) path).getFunction()
						.deepCopy(kernel);
				Coords coords = point.getCoordsInD2();
				double val = AlgoDistancePointObject
						.getClosestFunctionValueToPoint(fun, coords.getX(),
								coords.getY());
				P.setCoords(val, fun.value(val), 1.0);
			} else {
				setCoords();
				path.pointChanged(P);
			}

			P.updateCoords();
			// if input point and output point has same coordinates
			// return point
			// else set output point undefined
			if (!P.getCoords().equalsForKernel(point.getCoords())) {
				P.setUndefined();
			}
		} else {
			P.setUndefined();
		}
	}

	@Override
	final public String toString(StringTemplate tpl) {
		return getLoc().getPlainDefault("IntersectionOfAandB",
				"Intersection of %0 and %1", input[0].getLabel(tpl),
				input[1].getLabel(tpl));
	}

	@Override
	public boolean isChangeable(GeoElementND out) {
		return false;
	}

}
