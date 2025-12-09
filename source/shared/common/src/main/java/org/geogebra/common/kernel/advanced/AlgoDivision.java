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
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoNumeric;

public class AlgoDivision extends AlgoElement {

	private GeoNumberValue a;
	private GeoNumberValue b;
	private GeoNumeric num;
	private GeoNumeric mod;
	private GeoList result;

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param dividend
	 *            dividend
	 * @param divisor
	 *            divisor
	 */
	public AlgoDivision(Construction cons, String label,
			GeoNumberValue dividend, GeoNumberValue divisor) {
		super(cons);
		a = dividend;
		b = divisor;
		num = new GeoNumeric(cons);
		mod = new GeoNumeric(cons);
		result = new GeoList(cons);
		result.add(num);
		result.add(mod);
		setInputOutput();
		compute();
		result.setLabel(label);

	}

	@Override
	protected void setInputOutput() {
		setOnlyOutput(result);
		input = new GeoElement[] { a.toGeoElement(), b.toGeoElement() };
		setDependencies();
	}

	@Override
	public void compute() {
		if (input[0].isDefined() && input[1].isDefined()) {

			double numerator = a.getDouble();
			double denominator = b.getDouble();

			if (Math.abs(numerator) > MyDouble.LARGEST_INTEGER
					|| Math.abs(denominator) > MyDouble.LARGEST_INTEGER) {
				result.setUndefined();
				return;
			}
			result.setDefined(true);
			double m = numerator % Math.abs(denominator);
			if (m < 0) {
				m += Math.abs(denominator);
			}
			mod.setValue(m);

			num.setValue(Math.round((numerator - m) / denominator));
		} else {
			result.setUndefined();
		}

	}

	@Override
	public Commands getClassName() {
		return Commands.Division;
	}

	public GeoElement getResult() {
		return result;
	}

}
