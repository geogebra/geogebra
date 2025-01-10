package org.geogebra.common.kernel.advanced;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.util.MyMath;

public class AlgoIsPrime extends AlgoElement {
	private GeoBoolean result;
	private GeoNumberValue number;

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param number
	 *            number
	 */
	public AlgoIsPrime(Construction cons, String label, GeoNumberValue number) {
		super(cons);
		result = new GeoBoolean(cons);
		this.number = number;
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
		double n = Math.round(number.getDouble());
		result.setDefined();
		if (n == 1) {
			result.setValue(false);
			return;
		}

		if (n < 2 || n > MyMath.LARGEST_INTEGER) {
			result.setUndefinedProverOnly();
			return;
		}
		result.setValue(true);
		for (int i = 2; i <= n / i; i++) {
			if (n % i == 0) {
				result.setValue(false);
				return;
			}
		}
	}

	@Override
	public Commands getClassName() {
		return Commands.IsPrime;
	}

	public GeoBoolean getResult() {
		return result;
	}

}
