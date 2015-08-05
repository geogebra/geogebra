package org.geogebra.common.kernel.algos;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.EquationSolverInterface;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.arithmetic.Function;
import org.geogebra.common.kernel.arithmetic.PolyFunction;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoPoly;
import org.geogebra.common.kernel.geos.GeoSegment;
import org.geogebra.common.kernel.geos.GeoVec3D;
import org.geogebra.common.kernel.roots.RealRootFunction;

public class AlgoIntersectPolynomialPolyLine extends AlgoIntersect {

	protected GeoFunction func;
	protected GeoPoly poly;
	protected boolean polyClosed;
	protected boolean hasLabels;
	
	protected OutputHandler<GeoPoint> outputPoints;

	protected int numOfOutputPoints, polyPointCount, segCountOfPoly;
	protected ArrayList<Coords> intersectCoords;

	private GeoPoint[] tempSegEndPoints;
	private GeoSegment tempSeg;

	protected Function yValFunction;
	protected EquationSolverInterface eqnSolver;


	private double[] curRoots = new double[30]; // current roots
	private int curRealRoots;

	private Function diffFunction;
	private GeoPoint tempPoint;

	/**
	 * constructor with labels
	 * 
	 * @param cons
	 * @param labels
	 * @param func
	 * @param poly
	 */
	public AlgoIntersectPolynomialPolyLine(Construction cons, String[] labels,
			GeoFunction func, GeoPoly poly, boolean polyClosed) {

		this(cons, func, poly, polyClosed);

		setLabels(labels);
		hasLabels = true;

		update();
	}

	/**
	 * Common constructor
	 * 
	 * @param cons
	 * @param func
	 * @param poly
	 */
	public AlgoIntersectPolynomialPolyLine(Construction cons, GeoFunction func,
			GeoPoly poly, boolean polyClosed) {
		super(cons);
		this.func = func;
		this.poly = poly;
		this.polyClosed = polyClosed;

		initElements();

		setInputOutput();

		setDependencies();

		compute();
	}

	private void initElements() {

		numOfOutputPoints = 0;
		polyPointCount = (getPoly().getPoints()).length;
		segCountOfPoly = isPolyClosed() ? polyPointCount : polyPointCount - 1;

		tempSegEndPoints = new GeoPoint[2];
		for (int i = 0; i < tempSegEndPoints.length; i++) {
			tempSegEndPoints[i] = new GeoPoint(getConstruction());
		}
		tempSeg = new GeoSegment(getConstruction());

		diffFunction = new Function(kernel);
		tempPoint = new GeoPoint(getConstruction());
		eqnSolver = cons.getKernel().getEquationSolver();
		curRealRoots = 0;

		outputPoints = this.createOutputPoints();
	}

	private void setLabels(String[] labels) {
		if (labels != null && labels.length == 1 && outputPoints.size() > 1
				&& labels[0] != null && !labels[0].equals("")) {
			outputPoints.setIndexLabels(labels[0]);

		} else {
			outputPoints.setLabels(labels);
		}

	}

	// intersection of f and line
	private void computePolyLineIntersection(GeoSegment seg,
			ArrayList<Coords> intrsctCrds) {
		if (func.isDefined() && seg.isDefined()) {
			yValFunction = func.getFunction();

			// check for vertical line a*x + c = 0: intersection at x=-c/a
			if (Kernel.isZero(seg.y)) {
				double x = -seg.z / seg.x;
				curRoots[0] = x;
				curRealRoots = 1;
			}
			// standard case
			else {
				// get difference f - line
				Function.difference(func.getFunction(), seg, diffFunction);
				calcRoots(diffFunction, 0);
			}

			// check if the intersection points really are on the line
			// this is important for segments and rays
			// Zbynek Konecny 2010-02-12 -- following must be done for both
			// vertical and standard
			for (int i = 0; i < curRealRoots; i++) {
				tempPoint.setCoords(curRoots[i], func.evaluate(curRoots[i]),
						1.0);
				if (seg.isOnPath(tempPoint,
						Kernel.MIN_PRECISION)) {
					intrsctCrds.add(tempPoint.getCoords());
					numOfOutputPoints++;
				}
			}
			// end Zbynek Konecny
		} else {
			curRealRoots = 0;
		}
	}

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
				derivPoly = fun.getNumericPolynomialDerivative(derivDegree,
						false);
				evalFunction = derivPoly;
			} else {
				evalFunction = fun.getDerivative(derivDegree, true);
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

	private static final double DELTA = Kernel.MIN_PRECISION * 10;

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

	/**
	 * 
	 * @return handler for output points
	 */
	protected OutputHandler<GeoPoint> createOutputPoints() {
		return new OutputHandler<GeoPoint>(new elementFactory<GeoPoint>() {
			public GeoPoint newElement() {
				GeoPoint p = new GeoPoint(cons);
				p.setCoords(0, 0, 1);
				p.setParentAlgorithm(AlgoIntersectPolynomialPolyLine.this);
				return p;
			}
		});
	}

	@Override
	public GeoPoint[] getIntersectionPoints() {
		GeoPoint[] iPoint = new GeoPoint[2];
		return outputPoints.getOutput(iPoint);
	}

	@Override
	protected GeoPoint[] getLastDefinedIntersectionPoints() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void setInputOutput() {
		input = new GeoElement[2];
		input[0] = this.func;
		input[1] = (GeoElement) this.poly;

	}

	@Override
	public void compute() {
		numOfOutputPoints = 0;
		intersectCoords = new ArrayList<Coords>();

		// calculate intersectpaths between poly and conic
		for (int index = 0; index < segCountOfPoly; index++) {

			curRealRoots = 0;

			tempSegEndPoints[0] = getPoly().getPoint(index);
			tempSegEndPoints[1] = getPoly().getPoint(
					(index + 1) % polyPointCount);
			GeoVec3D.lineThroughPoints(tempSegEndPoints[0],
					tempSegEndPoints[1], tempSeg);
			tempSeg.setPoints(tempSegEndPoints[0], tempSegEndPoints[1]);
			tempSeg.calcLength();

			computePolyLineIntersection(tempSeg, intersectCoords);
		}

		if (numOfOutputPoints > 0) {
			outputPoints.adjustOutputSize(numOfOutputPoints, false);
			for (int i = 0; i < numOfOutputPoints; i++) {
				outputPoints.getElement(i).setCoords(intersectCoords.get(i),
						true);
			}
		} else {
			outputPoints.adjustOutputSize(1, false);
			outputPoints.getElement(0).setUndefined();
		}
		if (hasLabels) {
			outputPoints.updateLabels();
		}

	}

	/**
	 * getter of input poly
	 * 
	 * @return GeoPoly
	 */
	public GeoPoly getPoly() {
		return this.poly;
	}

	/**
	 * @return whether the poly is closed or not. true means a polyline, false
	 *         means a boundary polygon
	 */
	public boolean isPolyClosed() {
		return polyClosed;
	}

	@Override
	public GetCommand getClassName() {
		return Commands.Intersect;
	}

	@Override
	public int getRelatedModeID() {
		return EuclidianConstants.MODE_INTERSECT;
	}

}
