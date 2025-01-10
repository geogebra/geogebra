package org.geogebra.common.kernel.algos;

import java.util.ArrayList;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.EquationSolverInterface;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Function;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunctionable;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoPoly;
import org.geogebra.common.kernel.geos.GeoSegment;
import org.geogebra.common.kernel.geos.GeoVec3D;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.util.DoubleUtil;

public class AlgoIntersectPolynomialPolyLine extends AlgoIntersect {
	private static final double DELTA = Kernel.MIN_PRECISION * 10;

	protected GeoFunctionable func;
	protected GeoPoly poly;
	protected boolean polyClosed;
	protected boolean hasLabels;

	protected OutputHandler<GeoPoint> outputPoints;

	protected int numOfOutputPoints;
	protected int polyPointCount;
	protected int segCountOfPoly;
	protected ArrayList<Coords> intersectCoords;

	private GeoPoint[] tempSegEndPoints;
	private GeoSegment tempSeg;

	protected EquationSolverInterface eqnSolver;
	private final Solution solution = new Solution();

	private Function diffFunction;
	private GeoPoint tempPoint;

	/**
	 * constructor with labels
	 * 
	 * @param cons
	 *            construction
	 * @param labels
	 *            output labels
	 * @param func
	 *            function
	 * @param poly
	 *            polyline
	 */
	public AlgoIntersectPolynomialPolyLine(Construction cons, String[] labels,
			GeoFunctionable func, GeoPoly poly, boolean polyClosed) {

		this(cons, func, poly, polyClosed);

		if (!cons.isSuppressLabelsActive()) {
			setLabels(labels);
			hasLabels = true;
		}

		update();
	}

	/**
	 * Common constructor
	 * 
	 * @param cons
	 *            construction
	 * @param func
	 *            function
	 * @param poly
	 *            polyline
	 */
	public AlgoIntersectPolynomialPolyLine(Construction cons,
			GeoFunctionable func,
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
		solution.resetRoots();

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

			// check for vertical line a*x + c = 0: intersection at x=-c/a
			if (DoubleUtil.isZero(seg.y)) {
				solution.setSingleRoot(-seg.z / seg.x);
			}
			// standard case
			else {
				// get difference f - line
				Function.difference(func.getFunction(), seg, diffFunction);
				calcRoots(diffFunction, 0);
			}

			// check if the intersection points really are on the line
			// this is important for segments and rays
			// following must be done for both vertical and standard
			for (int i = 0; i < solution.curRealRoots; i++) {
				tempPoint.setCoords(solution.curRoots[i],
						func.value(solution.curRoots[i]), 1.0);
				if (seg.isOnPath(tempPoint, Kernel.MIN_PRECISION)) {
					intrsctCrds.add(tempPoint.getCoords());
					numOfOutputPoints++;
				}
			}
		} else {
			solution.curRealRoots = 0;
		}
	}

	// add first number of doubles in roots to current roots

	/**
	 * Calculates the roots of the given function resp. its derivative, stores
	 * them in solution.curRoots and sets solution.curRealRoots to the number of
	 * real roots found.
	 * 
	 * @param derivDegree
	 *            degree of derivative to compute roots from
	 */
	public final void calcRoots(Function fun, int derivDegree) {
		UnivariateFunction evalFunction = AlgoRootsPolynomial
				.calcRootsMultiple(fun, derivDegree, solution, eqnSolver);

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

	// remove roots where the sign of the function's values did not change

	/**
	 * 
	 * @return handler for output points
	 */
	protected OutputHandler<GeoPoint> createOutputPoints() {
		return new OutputHandler<>(new ElementFactory<GeoPoint>() {
			@Override
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
		input[0] = func.toGeoElement();
		input[1] = poly.toGeoElement();

	}

	@Override
	public void compute() {
		numOfOutputPoints = 0;
		intersectCoords = new ArrayList<>();

		// calculate intersectpaths between poly and conic
		for (int index = 0; index < segCountOfPoly; index++) {

			solution.resetRoots();

			tempSegEndPoints[0] = getPoly().getPoint(index);
			tempSegEndPoints[1] = getPoly()
					.getPoint((index + 1) % polyPointCount);
			GeoVec3D.lineThroughPoints(tempSegEndPoints[0], tempSegEndPoints[1],
					tempSeg);
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
