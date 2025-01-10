package org.geogebra.common.kernel;

import org.geogebra.common.kernel.kernelND.GeoElementND;

/**
 * Interface for algos that are random and hence allow setting result value eg
 * from SetValue[] command
 */
public interface SetRandomValue {

	/**
	 * Changes the random value
	 * 
	 * @param d
	 *            random value
	 * @return whether setting was successful (argument in range)
	 */
	boolean setRandomValue(GeoElementND d);

	/**
	 * @return whether the value is actually random
	 */
	default boolean canSetRandomValue() {
		return true;
	}
}
