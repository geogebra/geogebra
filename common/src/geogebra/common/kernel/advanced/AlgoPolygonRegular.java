/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra.common.kernel.advanced;

import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.algos.AlgoJoinPointsSegment;
import geogebra.common.kernel.algos.Algos;
import geogebra.common.kernel.algos.AlgoElement.OutputHandler;
import geogebra.common.kernel.algos.AlgoElement.elementFactory;
import geogebra.common.kernel.arithmetic.MyDouble;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoBoolean;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.geos.GeoPolygon;
import geogebra.common.kernel.geos.GeoSegment;
import geogebra.common.kernel.kernelND.GeoPointND;

import java.util.ArrayList;

/**
 * Creates a regular Polygon for two points and the number of vertices.
 * 
 * @author Markus Hohenwarter
 */
public class AlgoPolygonRegular extends AlgoElement {
	/** first input point */
	final GeoPoint A; // input
	/** second input point */
	final GeoPoint B;
	private NumberValue num; // input

	private int numOld = 2;
	/** output handler */
	OutputHandler<GeoPolygon> outputPolygon;
	private OutputHandler<GeoPoint> outputPoints;
	private OutputHandler<GeoSegment> outputSegments;

	private GeoPoint centerPoint;
	private MyDouble rotAngle;

	private boolean labelPointsAndSegments;
	/** whether new segment labels should be visible */
	boolean showNewSegmentsLabels;
	/** whether new point labels should be visible */
	boolean showNewPointsLabels;
	private boolean labelsNeedIniting;

	/**
	 * Creates a new regular polygon algorithm
	 * 
	 * @param c
	 *            construction
	 * @param labels
	 *            labels[0] for polygon, then labels for segments and then for
	 *            points
	 * @param A1
	 *            first input point
	 * @param B1
	 *            second input point
	 * @param num
	 *            number of vertices
	 */
	public AlgoPolygonRegular(Construction c, String[] labels, GeoPoint A1,
			GeoPoint B1, NumberValue num) {
		super(c);

		labelsNeedIniting = true;

		this.A = A1;
		this.B = B1;
		this.num = num;

		// labels given by user or loaded from file
		int labelsLength = labels == null ? 0 : labels.length;

		// set labels for segments only when points have labels
		labelPointsAndSegments = A.isLabelSet() || B.isLabelSet()
				|| labelsLength > 1;
		showNewSegmentsLabels = false;
		showNewPointsLabels = false;

		// temp center point of regular polygon
		centerPoint = new GeoPoint(c);
		rotAngle = new MyDouble(kernel);

		outputPolygon = new OutputHandler<GeoPolygon>(
				new elementFactory<GeoPolygon>() {
					public GeoPolygon newElement() {
						GeoPolygon p = new GeoPolygon(cons);
						p.setParentAlgorithm(AlgoPolygonRegular.this);
						return p;
					}
				});

		outputSegments = new OutputHandler<GeoSegment>(
				new elementFactory<GeoSegment>() {
					public GeoSegment newElement() {
						GeoSegment segment = (GeoSegment) outputPolygon
								.getElement(0).createSegment(A, B, true);
						segment.setAuxiliaryObject(true);
						segment.setLabelVisible(showNewSegmentsLabels);
						return segment;
					}
				});

		if (!labelPointsAndSegments)
			outputSegments.removeFromHandler(); // no segments has output

		outputPoints = new OutputHandler<GeoPoint>(
				new elementFactory<GeoPoint>() {
					public GeoPoint newElement() {
						GeoPoint newPoint = new GeoPoint(cons);
						newPoint.setCoords(0, 0, 1);
						newPoint.setParentAlgorithm(AlgoPolygonRegular.this);
						newPoint.setAuxiliaryObject(true);
						newPoint.setPointSize(A.getPointSize());
						newPoint.setEuclidianVisible(A.isEuclidianVisible()
								|| B.isEuclidianVisible());
						newPoint.setAuxiliaryObject(true);
						newPoint.setLabelVisible(showNewPointsLabels);
						GeoBoolean conditionToShow = A.getShowObjectCondition();
						if (conditionToShow == null)
							conditionToShow = B.getShowObjectCondition();
						if (conditionToShow != null) {
							try {
								((GeoElement) newPoint)
										.setShowObjectCondition(conditionToShow);
							} catch (Exception e) {
								// circular exception -- do nothing
							}
						}
						return newPoint;
					}
				});

		if (!labelPointsAndSegments)
			outputPoints.removeFromHandler(); // no segments has output

		// create polygon
		outputPolygon.adjustOutputSize(1);

		// create 2 first segments
		outputSegments.augmentOutputSize(2, false);
		outputSegments.getElement(0).setAuxiliaryObject(false);
		outputSegments.getElement(1).modifyInputPoints(B, A);

		// for AlgoElement
		setInputOutput();

		GeoPolygon poly = getPoly();

		// compute poly
		if (labelsLength > 1) {
			compute((labelsLength + 1) / 2);// create maybe undefined outputs
			poly.setLabel(labels[0]);
			int d = 1;
			for (int i = 0; i < outputSegments.size(); i++)
				outputSegments.getElement(i).setLabel(labels[d + i]);
			d += outputSegments.size();
			for (int i = 0; i < outputPoints.size(); i++)
				outputPoints.getElement(i).setLabel(labels[d + i]);
		} else if (labelsLength == 1) {
			poly.setLabel(labels[0]);
		} else {
			poly.setLabel(null);
		}

		labelsNeedIniting = false;

		update();

		/*
		 * if (labelPointsAndSegments) { //poly.initLabels(labels); } else if
		 * (labelsLength == 1) { poly.setLabel(labels[0]); } else {
		 * poly.setLabel(null); }
		 * 
		 * 
		 * labelsNeedIniting = false;
		 */
		// make sure that we set all point and segment labels when needed
		// updateSegmentsAndPointsLabels(points.length);
	}

	@Override
	public Algos getClassName() {
		return Algos.AlgoPolygonRegular;
	}

	@Override
	public int getRelatedModeID() {
		return EuclidianConstants.MODE_REGULAR_POLYGON;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[3];
		input[0] = A;
		input[1] = B;
		input[2] = num.toGeoElement();
		// set dependencies
		for (int i = 0; i < input.length; i++) {
			input[i].addAlgorithm(this);
		}
		cons.addToAlgorithmList(this);

		// setOutput(); done in compute

		// parent of output
		getPoly().setParentAlgorithm(this);
		cons.addToAlgorithmList(this);
	}

	/**
	 * 
	 * @return resulting polygon
	 */
	GeoPolygon getPoly() {
		return outputPolygon.getElement(0);
	}

	/**
	 * Computes points of regular polygon
	 */
	@Override
	public final void compute() {

		// check points and number
		double nd = num.getDouble();

		if (Double.isNaN(nd))
			nd = 2;

		compute((int) Math.round(nd));
	}

	/**
	 * @param nd
	 *            number of vertices
	 */
	private final void compute(int nd) {

		GeoPolygon poly = getPoly();

		// get integer number of vertices n
		int n = Math.max(2, nd);

		// if number of points changed, we need to update the
		// points array and the output array
		updateOutput(n);

		// check if regular polygon is defined
		if (n < 3 || !A.isDefined() || !B.isDefined()) {
			poly.setUndefined();
			numOld = n;
			return;
		}

		// some temp values
		double mx = (A.inhomX + B.inhomX) / 2; // midpoint of AB
		double my = (A.inhomY + B.inhomY) / 2;
		double alpha = Kernel.PI_2 / n; // center angle ACB
		double beta = (Math.PI - alpha) / 2; // base angle CBA = BAC

		// normal vector of AB
		double nx = A.inhomY - B.inhomY;
		double ny = B.inhomX - A.inhomX;

		// center point of regular polygon
		double tanBetaHalf = Math.tan(beta) / 2;
		centerPoint
				.setCoords(mx + tanBetaHalf * nx, my + tanBetaHalf * ny, 1.0);

		// now we have the center point of the polygon and
		// the center angle alpha between two neighbouring points
		// let's create the points by rotating A around the center point
		for (int k = 0; k < n - 2; k++) {
			// rotate point around center point
			outputPoints.getElement(k).set((GeoElement) A);
			rotAngle.set((k + 2) * alpha);
			outputPoints.getElement(k).rotate(rotAngle, centerPoint);
		}

		GeoPoint[] points = new GeoPoint[n];
		points[0] = A;
		points[1] = B;
		for (int i = 2; i < n; i++)
			points[i] = outputPoints.getElement(i - 2);

		// update new segments
		for (int i = numOld - 1; i < n; i++) {
			// Application.debug(i+": "+points[i]+" , "+points[(i+1)%n]);
			outputSegments.getElement(i).modifyInputPoints(points[i],
					points[(i + 1) % n]);
		}

		// update polygon
		poly.setPoints(points, null, false); // don't create segments
		poly.setSegments(outputSegments.getOutput(new GeoSegment[n]));

		// compute area of poly
		poly.calcArea();

		// update region coordinate system
		poly.updateRegionCS(A, B, outputPoints.getElement(0));

		numOld = n;
	}

	/**
	 * Ensures that the pointList holds n points.
	 * 
	 * @param n
	 */
	private void updateOutput(int n) {

		int nOld = outputPoints.size() + 2;

		// Application.debug("nOld="+nOld+", n="+n);

		if (nOld == n)
			return;

		// update points and segments
		if (n > nOld) {
			showNewPointsLabels = labelPointsAndSegments
					&& (A.isEuclidianVisible() && A.isLabelVisible() || B
							.isEuclidianVisible() && B.isLabelVisible());
			outputPoints.augmentOutputSize(n - nOld, false);
			if (labelPointsAndSegments && !labelsNeedIniting)
				outputPoints.updateLabels();

			showNewSegmentsLabels = false;
			for (int i = 0; i < outputSegments.size(); i++)
				showNewSegmentsLabels = showNewSegmentsLabels
						|| outputSegments.getElement(i).isLabelVisible();
			outputSegments.augmentOutputSize(n - nOld, false);
			if (labelPointsAndSegments && !labelsNeedIniting)
				outputSegments.updateLabels();
		} else {
			for (int i = n; i < nOld; i++) {
				outputPoints.getElement(i - 2).setUndefined();
				// outputSegments.getElement(i).setUndefined();
			}
			// update last segment
			if (n > 2)
				outputSegments.getElement(n - 1).modifyInputPoints(
						outputPoints.getElement(n - 3), A);
			else
				outputSegments.getElement(n - 1).modifyInputPoints(B, A);
		}

	}

	private void removePoint(GeoPoint oldPoint) {

		// remove dependent algorithms (e.g. segments) from update sets of
		// objects further up (e.g. polygon) the tree
		ArrayList<AlgoElement> algoList = oldPoint.getAlgorithmList();
		for (int k = 0; k < algoList.size(); k++) {
			AlgoElement algo = algoList.get(k);
			for (int j = 0; j < input.length; j++)
				input[j].removeFromUpdateSets(algo);
		}

		// remove old point
		oldPoint.setParentAlgorithm(null);

		// remove dependent segment algorithm that are part of this polygon
		// to make sure we don't remove the polygon as well
		GeoPolygon poly = getPoly();
		for (int k = 0; k < algoList.size(); k++) {
			AlgoElement algo = algoList.get(k);
			// make sure we don't remove the polygon as well
			if (algo instanceof AlgoJoinPointsSegment
					&& ((AlgoJoinPointsSegment) algo).getPoly() == poly) {
				continue;
			}
			algo.remove();

		}

		algoList.clear();
		// remove point
		oldPoint.doRemove();

	}

	/**
	 * Calls doRemove() for all output objects of this algorithm except for
	 * keepGeo.
	 */
	@Override
	public void removeOutputExcept(GeoElement keepGeo) {
		for (int i = 0; i < super.getOutputLength(); i++) {
			GeoElement geo = super.getOutput(i);
			if (geo != keepGeo) {
				if (geo.isGeoPoint()) {
					removePoint((GeoPoint) geo);
				} else {
					geo.doRemove();
				}
			}
		}
	}

	/**
	 * 
	 * @return all points of the algo, comprising undefined ones
	 */
	public GeoPointND[] getPoints() {
		GeoPointND[] ret = new GeoPointND[2 + outputPoints.size()];
		ret[0] = A;
		ret[1] = B;
		for (int i = 0; i < outputPoints.size(); i++)
			ret[2 + i] = outputPoints.getElement(i);
		return ret;
	}

	@Override
	public boolean isLocusEquable() {
		return true;
	}

}
