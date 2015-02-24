/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra.common.kernel.algos;

import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.LocusEquation;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.Matrix.CoordSys;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.geos.GeoPolygon;
import geogebra.common.kernel.kernelND.GeoDirectionND;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.kernel.kernelND.GeoSegmentND;
import geogebra.common.main.App;
import geogebra.common.plugin.GeoClass;

/**
 * Creates a Polygon from a given list of points or point array.
 * 
 * @author Markus Hohenwarter
 * @version
 */
public class AlgoPolygon extends AlgoElement implements PolygonAlgo {

	protected GeoPointND[] points; // input
	protected GeoList geoList; // alternative input
	protected GeoPolygon poly; // output

	/** /2D coord sys used for 3D */
	protected CoordSys cs2D;

	/** polyhedron (when segment is part of), used for 3D */
	protected GeoElement polyhedron;

	/** normal direction, used for 3D */
	protected GeoDirectionND direction;

	public AlgoPolygon(Construction cons, String[] labels, GeoList geoList) {
		this(cons, labels, null, geoList);
	}

	public AlgoPolygon(Construction cons, String[] labels, GeoPointND[] points) {
		this(cons, labels, points, null);
	}

	protected AlgoPolygon(Construction cons, String[] labels,
			GeoPointND[] points, GeoList geoList) {
		this(cons, labels, points, geoList, null, true, null, null);
	}

	/**
	 * @param cons
	 *            the construction
	 * @param points
	 *            vertices of the polygon
	 * @param geoList
	 *            list of vertices of the polygon (alternative to points)
	 * @param cs2D
	 *            for 3D stuff : GeoCoordSys2D
	 * @param createSegments
	 *            says if the polygon has to creates its edges (3D only)
	 * @param polyhedron
	 *            polyhedron (when segment is part of), used for 3D
	 * @param direction
	 *            normal direction, used for 3D
	 */
	protected AlgoPolygon(Construction cons, GeoPointND[] points,
			GeoList geoList, CoordSys cs2D, boolean createSegments,
			GeoElement polyhedron, GeoDirectionND direction) {
		super(cons);
		this.points = points;
		this.geoList = geoList;
		this.cs2D = cs2D;
		this.polyhedron = polyhedron;
		this.direction = direction;

		// make sure that this helper algorithm is updated right after its
		// parent polygon
		if (polyhedron != null) {
			setUpdateAfterAlgo(polyhedron.getParentAlgorithm());
		}

		// poly = new GeoPolygon(cons, points);
		createPolygon(createSegments);

		// compute polygon points
		compute();

		setInputOutput(); // for AlgoElement

	}

	/**
	 * @param cons
	 *            the construction
	 * @param labels
	 *            names of the polygon and the segments
	 * @param points
	 *            vertices of the polygon
	 * @param geoList
	 *            list of vertices of the polygon (alternative to points)
	 * @param cs2D
	 *            for 3D stuff : GeoCoordSys2D
	 * @param createSegments
	 *            says if the polygon has to creates its edges (3D only)
	 * @param polyhedron
	 *            polyhedron (when segment is part of), used for 3D
	 * @param direction
	 *            normal direction, used for 3D
	 */
	protected AlgoPolygon(Construction cons, String[] labels,
			GeoPointND[] points, GeoList geoList, CoordSys cs2D,
			boolean createSegments, GeoElement polyhedron,
			GeoDirectionND direction) {

		this(cons, points, geoList, cs2D, createSegments, polyhedron, direction);

		// G.Sturr 2010-3-14: Do not label segments or points for polygons
		// formed by a geolist.
		// (current code cannot handle sequences of variable length)

		// poly.initLabels(labels);
		if (geoList == null) {
			poly.initLabels(labels);
		} else {
			if (labels != null)
				poly.setLabel(labels[0]);
		}

		// END G.Sturr
	}

	/**
	 * create the polygon
	 * 
	 * @param createSegments
	 *            says if the polygon has to creates its edges (3D only)
	 */
	protected void createPolygon(boolean createSegments) {
		poly = new GeoPolygon(this.cons, this.points);
	}

	@Override
	public Commands getClassName() {
		return Commands.Polygon;
	}

	@Override
	public int getRelatedModeID() {
		return EuclidianConstants.MODE_POLYGON;
	}

	/**
	 * Update point array of polygon using the given array list
	 * 
	 * @param pointList
	 */
	private void updatePointArray(GeoList pointList) {
		// check if we have a point list
		if (!pointList.getElementType().equals(GeoClass.POINT)) {
			poly.setUndefined();
			return;
		}

		// remember old number of points
		int oldPointsLength = points == null ? 0 : points.length;

		// create new points array
		int size = pointList.size();
		points = new GeoPoint[size];
		for (int i = 0; i < size; i++) {
			points[i] = (GeoPoint) pointList.get(i);
		}
		poly.setPointsAndSegments(points);

		if (oldPointsLength != points.length)
			setOutput();
	}

	protected GeoElement[] createEfficientInput() {

		GeoElement[] efficientInput;

		if (geoList != null) {
			// list as input
			efficientInput = new GeoElement[1];
			efficientInput[0] = geoList;
		} else {
			// points as input
			efficientInput = new GeoElement[points.length];
			for (int i = 0; i < points.length; i++)
				efficientInput[i] = (GeoElement) points[i];
		}

		return efficientInput;
	}

	/**
	 * modify input points
	 * 
	 * @param newPoints
	 *            new input points
	 */
	public void modifyInputPoints(GeoPointND[] newPoints) {
		for (int i = 0; i < input.length; i++)
			input[i].removeAlgorithm(this);

		points = newPoints;
		poly.setPoints(points, null, false); // don't recreate segments
		setInputOutput();

		compute();

	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {

		// efficient inputs are points or list
		GeoElement[] efficientInput = createEfficientInput();

		// add polyhedron to inputs
		if (polyhedron == null) {
			input = efficientInput;
		} else {
			input = new GeoElement[efficientInput.length + 1];
			for (int i = 0; i < efficientInput.length; i++)
				input[i] = efficientInput[i];
			input[efficientInput.length] = polyhedron;
		}

		setEfficientDependencies(input, efficientInput);

		// set output after, to avoid segments to have this to parent algo
		setOutput();

		// parent of output
		poly.setParentAlgorithm(this);
		cons.addToAlgorithmList(this);
	}

	private void setOutput() {
		GeoSegmentND[] segments = poly.getSegments();
		int size = 1;

		if (segments != null && polyhedron == null && geoList == null) {// if
																		// from
																		// polyhedron,
																		// segments
																		// are
																		// output
																		// of
																		// algo
																		// for
																		// the
																		// polyhedron
			size += segments.length;
		}

		super.setOutputLength(size);
		super.setOutput(0, poly);

		if (polyhedron == null && geoList == null) {// if from polyhedron,
													// segments are output of
													// algo for the polyhedron
			for (int i = 0; i < size - 1; i++) {
				super.setOutput(i + 1, (GeoElement) segments[i]);
			}
		}
	}

	@Override
	protected void removeOutput() {
		if (polyhedron == null) // if from polyhedron, no need to remove
								// dependent objects
			super.removeOutput();
	}

	@Override
	public void update() {
		// compute output from input
		compute();
		super.getOutput(0).update();
	}

	public GeoPolygon getPoly() {
		return poly;
	}

	public GeoPointND[] getPoints() {
		return points;
	}

	public GeoElement getPolyhedron() {
		return polyhedron;
	}

	@Override
	public void remove() {
		if (removed)
			return;
		super.remove();
		// if polygon is part of a polyhedron, remove it
		if (polyhedron != null)
			polyhedron.remove();
	}

	@Override
	public void compute() {

		// AbstractApplication.printStacktrace("");

		if (geoList != null) {
			updatePointArray(geoList);
		}

		calcArea();

		// update region coord sys
		poly.updateRegionCS();

	}

	protected StringBuilder sb;

	/**
	 * Returns the area of a polygon given by points P, negative if clockwise
	 * changed name from calcArea as we need the sign when calculating the
	 * centroid Michael Borcherds 2008-01-26 TODO Does not work if polygon is
	 * self-entrant
	 * 
	 * @param points2
	 *            array of points
	 * @return directed area
	 */
	final static public double calcAreaWithSign(GeoPointND[] points2) {
		if (points2 == null || points2.length < 2)
			return Double.NaN;

		int i = 0;
		for (; i < points2.length; i++) {
			if (points2[i].isInfinite()) {
				return Double.NaN;
			}
		}

		// area = 1/2 | det(P[i], P[i+1]) |
		int last = points2.length - 1;
		double sum = 0;
		for (i = 0; i < last; i++) {
			sum += GeoPoint.det((GeoPoint) points2[i],
					(GeoPoint) points2[i + 1]);

		}
		sum += GeoPoint.det((GeoPoint) points2[last], (GeoPoint) points2[0]);
		return sum / 2.0; // positive (anticlockwise points) or negative
		// (clockwise)
	}

	/**
	 * Calculates the centroid of this polygon and writes the result to the
	 * given point. algorithm at
	 * http://local.wasp.uwa.edu.au/~pbourke/geometry/polyarea/ TODO Does not
	 * work if polygon is self-entrant
	 * 
	 * @param centroid
	 *            point to store result
	 */
	public static void calcCentroid(double[] centroid, double signedArea,
			GeoPointND[] points2) {
		if (Double.isNaN(signedArea) || Double.isInfinite(signedArea)) { // || points2 == null || points2.length == 0) {
			centroid[0] = Double.NaN;
			return;
		}

		double xsum = 0;
		double ysum = 0;
		double factor = 0;
		for (int i = 0; i < points2.length; i++) {
			factor = pointsClosedX(i, points2) * pointsClosedY(i + 1, points2)
					- pointsClosedX(i + 1, points2) * pointsClosedY(i, points2);
			xsum += (pointsClosedX(i, points2) + pointsClosedX(i + 1, points2))
					* factor;
			ysum += (pointsClosedY(i, points2) + pointsClosedY(i + 1, points2))
					* factor;
		}
		centroid[0] = xsum;
		centroid[1] = ysum;
		centroid[2] = 6.0 * signedArea;
		// getArea
		// includes
		// the +/-
		// to
		// compensate
		// for
		// clockwise/anticlockwise
	}

	private static double pointsClosedX(int i, GeoPointND[] points2) {
		// pretend array has last element==first element
		if (i == points2.length) {
			// return points[0].inhomX; else return points[i].inhomX;
			return ((GeoPoint) points2[0]).inhomX;
		}
		return ((GeoPoint) points2[i]).inhomX;
	}

	private static double pointsClosedY(int i, GeoPointND[] points2) {
		// pretend array has last element==first element
		if (i == points2.length) {
			// return points[0].inhomY; else return
			// points[i].inhomY;
			return ((GeoPoint) points2[0]).inhomY;
		}
		return ((GeoPoint) points2[i]).inhomY;
	}

	protected void createStringBuilder(StringTemplate tpl) {

		if (sb == null) {
			sb = new StringBuilder();
		} else {
			sb.setLength(0);
		}

		String label;

		// G.Sturr: get label from geoList (2010-3-15)
		if (geoList != null) {
			label = geoList.getLabel(tpl);
		} else {
			// use point labels

			int last = points.length - 1;
			for (int i = 0; i < last; i++) {
				sb.append(points[i].getLabel(tpl));
				sb.append(", ");
			}
			sb.append(points[last].getLabel(tpl));

			label = sb.toString();
			sb.setLength(0);
		}

		sb.append(getLoc().getPlain("PolygonA", label));

	}

	@Override
	final public String toString(StringTemplate tpl) {
		createStringBuilder(tpl);
		return sb.toString();
	}

	@Override
	public boolean isLocusEquable() {
		return true;
	}

	public EquationElementInterface buildEquationElementForGeo(GeoElement geo,
			EquationScopeInterface scope) {
		return LocusEquation.eqnPolygon(geo, this, scope);
	}

	public void calcArea() {
		GeoPointND[] points2d = poly.getPoints();

		// compute area
		poly.setArea(calcAreaWithSign(points2d));
	}
	
	private double[] tmp3;

	public void calcCentroid(GeoPoint p) {
		GeoPointND[] points2d = poly.getPoints();

		if (tmp3 == null){
			tmp3 = new double[3];
		}
		calcCentroid(tmp3, poly.getAreaWithSign(), points2d);
		if (Double.isNaN(tmp3[0])){
			p.setUndefined();
		}else{
			p.setCoords(tmp3[0], tmp3[1], tmp3[2]);
		}

	}

}
