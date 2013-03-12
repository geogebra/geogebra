package geogebra.common.kernel;

/**
 * Interface for Equation solver
 *
 */
public interface EquationSolverInterface {

	/**
	 * @param eqn coefficients
	 * @param roots roots
	 * @return number of roots
	 */
	int solveCubic(double[] eqn, double[] roots,double eps);

	/**
	 * Solves the quadratic whose coefficients are in the <code>eqn</code> array
	 * and places the non-complex roots into the <code>res</code> array,
	 * returning the number of roots. The quadratic solved is represented by the
	 * equation:
	 * 
	 * <pre>
	 *     eqn = {C, B, A};
	 *     ax^2 + bx + c = 0
	 * </pre>
	 * 
	 * A return value of <code>-1</code> is used to distinguish a constant
	 * equation, which might be always 0 or never 0, from an equation that has
	 * no zeroes.
	 * @param equation coefficients
	 * @param roots roots
	 * 
	 * @return the number of roots, or <code>-1</code> if the equation is a
	 *         constant.
	 */
	int solveQuadratic(double[] equation, double[] roots, double eps);

	/**
	 * * @param equation coefficients
	 * @param roots roots
	 * @return number of roots
	 */
	int solveQuartic(double[] equation, double[] roots, double eps);
	/**
	 * Computes all roots of a polynomial using Laguerre's method for degrees >
	 * 3. The roots are polished and only distinct roots are returned.
	 * 
	 * @param roots
	 *            array with the coefficients of the polynomial
	 * @param multiple true to allow multiple roots
	 * @return number of realRoots found
	 */
	int polynomialRoots(double[] roots, boolean multiple);

	/**
	 * Computes all roots of a polynomial using Laguerre's method for degrees >
	 * 3. The roots are polished and only distinct roots are returned.
	 * @param real real parts
	 * @param complex complex parts
	 * 
	 * @return number of realRoots found
	 */
	int polynomialComplexRoots(double[] real, double[] complex);

}
