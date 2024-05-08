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
