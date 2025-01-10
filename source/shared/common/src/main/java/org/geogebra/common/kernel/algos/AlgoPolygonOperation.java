/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

//

package org.geogebra.common.kernel.algos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.kernel.geos.GeoSegment;
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
 * @author G.Sturr 2010-3-14, Modified by Thilina 20-05-2015 using clipper
 *         library
 *
 */
public abstract class AlgoPolygonOperation extends AlgoElement {

	/** first input polygon */
	protected GeoPolygon inPoly0;
	/** second input polygon */
	protected GeoPolygon inPoly1;

	/** output polygons */
	protected OutputHandler<GeoPolygon> outputPolygons;
	/** output points */
	protected OutputHandler<GeoPoint> outputPoints;
	/** output segments */
	protected OutputHandler<GeoSegment> outputSegments;

	private Path subject;
	private Path clip;
	private Paths solution;
	/**
	 * whether labels were suppressed during constructor; in such case never
	 * label outputs.
	 **/
	private boolean silent;
	/** operation type */
	protected PolyOperation operationType;
	/** output labels */
	protected String[] labels;

	/** operation type */
	public enum PolyOperation {
		/** intersection */
		INTERSECTION,
		/** union */
		UNION,
		/** difference */
		DIFFERENCE,
		/** xor */
		XOR
	}

	/**
	 * common constructor with outputsizes
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
	public AlgoPolygonOperation(Construction cons, String[] labels,
			GeoPolygon inPoly0, GeoPolygon inPoly1, PolyOperation operationType) {

		super(cons);

		this.operationType = operationType;
		this.inPoly0 = inPoly0;
		this.inPoly1 = inPoly1;

		this.labels = labels;

		subject = new Path(inPoly0.getPointsLength());
		clip = new Path(inPoly1.getPointsLength());
		solution = new Paths();
		silent = cons.isSuppressLabelsActive();

		createOutput();

	}

	/**
	 * @param outputSizes
	 *            output sizes from XML
	 */
	protected void initialize(int[] outputSizes) {
		setInputOutput();
		// We do compute() TWICE in the constructor (for some reason)
		// for this one we don't have the labels set yet, so do it silently
		compute(false);

		// set labels
		if (labels == null) {
			outputPolygons.setLabels(null);
			outputPoints.setLabels(null);
			outputSegments.setLabels(null);
		} else {
			int labelsLength = labels.length;

			if (labelsLength > 1) {
				// Log.debug("\nici :
				// "+outputSizes[0]+","+outputSizes[1]+","+outputSizes[2]);
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
				new ElementFactory<GeoPolygon>() {
					@Override
					public GeoPolygon newElement() {
						GeoPolygon p = new GeoPolygon(cons, true);
						p.setParentAlgorithm(AlgoPolygonOperation.this);
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
				new ElementFactory<GeoPoint>() {
					@Override
					public GeoPoint newElement() {
						GeoPoint newPoint = new GeoPoint(cons);
						newPoint.setCoords(0, 0, 1);
						newPoint.setParentAlgorithm(AlgoPolygonOperation.this);
						newPoint.setAuxiliaryObject(true);
						newPoint.setViewFlags(inPoly0.getViewSet());

						return newPoint;
					}
				});

		outputPoints.adjustOutputSize(1, false);

		outputSegments = new OutputHandler<>(
				new ElementFactory<GeoSegment>() {
					@Override
					public GeoSegment newElement() {
						GeoSegment segment = (GeoSegment) outputPolygons
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
	public void compute() {
		compute(!silent);
	}

	private void compute(boolean updateLabels) {

		// add subject polygon
		subject.clear();
		for (int i = 0; i < inPoly0.getPointsLength(); i++) {
			DoublePoint point = convert(inPoly0.getPoint(i));
			subject.add(point);
		}

		// add clip polygon
		clip.clear();
		for (int i = 0; i < inPoly1.getPointsLength(); i++) {
			DoublePoint point = convert(inPoly1.getPoint(i));
			clip.add(point);
		}

		// initializing clipper
		DefaultClipper clipper = new DefaultClipper(Clipper.STRICTLY_SIMPLE);
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

		// assign output calculated using clipper library to appropriately

		if (!solutionValid) { // if there is no output
			outputPolygons.adjustOutputSize(1, false);
			outputPoints.adjustOutputSize(1, false);
			outputSegments.adjustOutputSize(1, false);

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
					GeoPoint point = outputPoints.getElement(pointCount);
					DoublePoint calcPoint = path.get(i);
					point.setCoords(calcPoint.getX(), calcPoint.getY(), 1);
					pointCount++;
				}
			}
			if (updateLabels) {
				outputPoints.updateLabels();
			}

			GeoPoint[] points = new GeoPoint[pointCount];
			points = outputPoints.getOutput(points);

			int pointIndex = 0;
			int polygonIndex = 0;
			int segmentIndex = 0;
			for (Path path : solution) {

				GeoPolygon polygon = outputPolygons.getElement(polygonIndex);
				polygonIndex++;

				GeoPoint[] polyPoints = new GeoPoint[path.size()];
				GeoSegment[] polySegments = new GeoSegment[path.size()];

				for (int i = 0; i < path.size(); i++) {
					GeoSegment segment = outputSegments
							.getElement(segmentIndex);
					GeoPoint A = points[pointIndex + i];
					GeoPoint B = points[pointIndex + (i + 1) % path.size()];
					segment.setStartPoint(A);
					segment.setEndPoint(B);
					((AlgoJoinPointsSegmentInterface) segment
							.getParentAlgorithm()).modifyInputPoints(A, B);
					segment.update();
					segment.calcLength();
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
			if (updateLabels) {
				outputSegments.updateLabels();
				outputPolygons.updateLabels();
			}

		}
		for (int i = 0; i < outputPolygons.size(); i++) {
			outputPolygons.getElement(i).updateRegionCS();
		}

	}

	private static DoublePoint convert(GeoPoint point) {
		return new DoublePoint(point.getX() / point.getZ(),
				point.getY() / point.getZ());
	}

}