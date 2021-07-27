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
import org.geogebra.common.kernel.SetRandomValue;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.util.DoubleUtil;

/**
 * Random value from a list using the given probabilities
 * 
 * @author Rrubaa
 */

public class AlgoRandomDiscrete extends AlgoElement implements SetRandomValue {

	private GeoList values; // input
	private GeoList probabilities;
	private GeoNumeric randomDiscrete; // output
	private int size;

	/**
	 * 
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param values
	 *            discrete values
	 * @param probabilities
	 *            probabilities
	 */
	public AlgoRandomDiscrete(Construction cons, String label, GeoList values,
			GeoList probabilities) {
		super(cons);
		this.values = values;
		this.probabilities = probabilities;
		randomDiscrete = new GeoNumeric(cons);

		setInputOutput();
		compute();

		randomDiscrete.setLabel(label);
		cons.addRandomGeo(randomDiscrete);
	}

	@Override
	public Commands getClassName() {
		return Commands.RandomDiscrete;
	}

	@Override
	protected void setInputOutput() {
		input = new GeoElement[2];
		input[0] = values;
		input[1] = probabilities;

		super.setOnlyOutput(randomDiscrete);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * @return resulting random number
	 */
	public GeoNumeric getResult() {
		return randomDiscrete;
	}

	@Override
	public final void compute() {
		size = values.size();

		if (!values.isDefined() || !probabilities.isDefined() || size == 0
				|| size != probabilities.size()) {
			randomDiscrete.setUndefined();
			return;
		}

		double sum = 0;

		for (int i = 0; i < size; i++) {
			if (!(values.get(i) instanceof NumberValue)
					&& !(probabilities.get(i) instanceof NumberValue)) {
				randomDiscrete.setUndefined();
				return;
			}

			double val = values.get(i).evaluateDouble();
			double prob = probabilities.get(i).evaluateDouble();

			if (Double.isInfinite(val) || Double.isNaN(val)
					|| Double.isInfinite(prob) || Double.isNaN(prob)) {
				randomDiscrete.setUndefined();
				return;
			}

			sum += prob;
		}

		double randomDouble = sum * cons.getApplication().getRandomNumber();

		double total = probabilities.get(0).evaluateDouble();
		int count = 0;

		while (total < randomDouble) {
			count++;
			total += probabilities.get(count).evaluateDouble();
		}

		double result = values.get(count).evaluateDouble();
		randomDiscrete.setValue(result);
	}

	@Override
	public boolean setRandomValue(GeoElementND rnd) {
		double d = rnd.evaluateDouble();
		for (int i = 0; i < values.size(); i++) {
			if (DoubleUtil.isEqual(values.get(i).evaluateDouble(), d)) {
				randomDiscrete.setValue(values.get(i).evaluateDouble());
				return true;
			}
		}
		return false;
	}

}
