/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

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
import org.geogebra.common.kernel.geos.GeoFunctionable;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoPoint;

/**
 * Creates a residual plot.
 * 
 * Input: list of points (x,y) Input: regression function Output: list of
 * residual points (x, y - yPredicted)
 * 
 * @author G.Sturr
 */
public class AlgoResidualPlot extends AlgoElement {

	private GeoList inputList; // input
	private GeoFunctionable function;
	private GeoList outputList; // output
	private int size;
	private double min;
	private double max;

	/**
	 * @param cons
	 *            construction
	 * @param inputList
	 *            list of points
	 * @param function2
	 *            function
	 */
	public AlgoResidualPlot(Construction cons, GeoList inputList,
			GeoFunctionable function2) {
		super(cons);
		this.inputList = inputList;
		this.function = function2;
		outputList = new GeoList(cons);

		setInputOutput();
		compute();
	}

	@Override
	public Commands getClassName() {
		return Commands.ResidualPlot;
	}

	@Override
	protected void setInputOutput() {
		input = new GeoElement[2];
		input[0] = inputList;
		input[1] = function.toGeoElement();

		setOnlyOutput(outputList);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * @return list of residues
	 */
	public GeoList getResult() {
		return outputList;
	}

	/**
	 * @return min and max residues
	 */
	public double[] getResidualBounds() {
		return new double[] { min, max };
	}

	@Override
	public final void compute() {

		size = inputList.size();
		if (!inputList.isDefined() || size == 0) {
			outputList.setUndefined();
			return;
		}

		outputList.setDefined(true);
		outputList.clear();

		double x, y, r;

		min = Double.MAX_VALUE;
		max = -Double.MAX_VALUE;

		for (int i = 0; i < size; i++) {
			GeoElement p = inputList.get(i);
			if (p instanceof GeoPoint) {
				x = ((GeoPoint) p).getInhomX();
				y = ((GeoPoint) p).getInhomY();
				r = y - function.value(x);
				min = Math.min(r, min);
				max = Math.max(r, max);
				outputList.addPoint(x, r, 1.0, null);
			} else {
				outputList.setUndefined();
				return;
			}
		}
	}

}
