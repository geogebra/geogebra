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

package org.geogebra.common.kernel.cas;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.util.MyMath;

/**
 * Prime factors of a number. Adapted from AlgoPrimeFactors
 * 
 * @author Zbynek Konecny
 */

public class AlgoPrimeFactorization extends AlgoElement {

	private GeoNumberValue num; // input
	private GeoList outputList; // output

	/**
	 * Creates new factorization algo
	 * 
	 * @param cons
	 *            construction
	 * @param num
	 *            Number to factorize
	 */
	public AlgoPrimeFactorization(Construction cons, GeoNumberValue num) {
		super(cons);
		this.num = num;

		outputList = new GeoList(cons);

		setInputOutput();
		compute();
	}

	/**
	 * Creates new factorization algo
	 * 
	 * @param cons
	 *            construction
	 * @param label
	 *            label for output
	 * @param num
	 *            Number to factorize
	 */
	public AlgoPrimeFactorization(Construction cons, String label,
			GeoNumberValue num) {
		this(cons, num);
		outputList.setLabel(label);
	}

	@Override
	public Commands getClassName() {
		return Commands.Factors;
	}

	@Override
	protected void setInputOutput() {
		input = new GeoElement[1];
		input[0] = num.toGeoElement();

		setOnlyOutput(outputList);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * Returns the list of points (prime,exponent)
	 * 
	 * @return the list of points (prime,exponent)
	 */
	public GeoList getResult() {
		return outputList;
	}

	@Override
	public final void compute() {

		double n = Math.round(num.getDouble());

		if (n == 1) {
			outputList.clear();
			outputList.setDefined(true);
			return;
		}

		if (n < 2 || n > MyMath.LARGEST_INTEGER) {
			outputList.setUndefined();
			return;
		}

		outputList.setDefined(true);
		outputList.clear();

		int count = 0;

		for (int i = 2; i <= n / i; i++) {
			int exp = 0;
			while (n % i == 0) {
				exp++;
				n /= i;
			}
			if (exp > 0) {
				setListElement(count++, i, exp);
			}
		}
		if (n > 1) {
			setListElement(count++, n, 1);
		}
	}

	// copied from AlgoIterationList.java
	// TODO should it be centralised?
	private void setListElement(int index, double value, double exp) {
		GeoList listElement;
		if (index < outputList.getCacheSize()) {
			// use existing list element
			listElement = (GeoList) outputList.getCached(index);
			listElement.clear();
		} else {
			// create a new list element
			listElement = new GeoList(cons);
			listElement.setParentAlgorithm(this);
			listElement.setConstructionDefaults();
			listElement.setUseVisualDefaults(false);
		}

		outputList.add(listElement);
		GeoNumeric prime = new GeoNumeric(cons);
		prime.setValue(value);
		GeoNumeric exponent = new GeoNumeric(cons);
		exponent.setValue(exp);
		listElement.add(prime);
		listElement.add(exponent);
	}

}
