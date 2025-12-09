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

package org.geogebra.common.kernel.statistics;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunctionable;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoPoint;

/**
 * Command: SumSquaredErrors[&lt;List&gt;,&lt;Function&gt;] Calculates
 * Sum[(y(&lt;List&gt;)-f(x( &lt;List&gt;))^2] for a function f(x) fitted to the list.
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
				v = function.value(x);
				errorsum += (v - y) * (v - y);
			} else {
				sse.setUndefined();
				return;
			} // if calculation is possible
		} // for all points

		sse.setValue(errorsum);
	}

}
