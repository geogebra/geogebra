package org.geogebra.common.kernel.geos;

import org.geogebra.common.kernel.arithmetic.MyArbitraryConstant;

/**
 * These elements use arbitrary constants.
 */
public interface HasArbitraryConstant {

	MyArbitraryConstant getArbitraryConstant();

	void setArbitraryConstant(MyArbitraryConstant constant);
}
