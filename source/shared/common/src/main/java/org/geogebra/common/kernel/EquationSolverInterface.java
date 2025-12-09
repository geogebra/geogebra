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

package org.geogebra.common.kernel;

/**
 * Interface for Equation solver
 *
 */
public interface EquationSolverInterface {

	/**
	 * @param equation
	 *            coefficients
	 * @param roots
	 *            roots
	 * @param eps
	 *            precision
	 * @return number of roots
	 */
	int solveQuartic(double[] equation, double[] roots, double eps);

	/**
	 * Computes all roots of a polynomial using Laguerre's method for degrees &gt;
	 * 3. The roots are polished and only distinct roots are returned.
	 * 
	 * @param roots
	 *            array with the coefficients of the polynomial
	 * @param multiple
	 *            true to allow multiple roots
	 * @return number of realRoots found
	 */
	int polynomialRoots(double[] roots, boolean multiple);

	/**
	 * Computes all roots of a polynomial using Laguerre's method for degrees &gt;
	 * 3. The roots are polished and only distinct roots are returned.
	 * 
	 * @param real
	 *            real parts
	 * @param complex
	 *            complex parts
	 * 
	 * @return number of realRoots found
	 */
	int polynomialComplexRoots(double[] real, double[] complex);

}
