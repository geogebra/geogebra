package org.geogebra.common.kernel.implicit;

import java.util.ArrayList;

import org.apache.commons.math.analysis.polynomials.PolynomialFunction;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.EquationSolverInterface;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.algos.AlgoIntersect;
import org.geogebra.common.kernel.algos.AlgoSimpleRootsPolynomial;
import org.geogebra.common.kernel.algos.GetCommand;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoPoly;
import org.geogebra.common.kernel.geos.GeoSegment;
import org.geogebra.common.kernel.geos.GeoVec3D;

/**
 * @author thilina
 *
 */
public class AlgoIntersectImplicitpolyPolyLine extends AlgoIntersect {

	/**
	 * input implicit polynomial
	 */
	protected GeoImplicitPoly implicitPolynomial;
	/**
	 * inpupt polyline/polygon
	 */
	protected GeoPoly poly;
	protected OutputHandler<GeoPoint> outputPoints;
	protected boolean hasLabels, polyclosed;

	protected int numOfOutputPoints, polyPointCount, segCountOfPoly;
	protected ArrayList<Coords> intersectCoords;

	private GeoPoint[] tempSegEndPoints;
	private GeoSegment tempSeg;

	private PolynomialFunction tx;
	private PolynomialFunction ty;

	private EquationSolverInterface eqnSolver;

	/**
	 * constructor with labels for intersection between implicitPoly and
	 * PolyLine
	 * 
	 * @param cons
	 *            construction
	 * @param labels
	 *            output labels
	 * @param implicitPolynomial
	 *            input implicit polynomial
	 * @param poly
	 *            input polyline/polygon
	 * @param polyClosed
	 *            states whether the input geoPOyl is a polyline or polygon
	 */
	public AlgoIntersectImplicitpolyPolyLine(Construction cons,
			String[] labels, GeoImplicitPoly implicitPolynomial,GeoPoly poly,
			boolean polyClosed) {
		this(cons, implicitPolynomial, poly, polyClosed);

		if (!cons.isSuppressLabelsActive()) {
			setLabels(labels);
			hasLabels = true;
		}

		update();
	}

	/**
	 * common constructor for intersection between implicitPoly and PolyLine
	 * 
	 * @param cons
	 *            construction
	 * @param implicitPolynomial
	 *            input implicit polynomial
	 * @param poly
	 *            input polyline/polygon
	 * @param polyClosed
	 *            states whether the input geoPOyl is a polyline or polygon
	 */
	public AlgoIntersectImplicitpolyPolyLine(Construction cons,
			GeoImplicitPoly implicitPolynomial, GeoPoly poly, boolean polyClosed) {
		super(cons);
		this.implicitPolynomial = implicitPolynomial;
		this.poly = poly;
		this.polyclosed = polyClosed;

		initElements();
		setInputOutput();
		setDependencies();
		compute();
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
		input[0] = getImplicitPolynomial();
		input[1] = (GeoElement) getPoly();

	}

	@Override
	public void compute() {
		numOfOutputPoints = 0;
		intersectCoords = new ArrayList<Coords>();

		// calculate intersectpaths between poly and conic
		for (int index = 0; index < segCountOfPoly; index++) {

			tempSegEndPoints[0] = getPoly().getPoint(index);
			tempSegEndPoints[1] = getPoly().getPoint(
					(index + 1) % polyPointCount);
			GeoVec3D.lineThroughPoints(tempSegEndPoints[0],
					tempSegEndPoints[1], tempSeg);
			tempSeg.setPoints(tempSegEndPoints[0], tempSegEndPoints[1]);
			tempSeg.calcLength();

			computePolyLineIntersection(tempSeg, intersectCoords);
		}

		numOfOutputPoints = intersectCoords.size();
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

	@Override
	public GetCommand getClassName() {
		return Commands.Intersect;
	}

	@Override
	public int getRelatedModeID() {
		return EuclidianConstants.MODE_INTERSECT;
	}

	/**
	 * calculates intersection points between given segment and implicitpoly.
	 * assign calculated points to given intersectCoords arrayList
	 */
	private void computePolyLineIntersection(GeoSegment tempSeg2,
			ArrayList<Coords> intersectCoords2) {
		double startP[] = new double[2];
		tempSeg2.getInhomPointOnLine(startP);
		tx = new PolynomialFunction(new double[] { startP[0], tempSeg2.getY() }); // x=p1+t*r1
		ty = new PolynomialFunction(
				new double[] { startP[1], -tempSeg2.getX() }); // y=p2+t*r2
		double maxT = tempSeg2.getMaxParameter();
		double minT = tempSeg2.getMinParameter();

		PolynomialFunction sum = null;
		// Insert x and y (univariat)polynomials via the Horner-scheme
		double[][] coeff = getImplicitPolynomial().getCoeff();
		if (coeff != null) {
			sum = lineIntersect(coeff, tx, ty);
		}
		setRootsPolynomialWithinRange(intersectCoords2, sum, minT, maxT);

	}

	public static PolynomialFunction lineIntersect(double[][] coeff,
			PolynomialFunction tx, PolynomialFunction ty) {
		PolynomialFunction sum = null;
		PolynomialFunction zs = null;
		for (int i = coeff.length - 1; i >= 0; i--) {
			zs = new PolynomialFunction(
					new double[] { coeff[i][coeff[i].length - 1] });
			for (int j = coeff[i].length - 2; j >= 0; j--) {
				zs = zs.multiply(ty).add(
						new PolynomialFunction(new double[] { coeff[i][j] }));// y*zs+coeff[i][j];
			}
			if (sum == null)
				sum = zs;
			else
				sum = sum.multiply(tx).add(zs);// sum*x+zs;
		}
		return sum;
	}

	private void setRootsPolynomialWithinRange(
			ArrayList<Coords> intersectCoords2, PolynomialFunction rootsPoly,
			double min, double max) {
		double roots[] = rootsPoly.getCoefficients();
		int nrRealRoots = 0;
		if (roots.length > 1)
			nrRealRoots = AlgoSimpleRootsPolynomial.getRoots(roots, eqnSolver);

		for (int i = 0; i < nrRealRoots; ++i) {
			if (Kernel.isGreater(roots[i], max, Kernel.STANDARD_PRECISION)
					|| Kernel.isGreater(min, roots[i],
							Kernel.STANDARD_PRECISION))
				roots[i] = Double.NaN;
		}

		// makePoints(roots, nrRealRoots);
		// protected double getYValue(double t) {
		// return ty.value(t); }
		// @Override
		// protected double getXValue(double t) {
		// return tx.value(t); }
		int count = 0;
		for (int i = 0; i < nrRealRoots; i++) {
			if (Double.isNaN(roots[i]))
				continue;
			Coords pair = new Coords(tx.value(roots[i]), ty.value(roots[i]), 1);
			for (int k = 1; k < count + 1; k++) {
				if (count > 0
						&& distancePairSq(
								pair,
								intersectCoords2.get(intersectCoords2.size()
										- k)) < Kernel.STANDARD_PRECISION) {
					pair = null;
					break;
				}
			}
			if (pair != null) {
				intersectCoords2.add(pair);
				count++;
			}
		}
	}

	private static double distancePairSq(Coords p1, Coords p2) {
		return (p1.getX() - p2.getX()) * (p1.getX() - p2.getX())
				+ (p1.getY() - p2.getY()) * (p1.getY() - p2.getY());
	}

	/**
	 * initializes auxiliary elements of the algo
	 */
	private void initElements() {
		this.outputPoints = createOutputPoints();
		this.hasLabels = false;

		numOfOutputPoints = 0;
		polyPointCount = (getPoly().getPoints()).length;
		segCountOfPoly = isPolyclosed() ? polyPointCount : polyPointCount - 1;

		tempSegEndPoints = new GeoPoint[2];
		for (int i = 0; i < tempSegEndPoints.length; i++) {
			tempSegEndPoints[i] = new GeoPoint(getConstruction());
		}
		tempSeg = new GeoSegment(getConstruction());

		eqnSolver = getConstruction().getKernel().getEquationSolver();
	}


	/**
	 * sets labels of output points
	 * 
	 * @param labels
	 *            output label
	 */
	public void setLabels(String[] labels) {
		if (labels != null && labels.length == 1 && outputPoints.size() > 1
				&& labels[0] != null && !labels[0].equals("")) {
			outputPoints.setIndexLabels(labels[0]);

		} else {
			outputPoints.setLabels(labels);
		}

		update();
	}

	/**
	 * @return handler for output points
	 */
	protected OutputHandler<GeoPoint> createOutputPoints() {
		return new OutputHandler<GeoPoint>(new elementFactory<GeoPoint>() {
			public GeoPoint newElement() {
				GeoPoint p = new GeoPoint(cons);
				p.setCoords(0, 0, 1);
				p.setParentAlgorithm(AlgoIntersectImplicitpolyPolyLine.this);
				return p;
			}
		});
	}

	/**
	 * getter of input geoImplicitPolinomial
	 * 
	 * @return input implicit polynomial
	 */
	public GeoImplicitPoly getImplicitPolynomial() {
		return implicitPolynomial;
	}

	/**
	 * getter of input geoPoly (polyline/polygon)
	 * 
	 * @return input geoPoly
	 */
	public GeoPoly getPoly() {
		return poly;
	}

	/**
	 * getter of poly type
	 * 
	 * @return true->polygon, false->polyline
	 */
	public boolean isPolyclosed() {
		return polyclosed;
	}

}
