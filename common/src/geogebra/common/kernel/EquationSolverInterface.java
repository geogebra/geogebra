package geogebra.common.kernel;

public interface EquationSolverInterface {

	int solveCubic(double[] eqn, double[] roots);

	int solveQuadratic(double[] eigenval, double[] eigenval2);

	int solveQuartic(double[] eqn, double[] roots);

	int polynomialRoots(double[] tRoots, boolean b);

}
