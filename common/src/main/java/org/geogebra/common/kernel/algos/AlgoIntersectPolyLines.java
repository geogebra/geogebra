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
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoPoly;
import org.geogebra.common.kernel.geos.GeoSegment;
import org.geogebra.common.kernel.geos.GeoVec3D;
import org.geogebra.common.kernel.kernelND.GeoPointND;

/**
 * Algorithm for intersection of two PolyLines
 * 
 * @author thilina
 * 
 */
public class AlgoIntersectPolyLines extends AlgoElement {

	protected GeoPoly polyA, polyB;
	protected final boolean polyAClosed, polyBClosed;

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
	 * @param labels
	 * @param polyLine1
	 * @param polyLine2
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

		this.intersectingCoords = new ArrayList<Coords>();

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

		setLabels(labels);

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
		intersectionCoords(this.polyA, this.polyB,
				this.intersectingCoords);

		// update and/or create points
		this.outputPoints
				.adjustOutputSize(this.intersectingCoords.size() > 0 ? this.intersectingCoords
						.size() : 1);

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

		return new OutputHandler<GeoElement>(new elementFactory<GeoElement>() {
			public GeoPoint newElement() {
				GeoPoint p = new GeoPoint(cons);
				p.setCoords(0, 0, 1);
				p.setParentAlgorithm(AlgoIntersectPolyLines.this);
				return p;
			}
		});
	}

	/**
	 * if only one label (e.g. "A") for more than one output, new labels will be
	 * A_1, A_2, ...
	 * 
	 * @param labels
	 */
	protected void setLabels(String[] labels) {
		// if only one label (e.g. "A") for more than one output, new labels
		// will be A_1, A_2, ...
		if (labels != null && labels.length == 1 &&
		// outputPoints.size() > 1 &&
				labels[0] != null && !labels[0].equals("")) {
			this.outputPoints.setIndexLabels(labels[0]);
		} else {

			this.outputPoints.setLabels(labels);
			this.outputPoints.setIndexLabels(this.outputPoints.getElement(0)
					.getLabel(StringTemplate.defaultTemplate));
		}
	}

	/**
	 * calculates the intersecting points of two polyLines
	 * 
	 * @param polyLineA
	 *            input polyLine 1
	 * @param polyLineB
	 *            input polyLine 2
	 * @param newCoords
	 *            TreeMap to add calculated intersecting Coords
	 */
	protected void intersectionCoords(GeoPoly polyA, GeoPoly polyB,
			ArrayList<Coords> newCoords) {

		GeoPointND[] pointsA = polyA.getPoints();
		GeoPointND[] pointsB = polyB.getPoints();

		int noOfSegmentsA = isPolyAClosed() ? pointsA.length
				: pointsA.length - 1;
		int noOfSegmentsB = isPolyBClosed() ? pointsB.length
				: pointsB.length - 1;

		for (int i = 0; i < noOfSegmentsA; i++) {

			this.dummyPoint[0].setCoords(polyA.getPoint(i));
			if (i == (pointsA.length - 1)) {
				this.dummyPoint[1].setCoords(polyA.getPoint(0));
			} else {
				this.dummyPoint[1].setCoords(polyA.getPoint(i + 1));
			}
			GeoVec3D.lineThroughPoints(this.dummyPoint[0], this.dummyPoint[1],
					this.dummySegment[0]);

			for (int k = 0; k < noOfSegmentsB; k++) {

				this.dummyPoint[2].setCoords(polyB.getPoint(k));
				if (k == (pointsB.length - 1)) {
					this.dummyPoint[3].setCoords(polyB.getPoint(0));
				} else {
					this.dummyPoint[3].setCoords(polyB.getPoint(k + 1));
				}
				GeoVec3D.lineThroughPoints(this.dummyPoint[2],
						this.dummyPoint[3], this.dummySegment[1]);

				GeoVec3D.cross(this.dummySegment[0], this.dummySegment[1],
						this.dummyPoint[4]);

				// checks whether the cross product(this.dummyPoint[4]) actually
				// an intersection point
				if (this.dummyPoint[4].isDefined()) {
					if (!(Kernel.isZero(this.dummySegment[0]
							.distance(this.dummyPoint[4])) && Kernel
							.isZero(this.dummySegment[1]
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
