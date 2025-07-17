/*
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel.algos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunctionable;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.optimization.ExtremumFinderI;

/**
 * Command: Min[&lt;function&gt;,left-x,right-x]
 * 
 * Numerically calculates Extremum point for &lt;function&gt; in closed interval
 * [left-x,right-x] without being dependent on being able to find the derivate
 * of &lt;function&gt;.
 * 
 * Restrictions for use: &lt;function&gt; should be continuous and only have one
 * extremum in the interval [left-x,right-x]
 * 
 * Breaking restrictions will give unpredictable results: -Will usually find the
 * first minimum if more than one extremums -Unpredictable results if
 * discontinuous in interval
 * 
 * Uses Brent's algorithm in geogebra.kernel.optimization.ExtremumFinder;
 * 
 * @author Hans-Petter Ulven
 * @version 2011-02.20
 */

public class AlgoFunctionMinMax extends AlgoElement {

	private GeoFunctionable f;
	private GeoNumberValue left; // input
	private GeoNumberValue right; // input
	private GeoPoint E; // output
	private ExtremumFinderI extrFinder = null;
	private boolean isMin;

	/**
	 * Constructor for Extremum[f,l,r]
	 * 
	 * @param cons
	 *            construction
	 * @param label
	 *            label
	 * @param function
	 *            function to maximize
	 * @param left
	 *            left boundary
	 * @param right
	 *            right boundary
	 * @param isMin
	 *            true for min, false for max
	 */
	public AlgoFunctionMinMax(Construction cons, String label,
			GeoFunctionable function, GeoNumberValue left, GeoNumberValue right,
			boolean isMin) {
		super(cons);
		this.f = function;
		this.left = left;
		this.right = right;
		this.isMin = isMin;

		E = new GeoPoint(cons); // Put an extremum point in the user interface
								// from the very start
		E.setCoords(0.0, 0.0, 1.0);

		setInputOutput();

		compute();

		E.setLabel(label);
	}

	@Override
	public Commands getClassName() {
		return this.isMin ? Commands.Min : Commands.Max;
	}

	@Override
	protected void setInputOutput() {
		input = new GeoElement[3];
		input[0] = f.toGeoElement();
		input[1] = left.toGeoElement();
		input[2] = right.toGeoElement();

		setOnlyOutput(E);

		setDependencies(); // done by AlgoElement
	}

	/**
	 * @return resulting point
	 */
	public GeoPoint getPoint() {
		return E;
	}

	@Override
	public final void compute() {
		double l = left.getDouble();
		double r = right.getDouble();

		if (!f.toGeoElement().isDefined() || !left.isDefined()
				|| !right.isDefined()
				|| (right.getDouble() <= left.getDouble())) {
			E.setUndefined();
			return;
		} // if input is ok?

		// Brent's algorithm
		extrFinder = kernel.getExtremumFinder();

		double min = isMin ? extrFinder.findMinimum(l, r, f, 5.0E-8)
				: extrFinder.findMaximum(l, r, f, 5.0E-8);

		E.setCoords(min, f.value(min), 1.0);
		E.updateRepaint();

	}

}
