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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.cas.AlgoPrimeFactorization;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.util.DoubleUtil;

public class AlgoDivisorsList extends AlgoElement {

	GeoList result;
	private GeoNumberValue number;
	private AlgoPrimeFactorization factors;
	private GeoList factorList;
	List<Long> factList = new ArrayList<>();

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param number
	 *            number
	 */
	public AlgoDivisorsList(Construction cons, String label,
			GeoNumberValue number) {
		super(cons);
		this.number = number;
		factors = new AlgoPrimeFactorization(cons, number);
		factorList = factors.getResult();
		result = new GeoList(cons);
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

		int oldLength = 1;
		factList.clear();
		factList.add(1L);
		for (int i = 0; i < factorList.size(); i++) {
			GeoList pair = (GeoList) factorList.get(i);
			double exp = pair.get(1).evaluateDouble();
			double prime = pair.get(0).evaluateDouble();
			long power = Math.round(prime);
			for (int k = 1; k <= exp; k++) {
				for (int j = 0; j < oldLength; j++) {
					factList.add(factList.get(j) * power);
				}
				power *= Math.round(prime);
			}
			oldLength = factList.size();

		}
		result.setDefined(true);
		result.clear();
		Set<Long> sortedSet = new TreeSet<>();
		sortedSet.addAll(factList);
		Iterator<Long> iterator = sortedSet.iterator();

		while (iterator.hasNext()) {
			result.addNumber(iterator.next(), this);
		}

	}

	@Override
	public Commands getClassName() {
		return Commands.DivisorsList;
	}

	public GeoList getResult() {
		return result;
	}

}
