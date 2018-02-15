package org.geogebra.common.kernel.statistics;

import org.apache.commons.math3.stat.Frequency;
import org.geogebra.common.util.DoubleUtil;

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
			Double v2 = DoubleUtil.checkDecimalFraction((Double) v);
			super.addValue(v2);
			return;
		}
		super.addValue(v);
	}
}
