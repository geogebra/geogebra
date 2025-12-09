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

package org.geogebra.common.kernel.algos;

import java.math.BigInteger;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.util.DoubleUtil;

/**
 * Computes LCM[a, b]
 * 
 * @author Michael Borcherds
 */
public class AlgoLCM extends AlgoTwoNumFunction {

	public AlgoLCM(Construction cons, String label, GeoNumberValue a,
			GeoNumberValue b) {
		super(cons, label, a, b);
	}

	@Override
	public Commands getClassName() {
		return Commands.LCM;
	}

	@Override
	public final double computeValue(double aVal, double bVal) {
		if (aVal > Long.MAX_VALUE || bVal > Long.MAX_VALUE || aVal < -Long.MAX_VALUE
				|| bVal < -Long.MAX_VALUE) {
			return Double.NaN;
		}

		if (DoubleUtil.isZero(aVal) || DoubleUtil.isZero(bVal)) {
			return 0;
		}

		if (DoubleUtil.isInteger(aVal) && DoubleUtil.isInteger(bVal)) {
			BigInteger i1 = BigInteger.valueOf(Math.round(aVal));
			BigInteger i2 = BigInteger.valueOf(Math.round(bVal));

			BigInteger gcd = i1.gcd(i2);

			i1 = i1.divide(gcd);

			double result = Math.abs(i1.multiply(i2).doubleValue());

			// can't store integers greater than this in a double accurately
			if (result < 1e15) {
				return result;
			}
		}
		return Double.NaN;
	}
}
