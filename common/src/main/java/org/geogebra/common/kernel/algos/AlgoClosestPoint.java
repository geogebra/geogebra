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
import org.geogebra.common.kernel.Path;
import org.geogebra.common.kernel.FixedPathRegionAlgo;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.arithmetic.Function;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.kernelND.GeoPointND;

public class AlgoClosestPoint extends AlgoElement implements FixedPathRegionAlgo {

	private Path path; // input
	protected GeoPointND point; // input
	protected GeoPointND P; // output

	public AlgoClosestPoint(Construction cons, Path path, GeoPointND point) {
		super(cons);
		this.path = path;
		this.point = point;

		// create point on path and compute current location
		createOutputPoint(cons, path);

		setInputOutput(); // for AlgoElement
		compute();
		addIncidence();
	}

	/**
	 * create the output point
	 * 
	 * @param cons
	 *            construction
	 * @param path
	 *            path
	 */
	protected void createOutputPoint(Construction cons, Path path) {
		P = new GeoPoint(cons);
		((GeoPoint) P).setPath(path);
	}

	public AlgoClosestPoint(Construction cons, String label, Path path,
			GeoPointND point) {
		this(cons, path, point);

		P.setLabel(label);
	}

	@Override
	public Commands getClassName() {
		return Commands.ClosestPoint;
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
	 * @author Tam
	 * 
	 *         for special cases of e.g. AlgoIntersectLineConic
	 */
	protected void addIncidence() {
		((GeoPoint) P).addIncidence((GeoElement) path, false);

	}

	public GeoPointND getP() {
		return P;
	}

	/**
	 * set coords of closest point to input point coords
	 */
	protected void setCoords() {
		((GeoPoint) P).setCoords((GeoPoint) point);
	}

	@Override
	public final void compute() {
		if (input[0].isDefined() && point.isDefined()) {
			if (path instanceof GeoFunction) {
				Function fun = (Function) ((GeoFunction) path).getFunction()
						.deepCopy(kernel);
				Coords coords = point.getCoordsInD2();
				double val = AlgoDistancePointObject
						.getClosestFunctionValueToPoint(fun, coords.getX(),
								coords.getY());
				((GeoPoint) P).setCoords(val, fun.evaluate(val), 1.0);
			} else {
				setCoords();
				path.pointChanged(P);
			}

			P.updateCoords();
		} else {
			P.setUndefined();
		}
	}

	@Override
	final public String toString(StringTemplate tpl) {
		return getLoc().getPlain("PointOnAClosestToB", input[0].getLabel(tpl),
				input[1].getLabel(tpl));
	}

	public boolean isChangeable() {
		return false;
	}

	// TODO Consider locusequability

}
