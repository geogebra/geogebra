/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel.algos;

import java.math.BigInteger;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;

/**
 * GCD of a list. adapted from AlgoListMax
 * 
 * @author Michael Borcherds
 * @version 03-01-2008
 */

public class AlgoListGCD extends AlgoElement {

	private GeoList geoList; // input
	private GeoNumeric num; // output

	public AlgoListGCD(Construction cons, String label, GeoList geoList) {
		super(cons);
		this.geoList = geoList;

		num = new GeoNumeric(cons);

		setInputOutput();
		compute();
		num.setLabel(label);
	}

	@Override
	public Commands getClassName() {
		return Commands.GCD;
	}

	@Override
	protected void setInputOutput() {
		input = new GeoElement[1];
		input[0] = geoList;

		super.setOutputLength(1);
		super.setOutput(0, num);
		setDependencies(); // done by AlgoElement
	}

	public GeoNumeric getGCD() {
		return num;
	}

	@Override
	public final void compute() {
		int size = geoList.size();
		if (!geoList.isDefined() || size == 0) {
			num.setUndefined();
			return;
		}

		if (!geoList.getGeoElementForPropertiesDialog().isGeoNumeric()) {
			num.setUndefined();
			return;
		}

		double value = ((GeoNumeric) (geoList.get(0))).getDouble();
		// check if first value is quite integer
		if (!Kernel.isInteger(value)) {
			num.setUndefined();
			return;
		}

		BigInteger gcd = BigInteger.valueOf((long) Kernel.checkInteger(value));

		for (int i = 1; i < geoList.size(); i++) {
			value = ((GeoNumeric) (geoList.get(i))).getDouble();
			// check if value is quite integer
			if (!Kernel.isInteger(value)) {
				num.setUndefined();
				return;
			}
			BigInteger n = BigInteger
					.valueOf((long) Kernel.checkInteger(value));
			gcd = gcd.gcd(n);
		}

		double result = Math.abs(gcd.doubleValue());

		// can't store integers greater than this in a double accurately
		if (result > 1e15) {
			num.setUndefined();
			return;
		}

		num.setValue(result);
	}

	

}
