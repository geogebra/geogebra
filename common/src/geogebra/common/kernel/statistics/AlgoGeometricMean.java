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
import geogebra.common.kernel.locusequ.EquationElement;
import geogebra.common.kernel.locusequ.EquationScope;

/**
 * Returns the geometric mean for a list of numbers
 */

public class AlgoGeometricMean extends AlgoElement {

	private GeoList inputList; // input
	private GeoNumeric result; // output
	private int size;

	public AlgoGeometricMean(Construction cons, String label, GeoList inputList) {
		super(cons);
		this.inputList = inputList;
		result = new GeoNumeric(cons);

		setInputOutput();
		compute();
		result.setLabel(label);
	}

	@Override
	public Algos getClassName() {
		return Algos.AlgoGeometricMean;
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
		// We don't use Apache's GeometricMean anymore here -- the
		// implementation in Apache
		// is the same as below, only uses FastMath instead of Math

		double resultLog = 0;
		// load input value array from geoList
		for (int i = 0; i < size; i++) {
			GeoElement geo = inputList.get(i);
			if (!geo.isNumberValue() || Double.isNaN(resultLog)) {
				result.setUndefined();
				return;
			}
			double val = ((NumberValue) geo).getDouble();
			resultLog += Math.log(val);
		}
		result.setValue(Math.exp(resultLog / size));
	}

	// TODO Consider locusequability

}
