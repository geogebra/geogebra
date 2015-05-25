package org.geogebra.common.kernel.cas;

/**
 * Interface for algorithm for definite integral to make sure it's drawn even if
 * undefined
 */
public interface AlgoIntegralDefiniteInterface {
	/**
	 * @return if the integral is for shading only
	 */
	public boolean evaluateOnly();
}
