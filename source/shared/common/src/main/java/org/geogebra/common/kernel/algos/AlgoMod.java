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

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.util.DoubleUtil;

/**
 * Computes Mod[a, b]
 * 
 * @author Markus Hohenwarter
 */
public class AlgoMod extends AlgoTwoNumFunction {

	/**
	 * Creates new mod algo
	 * 
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param a
	 *            dividend
	 * @param b
	 *            divisor
	 */
	public AlgoMod(Construction cons, String label, GeoNumberValue a,
			GeoNumberValue b) {
		super(cons, label, a, b);
	}

	@Override
	public Commands getClassName() {
		return Commands.Mod;
	}

	@Override
	public final double computeValue(double aValRaw, double bVal) {
		double aVal = DoubleUtil.checkInteger(aValRaw);
		double bAbs = DoubleUtil.checkInteger(Math.abs(bVal));

		if (DoubleUtil.isEqual(0, aVal % bVal)) {
			return 0;
		} else {
			if (Math.abs(aVal) > MyDouble.LARGEST_INTEGER || bAbs > MyDouble.LARGEST_INTEGER) {
				return Double.NaN;
			}

			double mod = aVal % bAbs;

			// https://docs.oracle.com/javase/specs/jls/se7/html/jls-15.html#jls-15.17.3
			// It follows from this rule that the result of the remainder
			// operation can be negative only if the dividend is negative, and
			// can be positive only if the dividend is positive.
			if (mod < 0) {
				mod += bAbs;
			}

			return mod;
		}
	}
}
