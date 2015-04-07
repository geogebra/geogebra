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
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.commands.Commands;

/**
 * Computes LCM[a, b]
 * 
 * @author Michael Borcherds
 * @version
 */
public class AlgoLCM extends AlgoTwoNumFunction {

	public AlgoLCM(Construction cons, String label, NumberValue a, NumberValue b) {
		super(cons, label, a, b);
	}

	@Override
	public Commands getClassName() {
		return Commands.LCM;
	}

	@Override
	public final void compute() {
		if (input[0].isDefined() && input[1].isDefined()) {

			if (a.getDouble() > Long.MAX_VALUE
					|| b.getDouble() > Long.MAX_VALUE
					|| a.getDouble() < -Long.MAX_VALUE
					|| b.getDouble() < -Long.MAX_VALUE) {
				num.setUndefined();
				return;
			}
			// this is the only case whwn gcd == zero
			if (Kernel.isZero(a.getDouble()) && Kernel.isZero(b.getDouble())) {
				num.setValue(0);
				return;
			}
			if (a.getDouble() == Math.floor(a.getDouble())
					&& b.getDouble() == Math.floor(b.getDouble())) {
				BigInteger i1 = BigInteger.valueOf((long) a.getDouble());
				BigInteger i2 = BigInteger.valueOf((long) b.getDouble());

				BigInteger gcd = i1.gcd(i2);

				i1 = i1.divide(gcd);

				double result = Math.abs(i1.multiply(i2).doubleValue());

				// can't store integers greater than this in a double accurately
				if (result > 1e15) {
					num.setUndefined();
					return;
				}

				num.setValue(result);
			} else {
				num.setUndefined();
			}
		} else
			num.setUndefined();
	}

}
