package org.geogebra.common.kernel.geos;

import org.geogebra.common.kernel.arithmetic.ArbitraryConstantRegistry;

/**
 * These elements use arbitrary constants.
 */
public interface HasArbitraryConstant {

	/**
	 * @return arbitrary constant registry
	 */
	ArbitraryConstantRegistry getArbitraryConstant();

	/**
	 * @param constant arbitrary constant registry
	 */
	void setArbitraryConstant(ArbitraryConstantRegistry constant);
}
