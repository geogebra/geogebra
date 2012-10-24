/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra.common.kernel.geos;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.MyPoint;
import geogebra.common.kernel.Path;
import geogebra.common.kernel.PathMover;
import geogebra.common.kernel.PathMoverLocus;
import geogebra.common.kernel.PathParameter;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.algos.AlgoLocusSliderInterface;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.plugin.GeoClass;

import java.util.ArrayList;

/**
 * Locus of points
 * @author Markus
 */
public class GeoLocus extends GeoElement implements Path, Traceable {

	/** maximal number of runs through the path when computing */
	public static final int MAX_PATH_RUNS = 10;

	private boolean defined;
	private boolean fillable;

	// coords of points on locus
	private ArrayList<MyPoint> myPointList;

	/**
	 * Creates new locus
	 * @param c construction
	 */
	public GeoLocus(Construction c) {
		super(c);

		// moved from GeoElement's constructor
		// must be called from the subclass, see
		// http://benpryor.com/blog/2008/01/02/dont-call-subclass-methods-from-a-superclass-constructor/
		setConstructionDefaults(); // init visual settings

		myPointList = new ArrayList<MyPoint>(500);
		setFillable(true);
	}

	@Override
	public GeoElement copy() {
		GeoLocus ret = new GeoLocus(cons);
		ret.set(this);
		return ret;
	}

	@Override
	public void set(GeoElement geo) {
		GeoLocus locus = (GeoLocus) geo;
		defined = locus.defined;

		myPointList.clear();
		myPointList.addAll(locus.myPointList);
	}

	/**
	 * Number of valid points in x and y arrays.
	 * 
	 * @return number of valid points in x and y arrays.
	 */
	final public int getPointLength() {
		return myPointList.size();
	}

	/**
	 * Clears list of points defining this locus
	 */
	public void clearPoints() {
		myPointList.clear();
	}

	/**
	 * Adds a new point (x,y) to the end of the point list of this locus.
	 * 
	 * @param x x-coord
	 * @param y y-coord
	 * @param lineTo
	 *            true to draw a line to (x,y); false to only move to (x,y)
	 */
	public void insertPoint(double x, double y, boolean lineTo) {
		myPointList.add(new MyPoint(x, y, lineTo));
	}

	/**
	 * @return list of points that define this locus
	 */
	public ArrayList<MyPoint> getPoints() {
		return myPointList;
	}

	@Override
	public String toString(StringTemplate tpl) {
		sbToString.setLength(0);
		sbToString.append(label);
		sbToString.append(" = ");
		sbToString.append(getCommandDescription(tpl));
		return sbToString.toString();
	}

	private StringBuilder sbToString = new StringBuilder(80);

	@Override
	public boolean showInAlgebraView() {
		return true;
	}

	@Override
	public GeoClass getGeoClassType() {
		return GeoClass.LOCUS;
	}

	/**
	 * returns all class-specific xml tags for getXML
	 */
	@Override
	protected void getXMLtags(StringBuilder sb) {
		super.getXMLtags(sb);
		getLineStyleXML(sb);
	}

	@Override
	public boolean isDefined() {
		return defined;
	}

	/**
	 * @param flag true to make this locus defined
	 */
	public void setDefined(boolean flag) {
		defined = flag;
	}

	@Override
	public void setUndefined() {
		defined = false;
	}

	@Override
	public String toValueString(StringTemplate tpl) {
		return "";
	}

	@Override
	protected boolean showInEuclidianView() {
		return isDefined();
	}

	@Override
	public boolean isGeoLocus() {
		return true;
	}

	public PathMover createPathMover() {
		return new PathMoverLocus(this);
	}

	public double getMaxParameter() {
		return myPointList.size() - 1;
	}

	public double getMinParameter() {
		return 0;
	}

	public boolean isClosedPath() {
		if (myPointList.size() > 0) {
			MyPoint first = myPointList.get(0);
			MyPoint last = myPointList.get(myPointList.size() - 1);
			return first.isEqual(last.x, last.y);
		}
		return false;
	}

	public boolean isOnPath(GeoPointND PI, double eps) {

		GeoPoint P = (GeoPoint) PI;

		MyPoint closestPoint = getClosestPoint(P);
		if (closestPoint != null) {
			return Math.sqrt(closestPointDist) < eps;
		}
		return false;
	}

	private MyPoint getClosestPoint(GeoPoint P) {
		GeoLine l = getClosestLine(P);

		boolean temp = cons.isSuppressLabelsActive();
		cons.setSuppressLabelCreation(true);
		GeoSegment closestSegment = new GeoSegment(cons);
		cons.setSuppressLabelCreation(temp);

		if (closestPointIndex == -1)
			return null;

		MyPoint locusPoint = myPointList.get(closestPointIndex);
		MyPoint locusPoint2 = myPointList.get(closestPointIndex + 1);

		closestSegment.setCoords(l.x, l.y, l.z);

		cons.setSuppressLabelCreation(true);
		closestSegment.setStartPoint(locusPoint.getGeoPoint(cons));
		closestSegment.setEndPoint(locusPoint2.getGeoPoint(cons));
		cons.setSuppressLabelCreation(temp);

		closestPointParameter = closestSegment.getParameter(P.x / P.z, P.y
				/ P.z);

		if (closestPointParameter < 0)
			closestPointParameter = 0;
		else if (closestPointParameter > 1)
			closestPointParameter = 1;

		return new MyPoint((1 - closestPointParameter) * locusPoint.x
				+ closestPointParameter * locusPoint2.x,
				(1 - closestPointParameter) * locusPoint.y
						+ closestPointParameter * locusPoint2.y, false);
	}

	/**
	 * Returns the point of this locus that is closest to GeoPoint P.
	 */
	private GeoLine getClosestLine(GeoPoint P) {
		int size = myPointList.size();
		if (size == 0)
			return null;

		// can't use P.inhomX, P.inhomY in path updating yet, so compute them
		// double px = P.x/P.z;
		// double py = P.y/P.z;

		P.updateCoords();

		// search for closest point on path
		// MyPoint closestPoint = null;
		closestPointDist = Double.MAX_VALUE;
		closestPointIndex = -1;

		// make a segment and points to reuse
		GeoSegment segment = new GeoSegment(cons);
		GeoPoint p1 = new GeoPoint(cons);
		GeoPoint p2 = new GeoPoint(cons);
		segment.setStartPoint(p1);
		segment.setEndPoint(p2);

		double closestx = 0, closesty = 0, closestz = 0;

		// search for closest point
		for (int i = 0; i < size - 1; i++) {
			MyPoint locusPoint = myPointList.get(i);
			MyPoint locusPoint2 = myPointList.get(i + 1);

			// not a line, just a move (eg Voronoi Diagram)
			if (!locusPoint2.lineTo)
				continue;

			double x1 = locusPoint.x;
			double x2 = locusPoint2.x;
			double y1 = locusPoint.y;
			double y2 = locusPoint2.y;

			// line thro' 2 points
			segment.setCoords(y1 - y2, x2 - x1, x1 * y2 - y1 * x2);
			p1.setCoords(x1, y1, 1.0);
			p2.setCoords(x2, y2, 1.0);

			double dist = segment.distance(P);
			if (dist < closestPointDist) {
				closestPointDist = dist;
				closestPointIndex = i;
				closestx = segment.x;
				closesty = segment.y;
				closestz = segment.z;
			}
		}

		segment.setCoords(closestx, closesty, closestz);

		return segment;
	}

	private double closestPointDist;
	private int closestPointIndex;
	private double closestPointParameter;

	private boolean trace;

	public void pathChanged(GeoPointND PI) {

		// if kernel doesn't use path/region parameters, do as if point changed
		// its coords
		if (!getKernel().usePathAndRegionParameters(this)) {
			pointChanged(PI);
			return;
		}

		// find closest point on changed path to P
		if (getParentAlgorithm() instanceof AlgoLocusSliderInterface) {
			pointChanged(PI);
			return;
		}

		// new method
		// keep point on same segment, the same proportion along it
		// better for loci with very few segments eg from ShortestDistance[ ]
		GeoPoint P = (GeoPoint) PI;
		PathParameter pp = P.getPathParameter();

		int n = (int) Math.floor(pp.t);

		double t = pp.t - n; // between 0 and 1

		// check n and n+1 are in a sensible range
		// might occur if locus has changed no of segments/points
		if (n >= myPointList.size() || n < 0) {
			n = (n < 0) ? 0 : myPointList.size() - 1;
		}

		MyPoint locusPoint = myPointList.get(n);
		MyPoint locusPoint2 = myPointList.get((n + 1) % myPointList.size());

		P.x = (1 - t) * locusPoint.x + t * locusPoint2.x;
		P.y = (1 - t) * locusPoint.y + t * locusPoint2.y;
		P.z = 1.0;

	}

	public void pointChanged(GeoPointND PI) {

		GeoPoint P = (GeoPoint) PI;

		// this updates closestPointParameter and closestPointIndex
		MyPoint closestPoint = getClosestPoint(P);

		PathParameter pp = P.getPathParameter();
		// Application.debug(pp.t);
		if (closestPoint != null) {
			P.x = closestPoint.x;// (1 - closestPointParameter) * locusPoint.x +
									// closestPointParameter * locusPoint2.x;
			P.y = closestPoint.y;// (1 - closestPointParameter) * locusPoint.y +
									// closestPointParameter * locusPoint2.y;
			P.z = 1.0;
			pp.t = closestPointIndex + closestPointParameter;
		}
	}

	@Override
	public boolean isPath() {
		return true;
	}

	// Michael Borcherds 2008-04-30
	@Override
	final public boolean isEqual(GeoElement geo) {
		// return false if it's a different type, otherwise use equals() method
		return false;
		// TODO?
		// if (geo.isGeoLocus()) return xxx else return false;
	}

	@Override
	public boolean isVector3DValue() {
		return false;
	}

	/**
	 * Returns whether the value (e.g. equation) should be shown as part of the
	 * label description
	 */
	@Override
	final public boolean isLabelValueShowable() {
		return false;
	}

	/**
	 * @param al list of points that definr this locus
	 */
	public void setPoints(ArrayList<MyPoint> al) {
		myPointList = al;

	}

	

	@Override
	final public boolean isAuxiliaryObjectByDefault() {
		return true;
	}

	public void setTrace(boolean trace) {
		this.trace = trace;
	}

	public boolean getTrace() {
		return trace;
	}

	@Override
	public boolean isTraceable() {
		return true;
	}

	@Override
	public boolean isFillable() {
		return fillable;
	}

	@Override
	public boolean isInverseFillable() {
		return fillable;
	}

	public void setFillable(boolean fill) {
		fillable = fill;
	}
}
