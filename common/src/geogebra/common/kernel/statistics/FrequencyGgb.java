package geogebra.common.kernel.statistics;

import geogebra.common.kernel.Kernel;

import org.apache.commons.math.stat.Frequency;

/**
 * Extension of the Apache Commons Frequency class. Adds methods for handling
 * comparisons with double values that may contain rounding errors and are
 * nearly, but not exactly equal.
 * 
 * @author G. Sturr
 * 
 */
public class FrequencyGgb extends Frequency {

	/**
	 * Returns the number of values = v.
	 * 
	 * @param v
	 *            the value to lookup.
	 * @return the frequency of v.
	 */
	public long getCount(double v) {
		Double v2 = Kernel.checkDecimalFraction(v);
		return getCount(v2);
	}

	/**
	 * Returns the cumulative frequency of values less than or equal to v.
	 * <p>
	 * Returns 0 if v is not comparable to the values set.
	 * </p>
	 * 
	 * @param v
	 *            the value to lookup
	 * @return the proportion of values equal to v
	 */
	public long getCumFreq(double v) {
		Double v2 = Kernel.checkDecimalFraction(v);
		return getCumFreq(v2);
	}

}
