/* 
 * GeoGebra - Dynamic Mathematics for Everyone
 * http://www.geogebra.org
 * This file is part of GeoGebra.
 * This program is free software; you can redistribute it and/or modify it 
 * under the terms of the GNU General Public License as published by 
 * the Free Software Foundation.
 */

/*
 * AlgoIntersectLines.java
 *
 * Created on 11. March 2015, 19:14
 */

package org.geogebra.common.kernel.algos;

import java.util.ArrayList;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoPoly;
import org.geogebra.common.kernel.geos.GeoSegment;
import org.geogebra.common.kernel.geos.GeoVec3D;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.util.DoubleUtil;

/**
 * Algorithm for intersection of two PolyLines
 * 
 * @author thilina
 * 
 */
public class AlgoIntersectPolyLines extends AlgoElement {

	protected GeoPoly polyA;
	protected GeoPoly polyB;
	protected final boolean polyAClosed;
	protected final boolean polyBClosed;

	protected ArrayList<Coords> intersectingCoords; // intersections

	protected OutputHandler<GeoElement> outputPoints; // output

	// two dummy GeoSegments to be used at intersecting points calculation
	protected GeoSegment[] dummySegment = new GeoSegment[2];

	// one dummy GeoPOint to be used at intersecting point calculation
	protected GeoPoint[] dummyPoint = new GeoPoint[5];

	public boolean isPolyAClosed() {
		return polyAClosed;
	}

	public boolean isPolyBClosed() {
		return polyBClosed;
	}

	/**
	 * Common Constructor
	 * 
	 * @param construction
	 *            construction
	 * @param labels
	 *            output labels
	 * @param polyA
	 *            first polyline / polygon
	 * @param polyB
	 *            second polyline / polygon
	 * @param polyAClosed
	 *            whether A is polygon
	 * @param polyBClosed
	 *            whether B is polygon
	 */
	public AlgoIntersectPolyLines(Construction construction, String[] labels,
			GeoPoly polyA, GeoPoly polyB, boolean polyAClosed,
			boolean polyBClosed) {

		super(construction);

		this.polyA = polyA;
		this.polyAClosed = polyAClosed;
		this.polyB = polyB;
		this.polyBClosed = polyBClosed;

		this.outputPoints = this.createOutputPoints();

		this.intersectingCoords = new ArrayList<>();

		this.dummySegment[0] = new GeoSegment(getConstruction());
		this.dummySegment[1] = new GeoSegment(getConstruction());
		for (int i = 0; i < this.dummyPoint.length; i++) {
			this.dummyPoint[i] = new GeoPoint(getConstruction());
		}

		this.dummySegment[0].setStartPoint(this.dummyPoint[0]);
		this.dummySegment[0].setEndPoint(this.dummyPoint[1]);

		this.dummySegment[1].setStartPoint(this.dummyPoint[2]);
		this.dummySegment[1].setEndPoint(this.dummyPoint[3]);

		compute();

		setInputOutput();

		outputPoints.setLabelsMulti(labels);

		update();
	}

	@Override
	protected void setInputOutput() {
		input = new GeoElement[2];
		input[0] = (GeoElement) this.polyA;
		input[1] = (GeoElement) this.polyB;

		setDependencies();
	}

	@Override
	public void compute() {

		// clears the point map
		this.intersectingCoords.clear();

		// calculates intersection points
		intersectionCoords(this.polyA, this.polyB, this.intersectingCoords);

		// update and/or create points
		this.outputPoints.adjustOutputSize(this.intersectingCoords.size() > 0
				? this.intersectingCoords.size() : 1);

		// affect new computed points
		int index = 0;
		for (; index < this.intersectingCoords.size(); index++) {
			Coords coords = this.intersectingCoords.get(index);
			GeoPointND point = (GeoPointND) this.outputPoints.getElement(index);
			point.setCoords(coords, false);
			point.updateCoords();
		}

		// other points are undefined
		for (; index < this.outputPoints.size(); index++) {
			this.outputPoints.getElement(index).setUndefined();
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
	 * 
	 * @return handler for output points
	 */
	protected OutputHandler<GeoElement> createOutputPoints() {

		return new OutputHandler<>(new ElementFactory<GeoElement>() {
			@Override
			public GeoPoint newElement() {
				GeoPoint p = new GeoPoint(cons);
				p.setCoords(0, 0, 1);
				p.setParentAlgorithm(AlgoIntersectPolyLines.this);
				return p;
			}
		});
	}

	/**
	 * calculates the intersecting points of two polyLines
	 * 
	 * @param poly1
	 *            input polyLine 1
	 * @param poly2
	 *            input polyLine 2
	 * @param newCoords
	 *            list to add calculated intersecting Coords
	 */
	protected void intersectionCoords(GeoPoly poly1, GeoPoly poly2,
			ArrayList<Coords> newCoords) {

		GeoPointND[] pointsA = poly1.getPoints();
		GeoPointND[] pointsB = poly2.getPoints();

		int noOfSegmentsA = isPolyAClosed() ? pointsA.length
				: pointsA.length - 1;
		int noOfSegmentsB = isPolyBClosed() ? pointsB.length
				: pointsB.length - 1;

		for (int i = 0; i < noOfSegmentsA; i++) {

			this.dummyPoint[0].setCoords(poly1.getPoint(i));
			if (i == (pointsA.length - 1)) {
				this.dummyPoint[1].setCoords(poly1.getPoint(0));
			} else {
				this.dummyPoint[1].setCoords(poly1.getPoint(i + 1));
			}
			GeoVec3D.lineThroughPoints(this.dummyPoint[0], this.dummyPoint[1],
					this.dummySegment[0]);

			for (int k = 0; k < noOfSegmentsB; k++) {

				this.dummyPoint[2].setCoords(poly2.getPoint(k));
				if (k == (pointsB.length - 1)) {
					this.dummyPoint[3].setCoords(poly2.getPoint(0));
				} else {
					this.dummyPoint[3].setCoords(poly2.getPoint(k + 1));
				}
				GeoVec3D.lineThroughPoints(this.dummyPoint[2],
						this.dummyPoint[3], this.dummySegment[1]);

				GeoVec3D.cross(this.dummySegment[0], this.dummySegment[1],
						this.dummyPoint[4]);

				// checks whether the cross product(this.dummyPoint[4]) actually
				// an intersection point
				if (this.dummyPoint[4].isDefined()) {
					if (!(DoubleUtil
							.isZero(this.dummySegment[0]
									.distance(this.dummyPoint[4]))
							&& DoubleUtil.isZero(this.dummySegment[1]
									.distance(this.dummyPoint[4])))) {
						this.dummyPoint[4].setUndefined();
					}
				}

				// adds dummyPoint Coords into intersectingCoords ArrayList
				if (this.dummyPoint[4].isDefined()) {
					newCoords.add(this.dummyPoint[4].getCoords());
				}
			}
		}
	}
}
