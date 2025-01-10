/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * AlgoIntersectPolyLineConic.java
 *
 * Created on 29. May 2015, 14:49
 */

package org.geogebra.common.kernel.algos;

import java.util.ArrayList;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoPoly;
import org.geogebra.common.kernel.geos.GeoSegment;
import org.geogebra.common.kernel.geos.GeoVec3D;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.matrix.Coords;

/**
 * AlgoIntersect class for finding intersection points of both polyLine-conic
 * and polygon(br)-conic combinations. In the constructor boolean isPolyClosed
 * is used to define whether the GeoPoly is a polygon or a polyLine.
 * 
 * @author thilina
 *
 */
public class AlgoIntersectPolyLineConic extends AlgoIntersect {

	/** Input poly **/
	protected GeoPoly poly;
	/** Input conic **/
	protected GeoConic conic;

	/** internal dummy segment */
	protected GeoSegment dummySegment;

	/** internal dummy points */
	protected GeoPoint dummyPoint1;
	protected GeoPoint dummyPoint2;
	protected GeoPoint[] dummyOutputPoints;

	protected boolean isPolyClosed;

	/** outputHandler for variable no of output points */
	protected OutputHandler<GeoElement> intersectingPoints;

	/** computed list of intersecting coordinates */
	protected ArrayList<Coords> intersectingCoords;
	protected boolean hasLabels;

	/**
	 * 
	 * @param cons
	 *            construction
	 * @param labels
	 *            labels for the output
	 * @param poly
	 *            input poly (can be polyLine or polygon as boundary)
	 * @param conic
	 *            input conic
	 * @param isPolyClosed
	 *            whether other input is polygon
	 */
	public AlgoIntersectPolyLineConic(Construction cons, String[] labels,
			GeoConic conic, GeoPoly poly, boolean isPolyClosed) {

		this(cons, conic, poly, isPolyClosed);

		if (!cons.isSuppressLabelsActive()) {
			intersectingPoints.setLabelsMulti(labels);
			hasLabels = true;
		}

		update();
	}

	/**
	 * common constructor
	 * 
	 * @param cons
	 *            construction
	 * @param poly
	 *            input poly (can be polyLine or polygon as boundary)
	 * @param conic
	 *            input conic
	 * @param isPolyClosed
	 *            whether the geopoly is a polygon or not(i.e a polyLine)
	 */
	public AlgoIntersectPolyLineConic(Construction cons, GeoConic conic,
			GeoPoly poly, boolean isPolyClosed) {
		super(cons);
		this.conic = conic;
		this.poly = poly;
		this.isPolyClosed = isPolyClosed;

		initElements();

		setInputOutput(); // for AlgoElement

		initForNearToRelationship();

		compute();

		addIncidence(); // must be after compute()
	}

	@Override
	public void initForNearToRelationship() {
		// TODO
	}

	@Override
	public GeoPoint[] getIntersectionPoints() {
		// TODO
		return null;
	}

	@Override
	protected GeoPoint[] getLastDefinedIntersectionPoints() {
		// TODO
		return null;
	}

	@Override
	public boolean isNearToAlgorithm() {
		return true;
	}

	private void addIncidence() {
		for (int i = 0; i < intersectingPoints.size(); i++) {
			((GeoPoint) intersectingPoints.getElement(i))
					.addIncidence((GeoElement) this.poly, false);
			((GeoPoint) intersectingPoints.getElement(i))
					.addIncidence(this.conic, false);
		}
	}

	private void initElements() {

		this.dummySegment = new GeoSegment(getConstruction());
		this.dummyPoint1 = new GeoPoint(getConstruction());
		this.dummyPoint2 = new GeoPoint(getConstruction());

		this.dummyOutputPoints = new GeoPoint[2];
		this.dummyOutputPoints[0] = new GeoPoint(getConstruction());
		this.dummyOutputPoints[1] = new GeoPoint(getConstruction());

		intersectingPoints = createOutputPoints();

		intersectingCoords = new ArrayList<>();

	}

	@Override
	protected void noUndefinedPointsInAlgebraView() {
		GeoElement[] temp = new GeoElement[2];
		GeoElement[] points = intersectingPoints.getOutput(temp);
		for (GeoElement el : points) {
			((GeoPoint) el).showUndefinedInAlgebraView(false);
		}
	}

	@Override
	protected void setInputOutput() {
		input = new GeoElement[2];
		input[0] = (GeoElement) this.poly;
		input[1] = this.conic;

		setDependencies(); // done by AlgoElement
	}

	@Override
	public void compute() {

		// calculate intersecting coordinates
		this.intersectingCoords(this.conic, this.poly, this.intersectingCoords);

		// update and/or create points
		this.intersectingPoints
				.adjustOutputSize(this.intersectingCoords.size() > 0
						? this.intersectingCoords.size() : 1);

		// affect new computed points
		int index = 0;
		for (; index < this.intersectingCoords.size(); index++) {
			Coords coords = this.intersectingCoords.get(index);
			GeoPointND point = (GeoPointND) this.intersectingPoints
					.getElement(index);
			point.setCoords(coords, false);
			point.updateCoords();
		}

		// other points are undefined
		for (; index < this.intersectingPoints.size(); index++) {
			this.intersectingPoints.getElement(index).setUndefined();
		}

		if (hasLabels) {
			intersectingPoints.updateLabels();
		}

	}

	/**
	 * Does the actual calculation of the intersecting points.
	 * 
	 * @param c
	 *            GeoConic - considered as a full conic in the calculation
	 * @param p
	 *            GeoPolyLine - segments of the polyLine considered as full
	 *            lines in the calculation
	 * @param coords
	 *            ArrayList of Coords to store the calculated intersecting
	 *            coords
	 */
	protected void intersectingCoords(GeoConic c, GeoPoly p,
			ArrayList<Coords> coords) {
		GeoPointND[] polyPoints = p.getPoints();
		int noOfSegments = isPolyClosed ? polyPoints.length
				: polyPoints.length - 1;

		coords.clear();

		for (int i = 0; i < noOfSegments; i++) {

			this.dummyPoint1.setCoords(p.getPoint(i));
			this.dummyPoint2.setCoords(p.getPoint((i + 1) % polyPoints.length));

			this.dummySegment.setStartPoint(dummyPoint1);
			this.dummySegment.setEndPoint(dummyPoint2);

			GeoVec3D.lineThroughPoints(this.dummyPoint1, this.dummyPoint2,
					this.dummySegment);

			/*
			 * Here both conic and conic parts are considered as full conics in
			 * the calculation. Segments in the polyline are considered as full
			 * lines in the calculation. Thus explicit checking needed to check
			 * whether the intersecting points are actually lying on both conic
			 * and polyline.
			 */
			AlgoIntersectLineConic.intersectLineConic(dummySegment, c,
					dummyOutputPoints, Kernel.MIN_PRECISION);

			/*
			 * checks the validity and the incidence on both conic and polyline
			 * of intersection point
			 */
			if (dummyOutputPoints[0].isDefined()) {
				if (dummySegment.isOnPath(dummyOutputPoints[0],
						Kernel.MIN_PRECISION)
						&& c.isOnPath(dummyOutputPoints[0],
								Kernel.MIN_PRECISION)) {
					coords.add(dummyOutputPoints[0].getCoords());
				}
			}

			if (dummyOutputPoints[1].isDefined()) {
				if (dummySegment.isOnPath(dummyOutputPoints[1],
						Kernel.MIN_PRECISION)
						&& c.isOnPath(dummyOutputPoints[1],
								Kernel.MIN_PRECISION)) {
					coords.add(dummyOutputPoints[1].getCoords());
				}
			}

		}
	}

	@Override
	public Commands getClassName() {
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
				GeoPoint p = new GeoPoint(getConstruction());
				p.setCoords(0, 0, 1);
				p.setParentAlgorithm(AlgoIntersectPolyLineConic.this);
				return p;
			}
		});
	}
}
