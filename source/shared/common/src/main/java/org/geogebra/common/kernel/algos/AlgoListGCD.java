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
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.util.DoubleUtil;

/**
 * GCD of a list. adapted from AlgoListMax
 * 
 * @author Michael Borcherds
 * @version 03-01-2008
 */

public class AlgoListGCD extends AlgoElement {

	private GeoList geoList; // input
	private GeoNumeric num; // output

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param geoList
	 *            list of numbers
	 */
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

		setOnlyOutput(num);
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

		double value = ((GeoNumeric) geoList.get(0)).getDouble();
		// check if first value is quite integer
		if (!DoubleUtil.isInteger(value)) {
			num.setUndefined();
			return;
		}

		BigInteger gcd = BigInteger.valueOf((long) DoubleUtil.checkInteger(value));

		for (int i = 1; i < geoList.size(); i++) {
			value = ((GeoNumeric) geoList.get(i)).getDouble();
			// check if value is quite integer
			if (!DoubleUtil.isInteger(value)) {
				num.setUndefined();
				return;
			}
			BigInteger n = BigInteger
					.valueOf((long) DoubleUtil.checkInteger(value));
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
