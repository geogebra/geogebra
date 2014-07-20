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
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.algos.AlgoLocusSliderInterface;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.plugin.GeoClass;

import java.util.ArrayList;

/**
 * Locus of points
 * @author Markus
 */
public abstract class GeoLocusND<T extends MyPoint> extends GeoElement implements Path, Traceable {

	/** maximal number of runs through the path when computing */
	public static final int MAX_PATH_RUNS = 10;

	private boolean defined;
	private boolean fillable;

	// coords of points on locus
	protected ArrayList<T> myPointList;

	/**
	 * Creates new locus
	 * @param c construction
	 */
	public GeoLocusND(Construction c) {
		super(c);

		// moved from GeoElement's constructor
		// must be called from the subclass, see
		// http://benpryor.com/blog/2008/01/02/dont-call-subclass-methods-from-a-superclass-constructor/
		setConstructionDefaults(); // init visual settings

		myPointList = new ArrayList<T>(500);
		setFillable(true);
	}

	@Override
	public GeoElement copy() {
		GeoLocusND<T> ret = newGeoLocus();
		ret.set(this);
		return ret;
	}
	
	
	/**
	 * 
	 * @return new GeoLocus of same type
	 */
	abstract protected GeoLocusND<T> newGeoLocus();

	@Override
	public void set(GeoElement geo) {
		GeoLocusND<T> locus = (GeoLocusND<T>) geo;
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
	 * @return list of points that define this locus
	 */
	public ArrayList<T> getPoints() {
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
			return first.isEqual(last);
		}
		return false;
	}

	public boolean isOnPath(GeoPointND P, double eps) {

		MyPoint closestPoint = getClosestPoint(P.getCoordsInD(2).getInhomCoordsInSameDimension());
		if (closestPoint != null) {
			return Math.sqrt(closestPointDist) < eps;
		}
		return false;
	}

	private MyPoint getClosestPoint(Coords P) {
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

		closestPointParameter = closestSegment.getParameter(P.getX(), P.getY());

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
	private GeoLine getClosestLine(Coords P) {
		int size = myPointList.size();
		if (size == 0)
			return null;

		
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

			double dist = segment.distance(P.getX(), P.getY());
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

	public void pathChanged(GeoPointND P) {

		// if kernel doesn't use path/region parameters, do as if point changed
		// its coords
		if (!getKernel().usePathAndRegionParameters(P)) {
			pointChanged(P);
			return;
		}

		// find closest point on changed path to P
		if (getParentAlgorithm() instanceof AlgoLocusSliderInterface) {
			pointChanged(P);
			return;
		}

		// new method
		// keep point on same segment, the same proportion along it
		// better for loci with very few segments eg from ShortestDistance[ ]
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

		P.set(1-t, t, locusPoint, locusPoint2);

	}
	
	
	public void pointChanged(GeoPointND P) {
		
		Coords coords = P.getCoordsInD(2).getInhomCoordsInSameDimension();
		 
		// this updates closestPointParameter and closestPointIndex
		MyPoint closestPoint = getClosestPoint(coords);

		PathParameter pp = P.getPathParameter();
		// Application.debug(pp.t);
		if (closestPoint != null) {
			coords.setX(closestPoint.x);// (1 - closestPointParameter) * locusPoint.x +
									// closestPointParameter * locusPoint2.x;
			coords.setY(closestPoint.y);// (1 - closestPointParameter) * locusPoint.y +
									// closestPointParameter * locusPoint2.y;
			coords.setZ(1.0);
			pp.t = closestPointIndex + closestPointParameter;
		}
		
		 P.setCoords2D(coords.getX(), coords.getY(), coords.getZ());
		 P.updateCoordsFrom2D(false);
		 P.updateCoords();
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
	public void setPoints(ArrayList<T> al) {
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

	/**
	 * @param fill whether this can be filled
	 */
	public void setFillable(boolean fill) {
		fillable = fill;
	}
	
	


	final public PathMover createPathMover() {
		return new PathMoverLocus<T>(this);
	}
	
	@Override
	public boolean hasDrawable3D() {
		return true;
	}
}
