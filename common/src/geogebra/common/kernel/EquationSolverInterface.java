package geogebra.common.kernel;

public interface EquationSolverInterface {

	int solveCubic(double[] eqn, double[] roots);

	int solveQuadratic(double[] eigenval, double[] eigenval2);

	int solveQuartic(double[] eqn, double[] roots);
	/**
	 * Computes all roots of a polynomial using Laguerre's method for degrees >
	 * 3. The roots are polished and only distinct roots are returned.
	 * 
	 * @param roots
	 *            array with the coefficients of the polynomial
	 * @return number of realRoots found
	 */
	int polynomialRoots(double[] roots, boolean b);

	int polynomialComplexRoots(double[] real, double[] complex);

}
