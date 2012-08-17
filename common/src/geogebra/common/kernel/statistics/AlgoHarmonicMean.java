/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra.common.kernel.statistics;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.algos.Algos;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoNumeric;

/**
 * Returns the harmonic mean for a list of numbers
 */

public class AlgoHarmonicMean extends AlgoElement {

	private GeoList inputList; // input
	private GeoNumeric result; // output
	private int size;
	private double sum;

	public AlgoHarmonicMean(Construction cons, String label, GeoList inputList) {
		super(cons);
		this.inputList = inputList;
		result = new GeoNumeric(cons);

		setInputOutput();
		compute();
		result.setLabel(label);
	}

	@Override
	public Algos getClassName() {
		return Algos.AlgoHarmonicMean;
	}

	@Override
	protected void setInputOutput() {
		input = new GeoElement[1];
		input[0] = inputList;

		setOutputLength(1);
		setOutput(0, result);
		setDependencies(); // done by AlgoElement
	}

	public GeoNumeric getResult() {
		return result;
	}

	@Override
	public final void compute() {

		// ==========================
		// validation
		size = inputList.size();
		if (!inputList.isDefined() || size == 0) {
			result.setUndefined();
			return;
		}

		// ==========================
		// compute result

		sum = 0;

		// load input value array from geoList
		for (int i = 0; i < size; i++) {
			GeoElement geo = inputList.get(i);
			if (geo.isNumberValue()) {
				NumberValue num = (NumberValue) geo;
				sum += 1 / num.getDouble();
			} else {
				result.setUndefined();
				return;
			}
		}

		result.setValue(size / sum);
	}

	// TODO Consider locusequability

}
