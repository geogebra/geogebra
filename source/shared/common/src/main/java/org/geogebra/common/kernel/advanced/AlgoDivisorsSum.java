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

package org.geogebra.common.kernel.advanced;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.cas.AlgoPrimeFactorization;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.util.DoubleUtil;
import org.geogebra.common.util.debug.Log;

public class AlgoDivisorsSum extends AlgoElement {

	GeoNumeric result;
	private GeoNumberValue number;
	private AlgoPrimeFactorization factors;
	private GeoList factorList;
	private boolean sum;

	/**
	 * @param c
	 *            construction
	 * @param label
	 *            output label
	 * @param number
	 *            number
	 * @param sum
	 *            true to compute divisor sum, otherwise count divisors
	 */
	public AlgoDivisorsSum(Construction c, String label, GeoNumberValue number,
			boolean sum) {
		super(c);
		this.number = number;
		this.sum = sum;
		factors = new AlgoPrimeFactorization(c, number);
		factorList = factors.getResult();
		result = new GeoNumeric(cons);
		setInputOutput();
		compute();
		result.setLabel(label);
	}

	@Override
	protected void setInputOutput() {
		setOnlyOutput(result);
		input = new GeoElement[] { number.toGeoElement() };
		setDependencies();
	}

	@Override
	public void compute() {
		if (!factorList.isDefined() || !DoubleUtil.isInteger(number.getDouble())) {
			result.setUndefined();
			return;
		}
		long res = 1;
		for (int i = 0; i < factorList.size(); i++) {
			GeoList pair = (GeoList) factorList.get(i);
			double exp = pair.get(1).evaluateDouble();
			if (sum) {
				double prime = pair.get(0).evaluateDouble();
				Log.debug(prime);
				res = res * Math
						.round((Math.pow(prime, exp + 1) - 1) / (prime - 1.0));
			} else {
				res = res * Math.round(exp + 1);
			}
		}
		result.setValue(res);
	}

	public GeoNumeric getResult() {
		return result;
	}

	@Override
	public Commands getClassName() {
		if (sum) {
			return Commands.DivisorsSum;
		}
		return Commands.Divisors;
	}

}
