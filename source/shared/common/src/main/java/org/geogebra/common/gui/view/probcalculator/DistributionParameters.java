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

package org.geogebra.common.gui.view.probcalculator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import org.geogebra.common.kernel.geos.GeoNumberValue;

/**
 * Stores distribution parameters
 */
public class DistributionParameters {
	final boolean isCumulative;
	private final ArrayList<GeoNumberValue> parameterList;

	/**
	 *
	 * @param parameters as GeoNumberValue array.
	 * @param isCumulative if the distribution is cumulative.
	 */
	public DistributionParameters(GeoNumberValue[] parameters, boolean isCumulative) {
		parameterList = new ArrayList<>(Arrays.asList(parameters));
		this.isCumulative = isCumulative;
	}

	/**
	 *
	 * @param idx index of the parameter.
	 * @return the parameter at given index.
	 */
	public GeoNumberValue at(int idx) {
		return parameterList.size() > idx ? parameterList.get(idx) : null;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}

		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		DistributionParameters params = (DistributionParameters) o;
		boolean result = isCumulative == params.isCumulative;
		for (int i = 0; i < parameterList.size(); i++) {
			result = result && params.at(i) == at(i);
		}
		return result;
	}

	@Override
	public int hashCode() {
		return Objects.hash(Arrays.hashCode(parameterList.toArray()), isCumulative);
	}
}
