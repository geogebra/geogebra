package geogebra.common.kernel.statistics;

import geogebra.common.kernel.Kernel;

import org.apache.commons.math.stat.Frequency;

/**
 * Extension of the Apache Commons Frequency class. Modifies some methods to
 * handle comparisons with double values that may contain rounding errors and
 * are nearly, but not exactly equal.
 * 
 * @author G. Sturr
 * 
 */
public class FrequencyGgb extends Frequency {

	private static final long serialVersionUID = 1L;

	@Override
	public long getCount(Comparable<?> v) {
		if (v instanceof Double) {
			Double v2 = Kernel.checkDecimalFraction((Double) v);
			return super.getCount(v2);
		}
		return super.getCount(v);
	}

	@Override
	public long getCumFreq(Comparable<?> v) {
		if (v instanceof Double) {
			Double v2 = Kernel.checkDecimalFraction((Double) v);
			return super.getCumFreq(v2);
		}
		return super.getCumFreq(v);
	}

}
