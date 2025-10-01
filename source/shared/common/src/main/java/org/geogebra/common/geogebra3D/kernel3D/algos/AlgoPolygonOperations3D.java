/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.geogebra3D.kernel3D.algos;

import java.util.ArrayList;
import java.util.TreeMap;

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoLine3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPoint3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPolygon3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoSegment3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.AlgoPolygonOperation.PolyOperation;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPoly;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoSegmentND;
import org.geogebra.common.kernel.kernelND.HasSegments;
import org.geogebra.common.kernel.matrix.CoordMatrix;
import org.geogebra.common.kernel.matrix.CoordMatrixUtil;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.util.clipper.Clipper;
import org.geogebra.common.util.clipper.Clipper.ClipType;
import org.geogebra.common.util.clipper.Clipper.PolyFillType;
import org.geogebra.common.util.clipper.Clipper.PolyType;
import org.geogebra.common.util.clipper.DefaultClipper;
import org.geogebra.common.util.clipper.Path;
import org.geogebra.common.util.clipper.Paths;
import org.geogebra.common.util.clipper.Point.DoublePoint;

/**
 * 
 * Input: Two polygons
 * 
 * Output: Polygon that is the result of an intersection, union or difference
 * operation on the input polygons.
 * 
 * @author thilina
 */

public abstract class AlgoPolygonOperations3D extends AlgoElement3D {

	/** first input polygon */
	protected GeoPolygon inPoly0;
	/** second input polygon */
	protected GeoPolygon inPoly1;

	/** output polygons */
	protected OutputHandler<GeoPolygon3D> outputPolygons;
	/** output points */
	protected OutputHandler<GeoPoint3D> outputPoints;
	/** output segments */
	protected OutputHandler<GeoSegment3D> outputSegments;

	// clipper library compatible types
	private Path subject;
	private Path clip;
	private Paths solution;

	// auxiliary objects for getting corresponding coords values in 2D coords
	// w.r.t input poly 0
	private Coords tmpCoord;
	private CoordMatrix matrix;
	private Coords retCoords;

	// 3d line for polygon in two different but not parallel case
	private GeoLine3D planeIntsctGeoLine3D;
	private TreeMap<Double, Coords> newCoords;
	private GeoPoint3D tmpPoint;
	private ArrayList<Integer> intersectSegmentIndex;

	/** polygon operation type */
	protected PolyOperation operationType;
	/** output labels */
	protected String[] labels;
	private boolean silent;

	private Coords o1;
	private Coords d1;

	/**
	 * constructor for retrieving saved 3d polygon intersections
	 * 
	 * @param cons
	 *            construction
	 * @param labels
	 *            labels
	 * @param inPoly0
	 *            input polygon 1
	 * @param inPoly1
	 *            input polygon 2
	 * @param operationType
	 *            the enum type of operation INTERSECTION, UNION, DIFFERENCE,XOR
	 */
	public AlgoPolygonOperations3D(Construction cons, String[] labels,
			GeoPoly inPoly0, GeoPoly inPoly1, PolyOperation operationType) {

		super(cons);

		this.operationType = operationType;
		this.inPoly0 = (GeoPolygon) inPoly0;
		this.inPoly1 = (GeoPolygon) inPoly1;

		tmpCoord = new Coords(4);

		subject = new Path(this.inPoly0.getPointsLength());
		clip = new Path(this.inPoly1.getPointsLength());
		solution = new Paths();

		planeIntsctGeoLine3D = new GeoLine3D(getConstruction());
		newCoords = new TreeMap<>(
				Kernel.doubleComparator(Kernel.STANDARD_PRECISION));
		tmpPoint = new GeoPoint3D(getConstruction());
		intersectSegmentIndex = new ArrayList<>();

		createOutput();

		this.labels = labels;
		silent = cons.isSuppressLabelsActive();

	}

	/**
	 * @param outputSizes
	 *            size of polygon, point, segment output
	 */
	protected void initialize(int[] outputSizes) {
		setInputOutput();

		compute(false);
		// set labels
		if (labels == null) {
			outputPolygons.setLabels(null);
			outputPoints.setLabels(null);
			outputSegments.setLabels(null);
		} else {
			int labelsLength = labels.length;
			if (labelsLength > 1) {
				if (outputSizes != null) {
					// set output sizes
					outputPolygons.adjustOutputSize(outputSizes[0], false);
					outputPoints.adjustOutputSize(outputSizes[1], false);
					outputSegments.adjustOutputSize(outputSizes[2], false);

					// set labels
					int i1 = 0;
					int i2 = 0;

					while (i1 < outputSizes[0]) {
						outputPolygons.getElement(i1).setLabel(labels[i2]);
						i1++;
						i2++;
					}

					i1 = 0;
					while (i1 < outputSizes[1]) {
						outputPoints.getElement(i1).setLabel(labels[i2]);
						i1++;
						i2++;
					}

					i1 = 0;
					while (i1 < outputSizes[2]) {
						outputSegments.getElement(i1).setLabel(labels[i2]);
						i1++;
						i2++;
					}

				} else {
					// set default
					outputPolygons.setLabels(null);
					outputSegments.setLabels(null);
					outputPoints.setLabels(null);
				}
			} else if (labels.length == 1 && labels[0] != null
					&& !labels[0].equals("")) {
				outputPolygons.setIndexLabels(labels[0]);
			}
		}

		update();

	}

	@Override
	protected void getCmdOutputXML(StringBuilder sb, StringTemplate tpl) {

		sb.append("\t<outputSizes val=\"");
		sb.append(outputPolygons.size());
		sb.append(",");
		sb.append(outputPoints.size());
		sb.append(",");
		sb.append(outputSegments.size());
		sb.append("\"");
		sb.append("/>\n");

		// common method
		super.getCmdOutputXML(sb, tpl);

	}

	/**
	 * create outputHandlers for output polygons, points, and segments and
	 * initiate them
	 */
	private final void createOutput() {

		outputPolygons = new OutputHandler<>(
				new ElementFactory<GeoPolygon3D>() {
					@Override
					public GeoPolygon3D newElement() {
						GeoPolygon3D p = new GeoPolygon3D(cons, true);
						p.setParentAlgorithm(AlgoPolygonOperations3D.this);
						if (outputPolygons.size() > 0) {
							p.setAllVisualProperties(
									outputPolygons.getElement(0), false);
						}
						p.setViewFlags(inPoly0.getViewSet());
						p.setNotFixedPointsLength(true);
						return p;
					}
				});

		outputPolygons.adjustOutputSize(1, false);

		outputPoints = new OutputHandler<>(
				new ElementFactory<GeoPoint3D>() {
					@Override
					public GeoPoint3D newElement() {
						GeoPoint3D newPoint = new GeoPoint3D(cons);
						newPoint.setCoords(0, 0, 1);
						newPoint.setParentAlgorithm(
								AlgoPolygonOperations3D.this);
						newPoint.setAuxiliaryObject(true);
						newPoint.setViewFlags(inPoly0.getViewSet());

						return newPoint;
					}
				});

		outputPoints.adjustOutputSize(1, false);

		outputSegments = new OutputHandler<>(
				new ElementFactory<GeoSegment3D>() {
					@Override
					public GeoSegment3D newElement() {
						GeoSegment3D segment = (GeoSegment3D) outputPolygons
								.getElement(0)
								.createSegment(cons, outputPoints.getElement(0),
										outputPoints.getElement(0), true);
						segment.setAuxiliaryObject(true);
						segment.setViewFlags(inPoly0.getViewSet());
						return segment;
					}
				});

	}

	@Override
	protected void setInputOutput() {

		input = new GeoElement[2];
		input[0] = inPoly0;
		input[1] = inPoly1;

		// set dependencies
		for (int i = 0; i < input.length; i++) {
			input[i].addAlgorithm(this);
		}
		cons.addToAlgorithmList(this);

		setDependencies();
	}

	@Override
	public final void compute() {
		compute(!silent);
	}

	/**
	 * @param updateLabels
	 *            whether to set labels (should be false ion silent mode)
	 */
	protected void compute(boolean updateLabels) {

		// one or more input polygons are undefined, terminate immediately
		if (!this.inPoly0.isDefined() || !this.inPoly1.isDefined()) {
			// Log.debug("one of the input polygons is not defined.");
			setOutputUndefined();
			return;
		}

		Coords[] res = CoordMatrixUtil.intersectPlanes(
				this.inPoly0.getCoordSys().getMatrixOrthonormal(),
				this.inPoly1.getCoordSys().getMatrixOrthonormal());
		// Log.debug("res[0]: " + res[0].getX() + " , " + res[0].getY() + " , "
		// + res[0].getZ() + " , " + res[0].getW());
		// Log.debug("res[1]: " + res[1].getX() + " , " + res[1].getY() + " , "
		// + res[1].getZ() + " , " + res[1].getW());

		// both the input polygons are on the same plane
		if (res[1].isZero() && !res[0].isZero()) {
			// Log.debug("the two input polygons are on the same plane.");

			// Log.debug(a + "X + " + b + "Y + " + c + "Z + " + d + " = 0");

			// add subject polygon
			subject.clear();
			for (int i = 0; i < this.inPoly0.getPointsLength(); i++) {
				DoublePoint point = new DoublePoint(
						this.inPoly0.getPoint(i).getX(),
						this.inPoly0.getPoint(i).getY());
				// Log.debug("SubjectPoly-> x:"
				// + this.inPoly0.getPoint(i).getX() + " y:"
				// + this.inPoly0.getPoint(i).getY());
				subject.add(point);
			}

			// add clip polygon
			matrix = this.inPoly0.getCoordSys().getMatrixOrthonormal();
			clip.clear();
			for (int i = 0; i < this.inPoly1.getPointsLength(); i++) {
				this.inPoly1.getPoint3D(i).projectPlaneInPlaneCoords(matrix,
						tmpCoord);
				DoublePoint point = new DoublePoint(tmpCoord.getX(),
						tmpCoord.getY());
				// Log.debug("ClipPoly-> x:" + tmpCoord.getX() + " y:"
				// + tmpCoord.getY());
				clip.add(point);
			}

			// initializing clipper
			DefaultClipper clipper = new DefaultClipper(
					Clipper.STRICTLY_SIMPLE);
			clipper.addPath(clip, PolyType.CLIP, true);
			clipper.addPath(subject, PolyType.SUBJECT, true);

			boolean solutionValid = false;
			solution.clear();

			// calculating output polygons
			switch (operationType) {
			default:
			case INTERSECTION:
				solutionValid = clipper.execute(ClipType.INTERSECTION, solution,
						PolyFillType.EVEN_ODD, PolyFillType.EVEN_ODD);
				break;
			case UNION:
				solutionValid = clipper.execute(ClipType.UNION, solution,
						PolyFillType.EVEN_ODD, PolyFillType.EVEN_ODD);
				break;
			case DIFFERENCE:
				solutionValid = clipper.execute(ClipType.DIFFERENCE, solution,
						PolyFillType.EVEN_ODD, PolyFillType.EVEN_ODD);
				break;
			case XOR:
				solutionValid = clipper.execute(ClipType.XOR, solution,
						PolyFillType.EVEN_ODD, PolyFillType.EVEN_ODD);
				break;

			}

			// assign output calculated using clipper library appropriately
			if (!solutionValid) { // if there is no output
				setOutputUndefined();
			} else {
				// adjust output sizes
				outputPolygons.adjustOutputSize(solution.size(), false);
				int pointCount = 0;
				for (Path path : solution) {
					pointCount += path.size();
				}
				outputPoints.adjustOutputSize(pointCount, false);
				outputSegments.adjustOutputSize(pointCount, false);

				// assigning coords to output points
				pointCount = 0;
				for (Path path : solution) {
					for (int i = 0; i < path.size(); i++) {
						GeoPoint3D point = outputPoints.getElement(pointCount);
						DoublePoint calcPoint = path.get(i);
						retCoords = this.inPoly0.getCoordSys()
								.getPoint(calcPoint.getX(), calcPoint.getY());

						point.setCoords(retCoords.getX(), retCoords.getY(),
								retCoords.getZ(), 1);
						// Log.debug("x: " + retCoords.getX() + " y: "
						// + retCoords.getY()
						// + " z: " + z);
						pointCount++;
					}
				}
				if (updateLabels) {
					outputPoints.updateLabels();
				}

				GeoPoint3D[] points = new GeoPoint3D[pointCount];
				points = outputPoints.getOutput(points);

				int pointIndex = 0;
				int polygonIndex = 0;
				int segmentIndex = 0;
				for (Path path : solution) {

					GeoPolygon3D polygon = outputPolygons
							.getElement(polygonIndex);
					polygonIndex++;

					GeoPoint3D[] polyPoints = new GeoPoint3D[path.size()];
					GeoSegment3D[] polySegments = new GeoSegment3D[path.size()];

					for (int i = 0; i < path.size(); i++) {
						GeoSegment3D segment = outputSegments
								.getElement(segmentIndex);

						segment.setPoints(points[pointIndex + i],
								points[pointIndex + (i + 1) % path.size()]);
						segment.setCoord(points[pointIndex + i],
								points[pointIndex + (i + 1) % path.size()]);
						segment.update();
						polyPoints[i] = points[pointIndex + i];

						polySegments[i] = segment;
						segmentIndex++;
					}

					pointIndex += path.size();

					// assign points to poly without creating segments
					polygon.setPoints(polyPoints, null, false);

					polygon.setSegments(polySegments);
					polygon.calcArea();
				}

				// Log.debug("ending computing intersection between two polygons
				// in the same plane. ");
			}
		}

		// the two input polygons are in two different parallel planes
		else if (res[1].isZero() && res[0].isZero()) {
			// Log.debug("two different parallel planes");
			setOutputUndefined();
		}
		// the two input polygons are in two different planes (not parallel)
		else {
			// Log.debug("two different planes (not parallel)");

			// calculating output polygons
			if (this.operationType == PolyOperation.INTERSECTION) {
				// sets the intersection line of planes
				this.planeIntsctGeoLine3D.setCoord(res[0], res[1].normalize());

				this.newCoords.clear();

				// finding intersection points between plane intersection line
				// and the polygons
				intersectionsCoords(this.inPoly0, this.planeIntsctGeoLine3D,
						newCoords);
				intersectionsCoords(this.inPoly1, this.planeIntsctGeoLine3D,
						newCoords);

				// affect new computed points
				Coords[] coords = new Coords[1];
				coords = newCoords.values().toArray(coords);
				this.intersectSegmentIndex.clear();
				tmpCoord.setW(1); // ensure homogeneous coords
				for (int i = 0; i < coords.length - 1; i++) {
					// check whether the mid point of two consecutive intersect
					// points is on both polygons
					tmpCoord.setAdd3(coords[i], coords[i + 1]).mulInside3(0.5);
					tmpPoint.setCoords(tmpCoord);
					if (this.inPoly0.isInRegion(tmpPoint)
							&& this.inPoly1.isInRegion(tmpPoint)) {
						this.intersectSegmentIndex.add(i);
					}
				}

				int noOfSegs = this.intersectSegmentIndex.size();
				// Log.debug("no Of Intersection Segments: " + noOfSegs);

				outputPolygons.adjustOutputSize(noOfSegs, false);
				outputPoints.adjustOutputSize(noOfSegs * 2, false);
				outputSegments.adjustOutputSize(noOfSegs, false);

				int pointIndex = 0;
				for (int i = 0; i < noOfSegs; i++) {
					outputPoints.getElement(pointIndex)
							.setCoords(coords[intersectSegmentIndex.get(i)]);
					outputPoints.getElement(pointIndex + 1).setCoords(
							coords[intersectSegmentIndex.get(i) + 1]);

					GeoPoint3D[] polyPoints = new GeoPoint3D[2];
					polyPoints[0] = outputPoints.getElement(pointIndex);
					polyPoints[1] = outputPoints.getElement(pointIndex + 1);

					pointIndex = pointIndex + 2;

					GeoSegment3D[] polySegments = new GeoSegment3D[1];
					outputSegments.getElement(i).setPoints(polyPoints[0],
							polyPoints[1]);
					polySegments[0] = outputSegments.getElement(i);
					polySegments[0].update();

					// assign points to poly without creating segments
					outputPolygons.getElement(i).setPoints(polyPoints, null,
							false);
					outputPolygons.getElement(i).setSegments(polySegments);
					outputPolygons.getElement(i).calcArea();
					// Log.debug("poly " + i + " defined : "
					// + outputPolygons.getElement(i).isDefined());
				}
			}
			// for Difference, Xor, Union no output if the input polygons are in
			// two different planes
			else {
				setOutputUndefined();
			}
		}
		if (updateLabels) {
			outputSegments.updateLabels();
			outputPolygons.updateLabels();
		}
	}

	private void setOutputUndefined() {
		outputPolygons.adjustOutputSize(1, false);
		outputPoints.adjustOutputSize(1, false);
		outputSegments.adjustOutputSize(1, false);
		outputSegments.updateLabels();
		outputPolygons.updateLabels();
		outputPolygons.setUndefined();
	}

	// ///////////////////////////////////////////////////////////////////////
	/*
	 * auxiliary methods for finding intersection segments when the two input
	 * polygons are on two different not-parallel planes
	 */
	private void setIntersectionLine(GeoLineND line) {
		o1 = line.getPointInD(3, 0).getInhomCoordsInSameDimension();
		d1 = line.getPointInD(3, 1).getInhomCoordsInSameDimension().sub(o1);
	}

	/**
	 * calc intersection coords
	 * 
	 */
	private void intersectionsCoords(GeoPolygon poly, GeoLineND line,
			TreeMap<Double, Coords> newCoords1) {
		// check if the line is contained by the polygon plane
		switch (AlgoIntersectCS1D2D.getConfigLinePlane(line, poly)) {
		case GENERAL: // intersect line/interior of polygon
			intersectionsCoordsGeneral(poly, line, newCoords1);
			break;
		case CONTAINED: // intersect line/segments
			intersectionsCoordsContained(poly, line, newCoords1);
			break;
		case PARALLEL: // no intersection
			break;
		}
	}

	/**
	 * calc intersection coords when line is contained in polygon's plane
	 */
	private void intersectionsCoordsContained(HasSegments p, GeoLineND line,
			TreeMap<Double, Coords> newCoords1) {

		// line origin and direction
		setIntersectionLine(line);

		for (int i = 0; i < p.getSegments().length; i++) {
			GeoSegmentND seg = p.getSegments()[i];

			Coords o2 = seg.getPointInD(3, 0).getInhomCoordsInSameDimension();
			Coords d2 = seg.getPointInD(3, 1).getInhomCoordsInSameDimension()
					.sub(o2);

			Coords[] project = CoordMatrixUtil.nearestPointsFromTwoLines(o1, d1,
					o2, d2);

			// check if projection is intersection point
			if (project != null && project[0].equalsForKernel(project[1],
					Kernel.STANDARD_PRECISION)) {

				double t1 = project[2].get(1); // parameter on line
				double t2 = project[2].get(2); // parameter on segment

				if (line.respectLimitedPath(t1) && seg.respectLimitedPath(t2)) {
					newCoords1.put(t1, project[0]);
				}
			}
		}
	}

	/**
	 * calc intersection coords when line is not contained in polygon's plane
	 * 
	 */
	private static void intersectionsCoordsGeneral(GeoPolygon p,
			GeoLineND line, TreeMap<Double, Coords> newCoords) {

		Coords globalCoords = new Coords(4);
		Coords inPlaneCoords = new Coords(4);

		Coords singlePoint = AlgoIntersectCS1D2D.getIntersectLinePlane(line, p,
				globalCoords, inPlaneCoords);

		// check if projection is intersection point
		if (singlePoint != null) {
			newCoords.put(0d, singlePoint);
		}

	}
}
