/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel.statistics;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoFunctionable;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoPoint;

/**
 * Command: SumSquaredErrors[<list>,<function>] Calculates
 * Sum[(y(<list>)-f(x(<list>))^2] for a function f(x) fitted to the list.
 * 
 * @author Hans-Petter Ulven
 * @version 2010-02-21
 */

public class AlgoSumSquaredErrors extends AlgoElement {

	private GeoList inputList; // input
	private GeoFunctionable function; // input
	private GeoNumeric sse; // output
	private int size;

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            label
	 * @param inputList
	 *            list of points
	 * @param function
	 *            function (model)
	 */
	public AlgoSumSquaredErrors(Construction cons, String label,
			GeoList inputList, GeoFunctionable function) {
		this(cons, inputList, function);
		sse.setLabel(label);
	}

	/**
	 * @param cons
	 *            construction
	 * 
	 * @param inputList
	 *            list of points
	 * @param function
	 *            function (model)
	 */
	public AlgoSumSquaredErrors(Construction cons, GeoList inputList,
			GeoFunctionable function) {
		super(cons);
		this.inputList = inputList;
		this.function = function;

		sse = new GeoNumeric(cons);

		setInputOutput();
		compute();
	}

	@Override
	public Commands getClassName() {
		return Commands.SumSquaredErrors;
	}

	@Override
	protected void setInputOutput() {
		input = new GeoElement[2];
		input[0] = inputList;
		input[1] = function.toGeoElement();

		setOnlyOutput(sse);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * @return resulting sum of errors
	 */
	public GeoNumeric getsse() {
		return sse;
	}

	@Override
	public final void compute() {

		size = inputList.size();
		if (!inputList.isDefined() || !function.toGeoElement().isDefined()) {
			sse.setUndefined();
			return;
		}

		GeoFunction funGeo = function.getGeoFunction();

		// Calculate sse:
		double errorsum = 0.0d;
		GeoElement geo = null;
		GeoPoint point = null;
		double x, y, v;
		for (int i = 0; i < size; i++) {
			geo = inputList.get(i);
			if (geo instanceof GeoPoint) {
				point = (GeoPoint) geo;
				x = point.getX();
				y = point.getY();
				v = funGeo.evaluate(x);
				errorsum += (v - y) * (v - y);
			} else {
				sse.setUndefined();
				return;
			}// if calculation is possible
		}// for all points

		sse.setValue(errorsum);

	}// compute()

	
}// class AlgoSumSquaredErrors

