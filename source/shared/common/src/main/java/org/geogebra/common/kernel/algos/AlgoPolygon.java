/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.common.kernel.algos;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.kernel.kernelND.GeoDirectionND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoSegmentND;
import org.geogebra.common.kernel.matrix.CoordSys;
import org.geogebra.common.plugin.GeoClass;

/**
 * Creates a Polygon from a given list of points or point array.
 * 
 * @author Markus Hohenwarter
 */
public class AlgoPolygon extends AlgoElement implements PolygonAlgo {
	/** input */
	protected GeoPointND[] points;
	/** alternative input */
	protected GeoList geoList;
	/** output */
	protected GeoPolygon poly;

	/** /2D coord sys used for 3D */
	protected CoordSys cs2D;

	/** polyhedron (when segment is part of), used for 3D */
	protected GeoElement polyhedron;

	/** normal direction, used for 3D */
	protected GeoDirectionND direction;
	/** String builder for description */
	protected StringBuilder sb;
	private double[] tmp3;

	public AlgoPolygon(Construction cons, String[] labels, GeoList geoList) {
		this(cons, labels, null, geoList);
	}

	public AlgoPolygon(Construction cons, String[] labels, GeoPointND[] points) {
		this(cons, labels, points, null);
	}

	public AlgoPolygon(Construction cons, String[] labels, GeoPointND[] points,
			boolean createSegments) {
		this(cons, labels, points, null, null, createSegments, null, null);
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
	public AlgoPolygon(Construction cons, GeoPointND[] points,
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

		this(cons, points, geoList, cs2D, createSegments, polyhedron,
				direction);

		// Do not label segments or points for polygons
		// formed by a geolist.
		// (current code cannot handle sequences of variable length)

		// poly.initLabels(labels);
		if (geoList == null) {
			poly.initLabels(labels);
		} else {
			if (labels != null) {
				poly.setLabel(labels[0]);
			}
		}
	}

	/**
	 * create the polygon
	 * 
	 * @param createSegments
	 *            says if the polygon has to creates its edges (3D only)
	 */
	protected void createPolygon(boolean createSegments) {
		poly = new GeoPolygon(this.cons, this.points, null,
				createSegments && !cons.getApplication().isWhiteboardActive());
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
	 */
	protected void updatePointArray() {
		// check if we have a point list
		if (!geoList.getElementType().equals(GeoClass.POINT)
				&& !geoList.getElementType().equals(GeoClass.POINT3D)) {
			poly.setUndefined();
			return;
		}

		// remember old number of points
		int oldPointsLength = points == null ? 0 : points.length;

		// create new points array
		int size = geoList.size();
		points = new GeoPointND[size];
		for (int i = 0; i < size; i++) {
			points[i] = (GeoPointND) geoList.get(i);
		}
		poly.setPointsAndSegments(points);

		if (oldPointsLength != points.length) {
			setOutput();
		}
	}

	/**
	 * @return either geoList or array of vertices
	 */
	protected GeoElement[] createEfficientInput() {

		GeoElement[] efficientInput;

		if (geoList != null) {
			// list as input
			efficientInput = new GeoElement[1];
			efficientInput[0] = geoList;
		} else {
			// points as input
			efficientInput = new GeoElement[points.length];
			for (int i = 0; i < points.length; i++) {
				efficientInput[i] = (GeoElement) points[i];
			}
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
		for (int i = 0; i < input.length; i++) {
			input[i].removeAlgorithm(this);
		}

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
			for (int i = 0; i < efficientInput.length; i++) {
				input[i] = efficientInput[i];
			}
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
		// if from polyhedron, segments are output of algo for the polyhedron
		if (segments != null && polyhedron == null && geoList == null) {
			size += segments.length;
		}

		super.setOutputLength(size);
		super.setOutput(0, poly);
		// if from polyhedron, segments are output of algo for the polyhedron
		if (polyhedron == null && geoList == null) {
			for (int i = 0; i < size - 1; i++) {
				super.setOutput(i + 1, (GeoElement) segments[i]);
			}
		}
	}

	@Override
	protected void removeOutput() {
		if (polyhedron == null) {
			// dependent objects
			super.removeOutput();
		}
	}

	@Override
	public void update() {
		// compute output from input
		compute();
		super.getOutput(0).update();
	}

	/**
	 * @return resulting polygon
	 */
	public GeoPolygon getPoly() {
		return poly;
	}

	/**
	 * @return array of vertices
	 */
	public GeoPointND[] getPoints() {
		return points;
	}

	/**
	 * @return parent polyhedron
	 */
	public GeoElement getPolyhedron() {
		return polyhedron;
	}

	@Override
	public void remove() {
		if (removed) {
			return;
		}
		super.remove();
		// if polygon is part of a polyhedron, remove it
		if (polyhedron != null) {
			polyhedron.remove();
		}
	}

	@Override
	public void compute() {
		if (geoList != null) {
			updatePointArray();
		}

		calcArea();

		// update region coord sys
		poly.updateRegionCS();
	}

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
		if (points2 == null || points2.length < 2) {
			return Double.NaN;
		}

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
		if (Double.isNaN(signedArea) || Double.isInfinite(signedArea)) { // ||
																			// points2
																			// ==
																			// null
																			// ||
																			// points2.length
																			// ==
																			// 0)
																			// {
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

	/**
	 * String builder for description
	 * 
	 * @param tpl
	 *            string template
	 */
	protected void createStringBuilder(StringTemplate tpl) {

		if (sb == null) {
			sb = new StringBuilder();
		} else {
			sb.setLength(0);
		}

		String label;

		// get label from geoList
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

		sb.append(getLoc().getPlainDefault("PolygonA", "Polygon %0", label));

	}

	@Override
	final public String toString(StringTemplate tpl) {
		createStringBuilder(tpl);
		return sb.toString();
	}

	@Override
	public void calcArea() {
		GeoPointND[] points2d = poly.getPoints();

		// compute area
		poly.setArea(calcAreaWithSign(points2d));
	}

	@Override
	public void calcCentroid(GeoPoint p) {
		GeoPointND[] points2d = poly.getPoints();

		if (tmp3 == null) {
			tmp3 = new double[3];
		}
		calcCentroid(tmp3, poly.getAreaWithSign(), points2d);
		if (Double.isNaN(tmp3[0])) {
			p.setUndefined();
		} else {
			p.setCoords(tmp3[0], tmp3[1], tmp3[2]);
		}

	}

}
