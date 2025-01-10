/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel.algos;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.EquationSolverInterface;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.Function;
import org.geogebra.common.kernel.arithmetic.PolyFunction;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoFunctionable;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.LabelManager;
import org.geogebra.common.util.DoubleUtil;

/**
 * Finds all real roots of a polynomial. TODO: extend for rational functions
 * 
 * @author Markus Hohenwarter
 */
public class AlgoRootsPolynomial extends AlgoIntersect {
	private static final double DELTA = Kernel.MIN_PRECISION * 10;

	private static final int ROOTS = 0;
	private static final int INTERSECT_POLYNOMIALS = 1;
	private static final int INTERSECT_POLY_LINE = 2;
	private static final int MULTIPLE_ROOTS = 3;
	private final int mode;

	protected GeoFunctionable f; // input (g for intersection of polynomials)
	GeoFunctionable g;
	protected GeoLine line; // input (for intersection of polynomial with line)
	protected GeoPoint[] rootPoints; // output, inherited from AlgoIntersect
	// private int rootPointsLength;

	private String[] labels;
	private boolean initLabels;
	protected boolean setLabels;
	protected EquationSolverInterface eqnSolver;
	protected final Solution solution = new Solution();

	protected Function yValFunction;
	// used for AlgoExtremumPolynomial, see setRootPoints()
	/** used for intersection of f and g */
	protected Function diffFunction;
	private final GeoPoint tempPoint;

	/**
	 * Computes all roots of fn
	 * @param cons construction
	 * @param labels output labels
	 * @param fn function
	 * @param labelEnabled whether to allow output labeling
	 */
	public AlgoRootsPolynomial(Construction cons, String[] labels,
			GeoFunctionable fn, boolean labelEnabled) {
		this(cons, labels, labelEnabled && !cons.isSuppressLabelsActive(), fn,
				null, null);
	}

	/**
	 * Intersects polynomials f and g.
	 */
	AlgoRootsPolynomial(Construction cons, GeoFunctionable f,
			GeoFunctionable g) {
		this(cons, null, false, f, g, null);
	}

	/**
	 * Intersects polynomial f and line l.
	 */
	AlgoRootsPolynomial(Construction cons, GeoFunctionable f, GeoLine l) {
		this(cons, null, false, f, null, l);
	}

	protected AlgoRootsPolynomial(Construction cons, String[] labels,
			boolean setLabels, GeoFunctionable f, GeoFunctionable g,
			GeoLine l) {
		super(cons);
		this.f = f;
		this.g = g;
		line = l;

		tempPoint = new GeoPoint(cons);

		// set mode
		if (g != null) {
			mode = INTERSECT_POLYNOMIALS;
		} else if (l != null) {
			mode = INTERSECT_POLY_LINE;
		} else {
			mode = ROOTS;
		}

		if (mode != ROOTS) { // for intersection of f and g resp. line
			diffFunction = new Function(kernel);
		}
		this.labels = labels;
		this.setLabels = setLabels; // should labels be used?

		eqnSolver = cons.getKernel().getEquationSolver();

		// make sure root points is not null
		int number = labels == null ? 1 : Math.max(1, labels.length);
		rootPoints = new GeoPoint[0];
		initRootPoints(number);
		initLabels = true;

		setInputOutput(); // for AlgoElement
		compute();

		// show at least one root point in algebra view
		// this is enforced here:
		if (!rootPoints[0].isDefined()) {
			rootPoints[0].setCoords(0, 0, 1);
			rootPoints[0].update();
			rootPoints[0].setUndefined();
			rootPoints[0].update();
		}
	}

	/**
	 * Helper constructor for inequality zeros: does not create points or add algo to construction
	 * @param f
	 *            function
	 */
	public AlgoRootsPolynomial(GeoFunction f) {
		super(f.cons, false);
		this.f = f;
		tempPoint = new GeoPoint(cons);
		// set mode
		mode = MULTIPLE_ROOTS;
		eqnSolver = cons.getKernel().getEquationSolver();
	}

	/**
	 * @param cons
	 *            construction
	 * @param f
	 *            function
	 */
	public AlgoRootsPolynomial(Construction cons, GeoFunctionable f) {
		super(cons);
		this.f = f;

		tempPoint = new GeoPoint(cons);

		// set mode
		mode = ROOTS;

		eqnSolver = cons.getKernel().getEquationSolver();

		// make sure root points is not null
		int number = 1;
		rootPoints = new GeoPoint[0];
		initRootPoints(number);
		initLabels = true;

		setInputOutput(); // for AlgoElement
		compute();
	}

	/**
	 * The given labels will be used for the resulting points.
	 * 
	 * @param labels
	 *            output labels
	 */
	public void setLabels(String[] labels) {
		this.labels = labels;
		setLabels = !cons.isSuppressLabelsActive();

		// make sure that there are at least as many
		// points as labels
		if (labels != null) {
			initRootPoints(labels.length);
		}

		update();
	}

	@Override
	public Commands getClassName() {
		return Commands.Root;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		switch (mode) {
		default:
		case MULTIPLE_ROOTS:
		case ROOTS: // roots of f
			input = new GeoElement[1];
			input[0] = f.toGeoElement();
			break;

		case INTERSECT_POLYNOMIALS: // intersection of f and g
			input = new GeoElement[2];
			input[0] = f.toGeoElement();
			input[1] = g.toGeoElement();
			break;

		case INTERSECT_POLY_LINE: // intersection of f and line
			input = new GeoElement[2];
			input[0] = f.toGeoElement();
			input[1] = line;
			break;
		}

		super.setOutput(rootPoints);
		noUndefinedPointsInAlgebraView();
		setDependencies();
	}

	/**
	 * @return resulting roots
	 */
	public GeoPoint[] getRootPoints() {
		return rootPoints;
	}

	@Override
	public GeoPoint[] getIntersectionPoints() {
		return rootPoints;
	}

	@Override
	protected GeoPoint[] getLastDefinedIntersectionPoints() {
		return null;
	}

	@Override
	public void compute() {
		switch (mode) {
		default:
		case ROOTS:
			// roots of f
			computeRoots();
			break;
		case MULTIPLE_ROOTS:
			if (f.isDefined()) {
				Function fun = f.getFunctionForRoot();
				// get polynomial factors and calc roots
				calcRootsMultiple(fun, 0, solution, eqnSolver, false);
			} else {
				solution.resetRoots();
			}
			break;
		case INTERSECT_POLYNOMIALS:
			// intersection of f and g
			computePolynomialIntersection();
			break;

		case INTERSECT_POLY_LINE:
			// intersection of f and line
			computePolyLineIntersection();
			break;
		}

		setRootPoints(solution.curRoots, solution.curRealRoots);
	}

	// roots of f
	protected void computeRoots() {
		if (f.isDefined()) {
			Function fun = f.getFunctionForRoot();
			// get polynomial factors and calc roots
			calcRoots(fun, 0);
		} else {
			solution.resetRoots();
		}
	}

	// intersection of f and g
	private void computePolynomialIntersection() {
		if (f.isDefined() && g.isDefined()) {
			Function fun = f.getFunction();
			yValFunction = fun;
			// get difference f - g
			updateDiffFunctions();
			calcRoots(diffFunction, 0);

			// check if the intersection points are really on the functions
			// due to interval restrictions this might not be the case
			for (int i = 0; i < solution.curRealRoots; i++) {
				if (!DoubleUtil.isEqual(fun.value(solution.curRoots[i]),
						g.value(solution.curRoots[i]),
						Kernel.MIN_PRECISION)) {
					solution.removeRoot(i);
					i--;
				}

			}

		} else {
			solution.resetRoots();
		}
	}

	/**
	 * Compute difference between functions, overridden for conditional case
	 */
	protected void updateDiffFunctions() {
		Function.difference(f.getFunction(), g.getFunction(),
				diffFunction);
	}

	// intersection of f and line
	private void computePolyLineIntersection() {
		if (f.isDefined() && line.isDefined()) {
			Function fun = f.getFunction();
			yValFunction = fun;

			// check for vertical line a*x + c = 0: intersection at x=-c/a
			if (DoubleUtil.isZero(line.y)) {
				solution.setSingleRoot(-line.z / line.x);
			}
			// standard case
			else {
				// get difference f - line
				updateDiffLine();
				calcRoots(diffFunction, 0);
			}

			// check if the intersection points really are on the line
			// this is important for segments and rays
			// following must be done for both vertical and standard
			for (int i = 0; i < solution.curRealRoots; i++) {
				tempPoint.setCoords(solution.curRoots[i],
						fun.value(solution.curRoots[i]), 1.0);
				if (!line.isIntersectionPointIncident(tempPoint,
						Kernel.MIN_PRECISION)) {
					solution.removeRoot(i);
					i--;
				}
			}
		} else {
			solution.curRealRoots = 0;
		}
	}

	/**
	 * Compute difference between function and line, overridden for conditional
	 * case
	 */
	protected void updateDiffLine() {
		Function.difference(f.getFunction(), line,
				diffFunction);
	}

	/**
	 * Calculates the roots of the given function resp. its derivative, stores
	 * them in solution.curRoots and sets solution.curRealRoots to the number of
	 * real roots found.
	 * 
	 * @param fun
	 *            function
	 * 
	 * @param derivDegree
	 *            degree of derivative to compute roots from
	 */
	public final void calcRoots(Function fun, int derivDegree) {
		UnivariateFunction evalFunction = calcRootsMultiple(fun, derivDegree,
				solution, eqnSolver, true);

		if (solution.curRealRoots > 1) {
			solution.sortAndMakeUnique();
		}

		// for first or second derivative we only
		// want roots where the signs changed
		// i.e. we only want extrema and inflection points
		if (derivDegree > 0) {
			solution.ensureSignChanged(evalFunction, DELTA);
		}
	}

	/**
	 * @param fun
	 *            function
	 * @param derivDegree
	 *            derivative degree
	 * @param solution
	 *            output solution
	 * @param eqnSolver
	 *            solver
	 * @return function used for root finding
	 */
	public static UnivariateFunction calcRootsMultiple(Function fun,
			int derivDegree, Solution solution,
			EquationSolverInterface eqnSolver) {
		return calcRootsMultiple(fun, derivDegree, solution, eqnSolver, false);
	}

	/**
	 * @param fun
	 *            function
	 * @param derivDegree
	 *            derivative degree
	 * @param solution
	 *            output solution
	 * @param eqnSolver
	 *            solver
	 * @param skipDoubleRoots whether double roots *may* be skipped
	 *           (ensuring uniqueness and sorting is up to the caller)
	 * @return function used for root finding
	 */
	public static UnivariateFunction calcRootsMultiple(Function fun,
			int derivDegree, Solution solution,
			EquationSolverInterface eqnSolver, boolean skipDoubleRoots) {
		LinkedList<PolyFunction> factorList;
		PolyFunction derivPoly = null; // only needed for derivatives
		UnivariateFunction evalFunction = null; // needed to remove wrong extrema
												// and inflection points

		// get polynomial factors for this function
		if (derivDegree > 0) {
			// try to get the factors of the symbolic derivative
			factorList = fun.getSymbolicPolynomialDerivativeFactors(derivDegree,
					true);

			// if this didn't work take the derivative of the numeric
			// expansion of this function
			if (factorList == null) {
				derivPoly = fun.getNumericPolynomialDerivative(derivDegree,
						false, false, true);
				evalFunction = derivPoly;
			} else {
				evalFunction = fun.getDerivativeNoFractions(derivDegree, true);
			}
		} else {
			// standard case
			factorList = fun.getPolynomialFactors(skipDoubleRoots, false);
		}

		double[] roots;
		int realRoots;
		solution.curRealRoots = 0; // reset solution.curRoots index

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
					return null;
				}

				// now let's compute the roots of this factor
				// compute all roots of polynomial polyFun
				roots = polyFun.getCoeffsCopy();
				realRoots = eqnSolver.polynomialRoots(roots, true);
				solution.addToCurrentRoots(roots, realRoots);
			}
		}
		// we've got one factor, i.e. derivPoly
		else if (derivPoly != null) {
			// compute all roots of derivPoly
			roots = derivPoly.getCoeffsCopy();
			realRoots = eqnSolver.polynomialRoots(roots, false);
			solution.addToCurrentRoots(roots, realRoots);
		} else {
			return null;
		}
		if (solution.curRealRoots > 1) {
			Arrays.sort(solution.curRoots, 0, solution.curRealRoots);
		}
		return evalFunction;

	}

	// roots array and number of roots
	protected final void setRootPoints(double[] roots, int number) {
		initRootPoints(number);

		// now set the new values of the roots
		for (int i = 0; i < number; i++) {
			if (yValFunction == null) {
				// check if defined
				// if (Double.isNaN(f.evaluate(roots[i])))
				// rootPoints[i].setUndefined();
				// else
				rootPoints[i].setCoords(roots[i], 0.0, 1.0); // root point
			} else { // extremum or turnal point
				rootPoints[i].setCoords(roots[i],
						yValFunction.value(roots[i]), 1.0);
			}
		}

		// all other roots are undefined
		for (int i = number; i < rootPoints.length; i++) {
			rootPoints[i].setUndefined();
		}

		if (setLabels) {
			updateLabels(number);
		}
	}

	// number is the number of current roots
	protected void updateLabels(int number) {
		if (initLabels) {
			LabelManager.setLabels(labels, rootPoints);
			initLabels = false;
		} else {
			for (int i = 0; i < number; i++) {
				// check labeling
				if (!rootPoints[i].isLabelSet()) {
					// use user specified label if we have one
					String newLabel = (labels != null && i < labels.length)
							? labels[i] : null;
					rootPoints[i].setLabel(newLabel);
				}
			}
		}

		// all other roots are undefined
		for (int i = number; i < rootPoints.length; i++) {
			rootPoints[i].setUndefined();
		}
	}

	/**
	 * Removes only one single output element if possible. If this is not
	 * possible the whole algorithm is removed.
	 */
	@Override
	public void remove(GeoElement output) {
		// only single undefined points may be removed
		for (int i = 0; i < rootPoints.length; i++) {
			if (rootPoints[i] == output && !rootPoints[i].isDefined()) {
				removeRootPoint(i);
				return;
			}
		}

		// if we get here removing output was not possible
		// so we remove the whole algorithm
		super.remove();
	}

	protected void initRootPoints(int number) {
		// make sure that there are enough points
		if (rootPoints.length < number) {
			GeoPoint[] temp = new GeoPoint[number];
			for (int i = 0; i < rootPoints.length; i++) {
				temp[i] = rootPoints[i];
				temp[i].setCoords(0, 0, 1); // init as defined
			}
			for (int i = rootPoints.length; i < temp.length; i++) {
				temp[i] = new GeoPoint(cons);
				temp[i].setCoords(0, 0, 1); // init as defined
				temp[i].setParentAlgorithm(this);
			}
			rootPoints = temp;
			super.setOutput(rootPoints);
		}
	}

	private void removeRootPoint(int pos) {
		rootPoints[pos].doRemove();

		// build new rootPoints array without the removed point
		GeoPoint[] temp = new GeoPoint[rootPoints.length - 1];
		int i;
		for (i = 0; i < pos; i++) {
			temp[i] = rootPoints[i];
		}
		for (i = pos + 1; i < rootPoints.length; i++) {
			temp[i - 1] = rootPoints[i];
		}
		rootPoints = temp;
	}

	@Override
	public String toString(StringTemplate tpl) {
		return getLoc().getPlainDefault("RootOfA", "Root of %0",
				f.getLabel(tpl));
	}

	/**
	 * @return all polynomial roots
	 */
	public double[] getRealRoots() {
		double[] ret = new double[solution.curRealRoots];
		System.arraycopy(solution.curRoots, 0, ret, 0, solution.curRealRoots);
		return ret;
	}
}
