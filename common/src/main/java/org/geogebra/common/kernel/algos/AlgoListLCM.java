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
 * LCM of a list. adapted from AlgoListMax
 * 
 * @author Michael Borcherds
 * @version 01-08-2011
 */

public class AlgoListLCM extends AlgoElement {

	private static final BigInteger bigZero = BigInteger.valueOf(0);
	private GeoList geoList; // input
	private GeoNumeric num; // output

	public AlgoListLCM(Construction cons, String label, GeoList geoList) {
		super(cons);
		this.geoList = geoList;

		num = new GeoNumeric(cons);

		setInputOutput();
		compute();
		num.setLabel(label);
	}

	@Override
	public Commands getClassName() {
		return Commands.LCM;
	}

	@Override
	protected void setInputOutput() {
		input = new GeoElement[1];
		input[0] = geoList;

		setOutputLength(1);
		setOutput(0, num);
		setDependencies(); // done by AlgoElement
	}

	public GeoNumeric getLCM() {
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

		double nd = ((GeoNumeric) (geoList.get(0))).getDouble();
		if (!Kernel.isInteger(nd)) {
			num.setUndefined();
			return;
		}

		BigInteger lcm = BigInteger.valueOf((long) Kernel.checkInteger(nd));

		for (int i = 1; i < geoList.size(); i++) {
			nd = ((GeoNumeric) (geoList.get(i))).getDouble();

			if (!Kernel.isInteger(nd)) {
				num.setUndefined();
				return;
			}
			BigInteger n = BigInteger.valueOf((long) nd);
			if (n.compareTo(bigZero) == 0) {
				lcm = bigZero;
			} else {
				BigInteger product = n.multiply(lcm);
				lcm = product.divide(lcm.gcd(n));
			}
		}

		double resultD = Math.abs(lcm.doubleValue());

		// can't store integers greater than this in a double accurately
		if (Math.abs(lcm.doubleValue()) > 1e15) {
			num.setUndefined();
			return;
		}
		num.setValue(resultD);

	}

	

}
