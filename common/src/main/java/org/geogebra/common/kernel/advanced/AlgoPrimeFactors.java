/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel.advanced;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumberValue;

/**
 * Prime factors of a number. Adapted from AlgoMode
 * 
 * @author Michael Borcherds
 */

public class AlgoPrimeFactors extends AlgoElement {

	private GeoNumberValue num; // input
	private GeoList outputList; // output

	private static double LARGEST_INTEGER = 9007199254740992d;

	public AlgoPrimeFactors(Construction cons, String label,
			GeoNumberValue num) {
		super(cons);
		this.num = num;

		outputList = new GeoList(cons);

		setInputOutput();
		compute();
		outputList.setLabel(label);
	}

	@Override
	public Commands getClassName() {
		return Commands.PrimeFactors;
	}

	@Override
	protected void setInputOutput() {
		input = new GeoElement[1];
		input[0] = num.toGeoElement();

		super.setOutputLength(1);
		super.setOutput(0, outputList);
		setDependencies(); // done by AlgoElement
	}

	public GeoList getResult() {
		return outputList;
	}

	@Override
	public final void compute() {

		double n = Math.round(num.getDouble());

		if (n == 1) {
			outputList.clear();
			outputList.setDefined(true);
			return;
		}

		if (n < 2 || n > LARGEST_INTEGER) {
			outputList.setUndefined();
			return;
		}

		outputList.setDefined(true);
		outputList.clear();

		for (int i = 2; i <= n / i; i++) {
			while (n % i == 0) {
				outputList.addNumber(i, this);
				n /= i;
			}
		}
		if (n > 1) {
			outputList.addNumber(n, this);
		}
	}

	

}
