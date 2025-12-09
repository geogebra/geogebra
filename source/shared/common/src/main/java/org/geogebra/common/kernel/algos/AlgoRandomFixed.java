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
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoNumeric;

/**
 * Random number on file load, but fixed after that
 * 
 * @author Michael
 */
public class AlgoRandomFixed extends AlgoElement {

	protected GeoNumberValue a; // input
	protected GeoNumberValue b; // input
	protected GeoNumeric num; // output

	double random;
	double aLast = Double.NaN;
	double bLast = Double.NaN;

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param a
	 *            min
	 * @param b
	 *            max
	 */
	public AlgoRandomFixed(Construction cons, String label, GeoNumberValue a,
			GeoNumberValue b) {
		this(cons, a, b);
		num.setLabel(label);
	}

	protected AlgoRandomFixed(Construction cons, GeoNumberValue a,
			GeoNumberValue b) {
		super(cons);
		this.a = a;
		this.b = b;
		num = new GeoNumeric(cons);
		setInputOutput();

		compute();
	}

	@Override
	public Commands getClassName() {
		return Commands.Random;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[3];
		input[0] = a.toGeoElement();
		input[1] = b.toGeoElement();
		input[2] = new GeoBoolean(cons, true); // dummy

		setOnlyOutput(num);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * @return random number
	 */
	public GeoNumeric getResult() {
		return num;
	}

	@Override
	public void compute() {
		if (input[0].isDefined() && input[1].isDefined()) {
			if (a.getDouble() != aLast || b.getDouble() != bLast) {
				// change random number only if a or b has changed
				aLast = a.getDouble();
				bLast = b.getDouble();
				random = cons.getApplication()
						.getRandomIntegerBetween(a.getDouble(), b.getDouble());
				num.setValue(random);

			} else {
				// keep same value as before
				num.setValue(random);
			}
		} else {
			num.setUndefined();
		}
	}

}
