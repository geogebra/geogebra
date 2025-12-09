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

/**
 * Symbolic vector, coordinates are expression values.
 *
 */
public interface MyVecNDNode extends ExpressionValue, ReplaceChildrenByValues {

	/**
	 * @return whether this vector is for CAS
	 */
	public boolean isCASVector();

	/**
	 * Set this vector to CAS
	 */
	public void setupCASVector();

	/**
	 * @return 2 or 3
	 */
	public int getDimension();

	/**
	 * 
	 * @return x component
	 */
	public ExpressionValue getX();

	/**
	 * 
	 * @return y component
	 */
	public ExpressionValue getY();

	/**
	 * 
	 * @return z component (or null if getDimension() = 2)
	 */
	public ExpressionValue getZ();

	/**
	 * Sets the printing mode to vector printing
	 */
	void setVectorPrintingMode();
}
