/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra.common.kernel.algos;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.EquationSolverInterface;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.arithmetic.Function;
import geogebra.common.kernel.arithmetic.PolyFunction;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.kernel.geos.GeoLine;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.roots.RealRootFunction;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Finds all real roots of a polynomial. TODO: extend for rational functions
 * 
 * @author Markus Hohenwarter
 */
public class AlgoRootsPolynomial extends AlgoIntersect {

	private static final int ROOTS = 0;
	private static final int INTERSECT_POLYNOMIALS = 1;
	private static final int INTERSECT_POLY_LINE = 2;
	private static final int MULTIPLE_ROOTS = 3;
	private int mode;

	protected GeoFunction f; // input (g for intersection of polynomials)
	GeoFunction g;
	private GeoLine line; // input (for intersection of polynomial with line)
	protected GeoPoint[] rootPoints; // output, inherited from AlgoIntersect
	// private int rootPointsLength;

	private String[] labels;
	private boolean initLabels;
	protected boolean setLabels;
	protected EquationSolverInterface eqnSolver;
	protected double[] curRoots = new double[30]; // current roots
	protected int curRealRoots;

	protected Function yValFunction;
	// used for AlgoExtremumPolynomial, see setRootPoints()
	private Function diffFunction; // used for intersection of f and g
	private GeoPoint tempPoint;

	/**
	 * Computes all roots of f
	 */
	public AlgoRootsPolynomial(Construction cons, String[] labels, GeoFunction f) {
		this(cons, labels, !cons.isSuppressLabelsActive(), f, null, null);
	}

	/**
	 * Intersects polynomials f and g.
	 */
	AlgoRootsPolynomial(Construction cons, GeoFunction f, GeoFunction g) {
		this(cons, null, false, f, g, null);
	}

	/**
	 * Intersects polynomial f and line l.
	 */
	AlgoRootsPolynomial(Construction cons, GeoFunction f, GeoLine l) {
		this(cons, null, false, f, null, l);
	}

	private AlgoRootsPolynomial(Construction cons, String[] labels,
			boolean setLabels, GeoFunction f, GeoFunction g, GeoLine l) {
		super(cons);
		this.f = f;
		this.g = g;
		line = l;

		tempPoint = new GeoPoint(cons);

		// set mode
		if (g != null)
			mode = INTERSECT_POLYNOMIALS;
		else if (l != null) {
			mode = INTERSECT_POLY_LINE;
		} else
			mode = ROOTS;

		if (mode != ROOTS) { // for intersection of f and g resp. line
			diffFunction = new Function(kernel);
		}
		this.labels = labels;
		this.setLabels = setLabels; // should lables be used?

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

	public AlgoRootsPolynomial(GeoFunction f) {
		super(f.cons);
		this.f = f;

		tempPoint = new GeoPoint(cons);

		// set mode
		mode = MULTIPLE_ROOTS;

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

	public AlgoRootsPolynomial(Construction cons, GeoFunction f) {
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
	 */
	public void setLabels(String[] labels) {
		this.labels = labels;
		setLabels = !cons.isSuppressLabelsActive();

		// make sure that there are at least as many
		// points as labels
		if (labels != null)
			initRootPoints(labels.length);

		update();
	}

	@Override
	public Algos getClassName() {
		return Algos.AlgoRootsPolynomial;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		switch (mode) {
		case MULTIPLE_ROOTS:
		case ROOTS: // roots of f
			input = new GeoElement[1];
			input[0] = f;
			break;

		case INTERSECT_POLYNOMIALS: // intersection of f and g
			input = new GeoElement[2];
			input[0] = f;
			input[1] = g;
			break;

		case INTERSECT_POLY_LINE: // intersection of f and line
			input = new GeoElement[2];
			input[0] = f;
			input[1] = line;
			break;
		}

		super.setOutput(rootPoints);
		noUndefinedPointsInAlgebraView();
		setDependencies();
	}

	public GeoPoint[] getRootPoints() {
		return rootPoints;
	}

	@Override
	protected GeoPoint[] getIntersectionPoints() {
		return rootPoints;
	}

	@Override
	protected GeoPoint[] getLastDefinedIntersectionPoints() {
		return null;
	}

	@Override
	public void compute() {
		switch (mode) {
		case ROOTS:
			// roots of f
			computeRoots();
			break;
		case MULTIPLE_ROOTS:
			if (f.isDefined()) {
				Function fun = f.getFunction();
				// get polynomial factors anc calc roots
				calcRootsMultiple(fun, 0);
			} else {
				curRealRoots = 0;
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

		setRootPoints(curRoots, curRealRoots);
	}

	// roots of f
	private void computeRoots() {
		if (f.isDefined()) {
			Function fun = f.getFunction();
			// get polynomial factors anc calc roots
			calcRoots(fun, 0);
		} else {
			curRealRoots = 0;
		}
	}

	// intersection of f and g
	private void computePolynomialIntersection() {
		if (f.isDefined() && g.isDefined()) {
			yValFunction = f.getFunction();
			// get difference f - g
			Function.difference(f.getFunction(), g.getFunction(), diffFunction);
			calcRoots(diffFunction, 0);

			// check if the intersection points are really on the functions
			// due to interval restrictions this might not be the case
			for (int i = 0; i < curRealRoots; i++) {
				if (!Kernel.isEqual(f.evaluate(curRoots[i]),
						g.evaluate(curRoots[i]), Kernel.MIN_PRECISION)) {
					removeRoot(i);
					i--;
				}

			}

		} else {
			curRealRoots = 0;
		}
	}

	// intersection of f and line
	private void computePolyLineIntersection() {
		if (f.isDefined() && line.isDefined()) {
			yValFunction = f.getFunction();

			// check for vertical line a*x + c = 0: intersection at x=-c/a
			if (Kernel.isZero(line.y)) {
				double x = -line.z / line.x;
				curRoots[0] = x;
				curRealRoots = 1;
			}
			// standard case
			else {
				// get difference f - line
				Function.difference(f.getFunction(), line, diffFunction);
				calcRoots(diffFunction, 0);
			}

			// check if the intersection points really are on the line
			// this is important for segments and rays
			// Zbynek Konecny 2010-02-12 -- following must be done for both
			// vertical and standard
			for (int i = 0; i < curRealRoots; i++) {
				tempPoint.setCoords(curRoots[i], f.evaluate(curRoots[i]), 1.0);
				if (!line.isIntersectionPointIncident(tempPoint,
						Kernel.MIN_PRECISION)) {
					removeRoot(i);
					i--;
				}
			}
			// end Zbynek Konecny
		} else {
			curRealRoots = 0;
		}
	}

	/**
	 * Calculates the roots of the given function resp. its derivative, stores
	 * them in curRoots and sets curRealRoots to the number of real roots found.
	 * 
	 * @param derivDegree
	 *            degree of derivative to compute roots from
	 */
	public final void calcRoots(Function fun, int derivDegree) {
		RealRootFunction evalFunction = calcRootsMultiple(fun, derivDegree);

		if (curRealRoots > 1) {
			// sort roots and eliminate duplicate ones
			Arrays.sort(curRoots, 0, curRealRoots);

			// eliminate duplicate roots
			double maxRoot = curRoots[0];
			int maxIndex = 0;
			for (int i = 1; i < curRealRoots; i++) {
				if ((curRoots[i] - maxRoot) > Kernel.MIN_PRECISION) {
					maxRoot = curRoots[i];
					maxIndex++;
					curRoots[maxIndex] = maxRoot;
				}
			}
			curRealRoots = maxIndex + 1;
		}

		// for first or second derivative we only
		// want roots where the signs changed
		// i.e. we only want extrema and inflection points
		if (derivDegree > 0) {
			ensureSignChanged(evalFunction);
		}
	}

	public RealRootFunction calcRootsMultiple(Function fun, int derivDegree) {
		LinkedList<PolyFunction> factorList;
		PolyFunction derivPoly = null;// only needed for derivatives
		RealRootFunction evalFunction = null; // needed to remove wrong extrema
												// and inflection points

		// get polynomial factors for this function
		if (derivDegree > 0) {
			// try to get the factors of the symbolic derivative
			factorList = fun.getSymbolicPolynomialDerivativeFactors(
					derivDegree, true);

			// if this didn't work take the derivative of the numeric
			// expansion of this function
			if (factorList == null) {
				derivPoly = fun.getNumericPolynomialDerivative(derivDegree,false);
				evalFunction = derivPoly;
			} else {
				evalFunction = fun.getDerivative(derivDegree);
			}
		} else {
			// standard case
			factorList = fun.getPolynomialFactors(false);
		}

		double[] roots;
		int realRoots;
		curRealRoots = 0; // reset curRoots index

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
					curRealRoots = 0;
					return null;
				}

				// now let's compute the roots of this factor
				// compute all roots of polynomial polyFun
				roots = polyFun.getCoeffsCopy();
				realRoots = eqnSolver.polynomialRoots(roots, true);
				addToCurrentRoots(roots, realRoots);
			}
		}
		// we've got one factor, i.e. derivPoly
		else if (derivPoly != null) {
			// compute all roots of derivPoly
			roots = derivPoly.getCoeffsCopy();
			realRoots = eqnSolver.polynomialRoots(roots, false);
			addToCurrentRoots(roots, realRoots);
		} else
			return null;
		if (curRealRoots > 1)
			Arrays.sort(curRoots, 0, curRealRoots);
		return evalFunction;

	}

	// remove roots where the sign of the function's values did not change
	private void ensureSignChanged(RealRootFunction f) {
		double left, right, leftEval, rightEval;
		boolean signUnChanged;
		for (int i = 0; i < curRealRoots; i++) {
			left = curRoots[i] - DELTA;
			right = curRoots[i] + DELTA;
			// ensure we get a non-zero y value to the left
			int count = 0;
			while (Math.abs(leftEval = f.evaluate(left)) < DELTA
					&& count++ < 100)
				left = left - DELTA;

			// ensure we get a non-zero y value to the right
			count = 0;
			while (Math.abs(rightEval = f.evaluate(right)) < DELTA
					&& count++ < 100)
				right = right + DELTA;

			// Application.debug("leftEval: " + leftEval + ", left: " + left);
			// Application.debug("rightEval: " + rightEval + ", right: " +
			// right);

			// check if the second derivative changed its sign here
			signUnChanged = leftEval * rightEval > 0;
			if (signUnChanged) {
				// remove root[i]
				removeRoot(i);
				i--;
			}
		}
	}

	private static final double DELTA = Kernel.MIN_PRECISION * 10;

	// add first number of doubles in roots to current roots
	private void addToCurrentRoots(double[] roots, int number) {
		int length = curRealRoots + number;
		if (length >= curRoots.length) { // ensure space
			double[] temp = new double[2 * length];
			for (int i = 0; i < curRealRoots; i++) {
				temp[i] = curRoots[i];
			}
			curRoots = temp;
		}

		// insert new roots
		for (int i = 0; i < number; i++) {
			curRoots[curRealRoots + i] = roots[i];
		}
		curRealRoots += number;
	}

	final private void removeRoot(int pos) {
		for (int i = pos + 1; i < curRealRoots; i++) {
			curRoots[i - 1] = curRoots[i];
		}
		curRealRoots--;
	}

	// roots array and number of roots
	protected final void setRootPoints(double[] roots, int number) {
		initRootPoints(number);

		// now set the new values of the roots
		for (int i = 0; i < number; i++) {
			// Application.debug("root[" + i + "] = " + roots[i]);
			if (yValFunction == null) {
				// check if defined
				// if (Double.isNaN(f.evaluate(roots[i])))
				// rootPoints[i].setUndefined();
				// else
				rootPoints[i].setCoords(roots[i], 0.0, 1.0); // root point
			} else { // extremum or turnal point
				rootPoints[i].setCoords(roots[i],
						yValFunction.evaluate(roots[i]), 1.0);

				// Application.debug("   " + rootPoints[i]);
			}
		}

		// all other roots are undefined
		for (int i = number; i < rootPoints.length; i++) {
			rootPoints[i].setUndefined();
		}

		if (setLabels)
			updateLabels(number);
	}

	// number is the number of current roots
	protected void updateLabels(int number) {
		if (initLabels) {
			GeoElement.setLabels(labels, rootPoints);
			initLabels = false;
		} else {
			for (int i = 0; i < number; i++) {
				// check labeling
				if (!rootPoints[i].isLabelSet()) {
					// use user specified label if we have one
					String newLabel = (labels != null && i < labels.length) ? labels[i]
							: null;
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
	public
	void remove(GeoElement output) {
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
		for (i = 0; i < pos; i++)
			temp[i] = rootPoints[i];
		for (i = pos + 1; i < rootPoints.length; i++)
			temp[i - 1] = rootPoints[i];
		rootPoints = temp;
	}

	@Override
	public String toString(StringTemplate tpl) {
		// Michael Borcherds 2008-03-30
		// simplified to allow better Chinese translation
		return app.getPlain("RootOfA", f.getLabel(tpl));
	}

}
