/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.common.kernel.arithmetic;

import org.geogebra.common.kernel.kernelND.GeoVecInterface;

/**
 * tag for VectorValue and Vector3DValue
 * 
 * @author mathieu
 *
 */
public interface VectorNDValue extends ExpressionValue {

	/**
	 * @return string mode: Kernel.COORD_COMPLEX, COORD_CARTESIAN etc.
	 */
	public int getToStringMode();

	/**
	 * @return dimension
	 */
	public int getDimension();

	/**
	 * 
	 * @return vector
	 */
	public GeoVecInterface getVector();

	/**
	 * @return array of coordinates
	 */
	public double[] getPointAsDouble();

	/**
	 * @param mode
	 *            one of Kernel.COORD_* constants
	 */
	public void setMode(int mode);

}
