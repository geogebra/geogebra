package org.geogebra.common.kernel.algos;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.arithmetic.Function;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoPoly;
import org.geogebra.common.kernel.geos.GeoSegment;
import org.geogebra.common.kernel.geos.GeoVec3D;
import org.geogebra.common.util.debug.Log;

public class AlgoIntersectNpFunctionPolyLine extends AlgoRootNewton {

	protected GeoFunction func; // input
	protected GeoPoly poly; // input
	protected GeoPoint startPoint, rootPoint;
	protected boolean hasLabel, polyClosed;

	protected int polySegCount, polyPointLength;

	private Function diffFunction;

	private GeoPoint[] segEndPoints;
	private GeoSegment tempSeg;
	private double disMinCoordsStart;

	/**
	 * constructor with labels
	 * 
	 * @param cons
	 *            construction
	 * @param label
	 *            labels
	 * @param startPoint
	 *            starting point
	 * @param func
	 *            GeoFunction(only non polynomials)
	 * @param poly
	 *            GeoPoly
	 * @param polyClosed
	 *            indicates whether the poly is polygon(true) or polyLine(false)
	 */
	AlgoIntersectNpFunctionPolyLine(Construction cons, String[] label,
			GeoPoint startPoint, GeoFunction func, GeoPoly poly,
			boolean polyClosed) {
		this(cons, startPoint, func, poly, polyClosed);

		if (label != null)
			rootPoint.setLabel(label[0]);
		else
			rootPoint.setLabel(null);
		hasLabel = true;

	}

	/**
	 * common constructor
	 * 
	 * @param cons
	 *            construction
	 * @param func
	 *            GeoFunction(only non polynomials)
	 * @param poly
	 *            GeoPoly
	 * @param polyClosed
	 *            indicates whether the poly is polygon(true) or polyLine(false)
	 */
	AlgoIntersectNpFunctionPolyLine(Construction cons, GeoPoint startPoint,
			GeoFunction func, GeoPoly poly, boolean polyClosed) {
		super(cons);
		this.startPoint = startPoint;
		this.func = func;
		this.poly = poly;
		this.polyClosed = polyClosed;

		initElements();
		setInputOutput(); // for AlgoElement
		setDependencies();
		compute();

	}

	private void initElements() {
		// output
		rootPoint = new GeoPoint(getConstruction());
		tempSeg = new GeoSegment(getConstruction());
		segEndPoints = new GeoPoint[2];
		for (int i = 0; i < segEndPoints.length; i++) {
			segEndPoints[i] = new GeoPoint(getConstruction());
		}
		
		polyPointLength = this.poly.getPoints().length;
		polySegCount = isPolyClosed() ? polyPointLength : polyPointLength - 1;

		diffFunction = new Function(getKernel());
	}

	@Override
	public final void compute() {
		Coords minIntersectCoords = null, currentIntersectCoords = null;

		if (!(getFunction().isDefined() && getPoly().isDefined() && startPoint
				.isDefined())) {
			rootPoint.setUndefined();
			Log.debug("either func, poly, or start is not defined");
			return;
		}

		double disCurrCoordsStart = -1.0;
		for (int index = 0; index < polySegCount; index++) {

			segEndPoints[0] = getPoly().getPoint(index);
			segEndPoints[1] = getPoly().getPoint(
					(index + 1) % this.polyPointLength);
			GeoVec3D.lineThroughPoints(segEndPoints[0], segEndPoints[1],
					tempSeg);
			tempSeg.setPoints(segEndPoints[0], segEndPoints[1]);
			tempSeg.calcLength();

			currentIntersectCoords = calcIntersectionPoint(getFunction(),
					tempSeg);

			if (minIntersectCoords == null) {
				if (currentIntersectCoords != null) {
					minIntersectCoords = currentIntersectCoords;
					disMinCoordsStart = distanceSqr(currentIntersectCoords,
							startPoint.getCoords());
				}
			} else {
				if (currentIntersectCoords == null)
					continue;

				disCurrCoordsStart = distanceSqr(currentIntersectCoords,
						startPoint.getCoords());
				if (disCurrCoordsStart < disMinCoordsStart) {
					minIntersectCoords = currentIntersectCoords;
					disMinCoordsStart = disCurrCoordsStart;
				}
			}
		}
		
		if (minIntersectCoords == null) {
			rootPoint.setUndefined();
			Log.debug("no intersection");
		} else {
			rootPoint.setCoords(minIntersectCoords, false);
			Log.debug("closet intersection found");
		}

		rootPoint.update();
	}

	private Coords calcIntersectionPoint(GeoFunction fn, GeoSegment seg) {
		Coords temp;
		double x;
		// check for vertical line a*x + c = 0: intersection at x=-c/a
		if (Kernel.isZero(seg.y)) {
			x = -seg.z / seg.x;
		}
		// standard case
		else {
			// get difference f - line
			Function.difference(fn.getFunction(startPoint.inhomX), seg,
					diffFunction);
			x = calcRoot(diffFunction, startPoint.inhomX);
		}

		if (Double.isNaN(x)) {
			return null;
		}

		double y = fn.evaluate(x);
		temp = new Coords(x, y, 1.0);

		// check if the intersection point really is on the line
		// this is important for segments and rays
		if (!seg.isOnPath(temp, Kernel.MIN_PRECISION)) {
			return null;
		}

		return temp;
	}

	public GeoPoint getIntersectionPoint() {
		return rootPoint;
	}

	@Override
	public Commands getClassName() {
		return Commands.Intersect;
	}

	@Override
	public int getRelatedModeID() {
		return EuclidianConstants.MODE_INTERSECT;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[3];
		input[0] = this.func;
		input[1] = (GeoElement) this.poly;
		input[2] = this.startPoint;

		super.setOutputLength(1);
		super.setOutput(0, rootPoint);
		setDependencies();
	}

	@Override
	final public String toString(StringTemplate tpl) {
		// Michael Borcherds 2008-03-31
		// simplified to allow better translation
		return getLoc().getPlain("IntersectionPointOfABWithInitialValueC",
				input[0].getLabel(tpl), input[1].getLabel(tpl),
				startPoint.getLabel(tpl));

	}

	public GeoPoly getPoly() {
		return this.poly;
	}

	public boolean isPolyClosed() {
		return this.polyClosed;
	}

	public GeoFunction getFunction() {
		return this.func;
	}

	private double distanceSqr(Coords A, Coords B) {
		double vx = A.getX() - B.getX();
		double vy = A.getY() - B.getY();
		return vx * vx + vy * vy;
	}

}
