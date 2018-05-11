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
