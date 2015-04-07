package org.geogebra.common.kernel.statistics;

import org.apache.commons.math.stat.Frequency;
import org.geogebra.common.kernel.Kernel;

/**
 * Extension of the Apache Commons Frequency class. Modifies the addValue()
 * method to handle comparisons with double values that may contain rounding
 * errors but should be treated as equal.
 * 
 * @author G. Sturr
 * 
 */
public class FrequencyGgb extends Frequency {

	private static final long serialVersionUID = 1L;

	@Override
	public void addValue(Comparable<?> v) {
		if (v instanceof Double) {
			Double v2 = Kernel.checkDecimalFraction((Double) v);
			super.addValue(v2);
			return;
		}
		super.addValue(v);
		return;
	}
}
