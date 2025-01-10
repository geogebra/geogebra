package org.geogebra.common.kernel.geos;

import org.geogebra.common.kernel.arithmetic.ArbitraryConstantRegistry;

/**
 * These elements use arbitrary constants.
 */
public interface HasArbitraryConstant {

	ArbitraryConstantRegistry getArbitraryConstant();

	void setArbitraryConstant(ArbitraryConstantRegistry constant);
}
