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
