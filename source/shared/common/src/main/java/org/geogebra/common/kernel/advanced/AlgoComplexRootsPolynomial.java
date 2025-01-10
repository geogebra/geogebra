package org.geogebra.common.kernel.advanced;

import java.util.Iterator;
import java.util.LinkedList;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.EquationSolverInterface;
import org.geogebra.common.kernel.algos.AlgoRootsPolynomial;
import org.geogebra.common.kernel.algos.Solution;
import org.geogebra.common.kernel.arithmetic.Function;
import org.geogebra.common.kernel.arithmetic.PolyFunction;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoFunctionable;

public class AlgoComplexRootsPolynomial extends AlgoRootsPolynomial {

	double[] curComplexRoots;

	public AlgoComplexRootsPolynomial(Construction cons, String[] labels,
			GeoFunctionable f) {
		super(cons, labels, f, true);
	}

	@Override
	public void compute() {

		computeComplexRoots();

		setRootPoints(solution.curRoots, curComplexRoots,
				solution.curRealRoots);
	}

	@Override
	public Commands getClassName() {
		return Commands.ComplexRoot;
	}

	// roots of f
	private void computeComplexRoots() {
		if (f.isDefined()) {
			Function fun = f.getFunctionForRoot();
			// get polynomial factors and calc roots
			curComplexRoots = calcComplexRoots(fun, solution, curComplexRoots, eqnSolver);
		} else {
			solution.resetRoots();
		}
	}

	/**
	 * @param fun Function
	 * @param solution Solution
	 * @param curComplexRoots current complex roots
	 * @param eqnSolver equation solver
	 * @return array of current complex roots
	 */
	public static double[] calcComplexRoots(Function fun, Solution solution,
			double[] curComplexRoots, EquationSolverInterface eqnSolver) {
		LinkedList<PolyFunction> factorList;
		double[] complexRoots = curComplexRoots;

		// get polynomial factors for this function

		factorList = fun.getPolynomialFactors(true, false);

		double[] real, complex;
		int noOfRoots;
		solution.resetRoots(); // reset solution.curRoots index

		// we got a list of polynomial factors
		if (factorList != null) {
			// compute the roots of every single factor
			Iterator<PolyFunction> it = factorList.iterator();
			while (it.hasNext()) {
				PolyFunction polyFun = it.next();

				// update the current coefficients of polyFun
				// (this is needed for SymbolicPolyFunction objects)
				if (!polyFun.updateCoeffValues()) {
					// current coefficients are not defined
					solution.curRealRoots = 0;
					return complexRoots;
				}

				// now let's compute the roots of this factor
				// compute all roots of polynomial polyFun
				if (polyFun.hasZeroRoot()) {
					complexRoots = addToCurrentRoots(new double[]{0}, new double[]{0},
							1, solution, complexRoots);
				}
				real = polyFun.getCoeffsCopyNoTrailingZeros();
				complex = new double[real.length];
				noOfRoots = eqnSolver.polynomialComplexRoots(real, complex);
				complexRoots =
						addToCurrentRoots(real, complex, noOfRoots, solution, complexRoots);
			}
			return complexRoots;
		}
		return complexRoots;
	}

	// add first number of doubles in roots to current roots
	private static double[] addToCurrentRoots(double[] real, double[] complex,
			int number, Solution solution, double[] curComplexRoots) {
		double[] complexRoots = curComplexRoots;
		int length = solution.curRealRoots + number;
		if (length >= solution.curRoots.length) { // ensure space
			double[] temp = new double[2 * length];
			double[] temp2 = new double[2 * length];
			for (int i = 0; i < solution.curRealRoots; i++) {
				temp[i] = solution.curRoots[i];
				temp2[i] = complexRoots[i];
			}
			solution.curRoots = temp;
			complexRoots = temp2;
		}

		if (complexRoots == null) {
			complexRoots = new double[solution.curRoots.length];
		}

		// insert new roots
		for (int i = 0; i < number; i++) {
			solution.curRoots[solution.curRealRoots + i] = real[i];
			complexRoots[solution.curRealRoots + i] = complex[i];
		}
		solution.curRealRoots += number;
		return complexRoots;
	}

	// roots array and number of roots
	final private void setRootPoints(double[] real, double[] complex,
			int number) {
		initRootPoints(number);

		// now set the new values of the roots
		for (int i = 0; i < number; i++) {
			rootPoints[i].setCoords(real[i], complex[i], 1.0); // root point
			rootPoints[i].setComplex();
		}

		// all other roots are undefined
		for (int i = number; i < rootPoints.length; i++) {
			rootPoints[i].setUndefined();
		}

		if (setLabels) {
			updateLabels(number);
		}
	}

}
