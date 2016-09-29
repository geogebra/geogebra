package org.geogebra.common.kernel.cas;

import org.geogebra.common.kernel.arithmetic.ReplaceChildrenByValues;

/**
 * Interface for algorithm for definite integral to make sure it's drawn even if
 * undefined
 */
public interface AlgoIntegralDefiniteInterface extends ReplaceChildrenByValues {
	/**
	 * @return if the integral is for shading only
	 */
	public boolean evaluateOnly();
}
