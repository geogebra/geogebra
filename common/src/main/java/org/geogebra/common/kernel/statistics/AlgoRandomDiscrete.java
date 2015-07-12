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
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;

/**
 * Random value from a list using the given probabilities
 * 
 * @author Rrubaa
 */

public class AlgoRandomDiscrete extends AlgoElement {

	private GeoList values; // input
	private GeoList probabilities;
	private GeoNumeric randomDiscrete; // output
	private int size;

	public AlgoRandomDiscrete(Construction cons, String label, GeoList values,
			GeoList probabilities) {
		super(cons);
		this.values = values;
		this.probabilities = probabilities;
		randomDiscrete = new GeoNumeric(cons);

		setInputOutput();
		compute();

		randomDiscrete.setLabel(label);
	}

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

	public GeoNumeric getResult() {
		return randomDiscrete;
	}

	public final void compute() {
		size = values.size();

		if (!values.isDefined() || !probabilities.isDefined() || size == 0 || size != probabilities.size()) {
			randomDiscrete.setUndefined();
			return;
		}

		double sum =0;
		
		for(int i =0; i<size; i++){
			if(!(values.get(i) instanceof NumberValue) && !(probabilities.get(i) instanceof NumberValue)){
				randomDiscrete.setUndefined();
				return;
			}

			double val = ((NumberValue) values.get(i)).getDouble();
			double prob = ((NumberValue) probabilities.get(i)).getDouble();

			if (Double.isInfinite(val) || Double.isNaN(val)
					|| Double.isInfinite(prob) || Double.isNaN(prob)) {
				randomDiscrete.setUndefined();
				return;
			}

			sum += prob;
		}
		
		double randomDouble = sum * cons.getApplication().getRandomNumber();
		
		double total = ((NumberValue)probabilities.get(0)).getDouble();
		int count =0;
		
		while(total < randomDouble){
			count++;
			total += ((NumberValue)probabilities.get(count)).getDouble();
		}
		
		double result = ((NumberValue) values.get(count)).getDouble();
		randomDiscrete.setValue(result);
	}

}

