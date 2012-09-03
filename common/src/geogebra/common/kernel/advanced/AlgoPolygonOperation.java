/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

//

package geogebra.common.kernel.advanced;

import geogebra.common.awt.GArea;
import geogebra.common.awt.GPathIterator;
import geogebra.common.euclidian.EuclidianViewInterfaceSlim;
import geogebra.common.euclidian.GeneralPathClipped;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.ConstructionDefaults;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.algos.AlgoJoinPointsSegment;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.geos.GeoPolygon;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.kernel.kernelND.GeoSegmentND;
import geogebra.common.util.Cloner;

import java.util.ArrayList;

/**
 * 
 * Input: Two polygons
 * 
 * Output: Polygon that is the result of an intersection, 
 * union or difference operation on the input polygons.
 *  
 * Based on AlgoRegularPolygon with polygon operations performed by 
 * the Java Area class.
 * 
 * @author G.Sturr 2010-3-14
 *
 */
public abstract class AlgoPolygonOperation extends AlgoElement {

	private GeoPolygon inPoly0; //input
	private GeoPolygon inPoly1; //input
	private GeoPolygon poly; //output	

	private GeoPoint [] points;
	private PolyOperation operationType;
	private EuclidianViewInterfaceSlim ev;

	private boolean labelPointsAndSegments;
	private boolean labelsNeedIniting;

	/** operation type */
	public enum PolyOperation  {
		/** intersection */
		INTERSECTION,
		/** union */
		UNION,
		/** difference TODO -- not working*/
		DIFFERENCE
	}

	/**
	 * @param cons construction
	 * @param labels labels for output
	 * @param inPoly0 first input polygon
	 * @param inPoly1 second input polygon
	 * @param operationType operation type (intersection, union, difference)
	 */
	public AlgoPolygonOperation(Construction cons, String[] labels,
			GeoPolygon inPoly0, GeoPolygon inPoly1, PolyOperation operationType) {

		super(cons);

		ev = cons.getApplication().getActiveEuclidianView();
		this.operationType = operationType;
		this.inPoly0 = inPoly0;
		this.inPoly1 = inPoly1;
		points = new GeoPoint[0];
		poly = new GeoPolygon(cons, points);

		labelPointsAndSegments = true;

		setInputOutput();
		compute();

		// labels given by user or loaded from file
		int labelsLength = labels == null ? 0 : labels.length;

		// poly.setLabel(labels[0]);
		labelsNeedIniting = true;

		if (labelPointsAndSegments) {
			poly.initLabels(labels);
		} else if (labelsLength == 1) {
			poly.setLabel(labels[0]);
		} else {
			poly.setLabel(null);
		}

		labelsNeedIniting = false;

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

		// setOutput(); done in compute
		// there we just set something to be sure that getOutput doesn't return null.
		setOutputLength(1);
		setOutput(0,poly);
		// parent of output
		poly.setParentAlgorithm(this);
		cons.addToAlgorithmList(this);

	}


	private void setOutput() {
		if (points == null)
			return;

		// if init points have no labels, all the points and segments
		// of the polygon don't get labels either: in this case we only
		// have the polygon itself as output object
		if (!labelPointsAndSegments) {
			super.setOutputLength(1);
	        super.setOutput(0, poly);
		}
		// otherwise: points and segments are also output objects
		else {
			// size = poly + points + segments
			GeoSegmentND[] segments = poly.getSegments();
			GeoPointND[] pts = poly.getPoints();
			int size = 1 + segments.length + pts.length;

			super.setOutputLength(size);
			int k = 0;
			super.setOutput(k, poly);

			for (int i = 0; i < segments.length; i++) {
				super.setOutput(++k, (GeoElement) segments[i]);
			}

			for (int i = 0; i < pts.length; i++) {
				super.setOutput(++k, (GeoElement) pts[i]);
			}
		}
	}

	/**
	 * @return resulting polygon
	 */
	GeoPolygon getPoly() {
		return poly;
	}

	/**
	 * Convert array of polygon GeoPoints to an Area object
	 */
	private GArea getArea(GeoPointND[] pts) {

		double [] coords = new double[2]; 
		GeneralPathClipped gp = new GeneralPathClipped(ev);

		// first point
		pts[0].getInhomCoords(coords);
		if(!pts[0].isDefined())
			return null;
		gp.moveTo(coords[0], coords[1]);   

		for (int i=1; i < pts.length; i++) {
			pts[i].getInhomCoords(coords);			
			gp.lineTo(coords[0], coords[1]);
			if(!pts[i].isDefined())
				return null;
		}
		gp.closePath();

		return geogebra.common.factories.AwtFactory.prototype.newArea(gp);	
	}

	@Override
	public final void compute() {

		ArrayList<Double> xcoord = new ArrayList<Double>();
		ArrayList<Double> ycoord = new ArrayList<Double>();
		double[] coords = new double[6];
				
		// Convert input polygons to Area objects
		GArea a1 = getArea(inPoly0.getPoints());
		GArea a2 = getArea(inPoly1.getPoints());
		GArea testArea = null;
		if(a1 != null && a2 != null){
		// test for empty intersection
			testArea = getArea(inPoly0.getPoints());
			testArea.intersect(a2);
		}
		if(testArea==null || testArea.isEmpty()) {
			poly.setUndefined();
			
		}
		// if intersection is non-empty perform operation
		else {
			switch (operationType) {
			case INTERSECTION:
				a1.intersect(a2);
				break;
			case UNION:
				a1.add(a2);
				break;
			case DIFFERENCE:
				a1.subtract(a2);
				break;
			}

			// Iterate through the path of the result 
			// and recover the polygon vertices.
			
			GPathIterator it = a1.getPathIterator(null);

			int type = it.currentSegment(coords);
			it.next();
			double[] oldCoords = Cloner.clone(coords);
			double[] initCoords = Cloner.clone(coords);
			double epsilon = 1E-10;

			while (!it.isDone()) {
				type = it.currentSegment(coords);
				if (type == GPathIterator.SEG_CLOSE) {
					break;
				}
				// Sometimes the Path iterator gives two almost identical points and 
				// we only want one of them. 
				// TODO: Why does this happen???
				if (Math.abs(oldCoords[0] - coords[0]) > epsilon
						|| Math.abs(oldCoords[1] - coords[1]) > epsilon) {
					xcoord.add(coords[0]);
					ycoord.add(coords[1]);
				}
				oldCoords = Cloner.clone(coords);

				it.next();

			}
			
			//fixes #1951: sometimes the initial coordinates are not added into xcoord, ycoord.
			if (Math.abs(oldCoords[0] - initCoords[0]) > epsilon
					|| Math.abs(oldCoords[1]- initCoords[1]) > epsilon) {
				xcoord.add(initCoords[0]);
				ycoord.add(initCoords[1]);
			}
			
		}

		
		// Update the points array to the correct size
		int n = xcoord.size();
		//System.out.println("number of points: " + n);
		int oldPointNumber = points.length;
		if (n != oldPointNumber) {
			updatePointsArray(n);
			poly.setPoints(points);
			setOutput();
		}

		// Set the points to the new polygon vertices
		for (int k = 0; k < n; k++) {
			points[k].setCoords(xcoord.get(k), ycoord.get(k), 1);
			//System.out.println("vertices: " + xcoord.get(k) + " , " + ycoord.get(k));

		}

		// Compute area of poly (this will also set our poly geo to be defined)
		poly.calcArea();


		// update new points and segments 
		if (n != oldPointNumber) {
			updateSegmentsAndPointsLabels();
		}    	    	
	}         


	private void updateSegmentsAndPointsLabels() {
		if (labelsNeedIniting)
			return;

		// set labels only when points have labels
		/*
		labelPointsAndSegments = labelPointsAndSegments || A.isLabelSet() || B.isLabelSet();


		boolean pointsSegmentsShowLabel = labelPointsAndSegments && 
				(A.isEuclidianVisible() && A.isLabelVisible() || 
				 B.isEuclidianVisible() && B.isLabelVisible());

		 */


		boolean pointsSegmentsShowLabel = (app.getLabelingStyle() == ConstructionDefaults.LABEL_VISIBLE_ALWAYS_ON)
		|| (app.getLabelingStyle() == ConstructionDefaults.LABEL_VISIBLE_USE_DEFAULTS && 
		 cons.getConstructionDefaults().getDefaultGeo(ConstructionDefaults.DEFAULT_SEGMENT).isLabelVisible());

		// set labels for points only if the original points had labels
		if (labelPointsAndSegments) {
			for (int i=0; i < points.length; i++) {            	
				if (!points[i].isLabelSet()) {
					points[i].setLabel(null); 
					points[i].setLabelVisible(pointsSegmentsShowLabel);
				}
			}
		}

		// update all segments and set labels for new segments
		GeoSegmentND[] segments = poly.getSegments();    	           
		for (int i=0; i < segments.length; i++) {   
			GeoElement seg = (GeoElement) segments[i];
			if (labelPointsAndSegments) {				
				if (!seg.isLabelSet()) {
					seg.setLabel(null);
					seg.setAuxiliaryObject(true);
					seg.setLabelVisible(pointsSegmentsShowLabel);
				} 
				else {
					pointsSegmentsShowLabel = pointsSegmentsShowLabel || seg.isLabelVisible();
				}
			}    			

			seg.getParentAlgorithm().update(); 
		}
	}




	/**
	 * Ensure that the pointList holds n points.
	 * 
	 */
	private void updatePointsArray(int n) {

		GeoPoint[] oldPoints = points;
		int oldPointsLength = oldPoints == null ? 0 : oldPoints.length;
		//System.out.println("update points: " + n + "  old length: " + oldPointsLength);

		// new points
		points = new GeoPoint[n];

		// reuse old points
		for (int i = 0; i < oldPointsLength; i++) {
			if (i < points.length) {
				// reuse old point
				points[i] = oldPoints[i];
			} else {
				removePoint(oldPoints[i]);
			}
		}

		// create new points if needed
		for (int i = oldPointsLength; i < points.length; i++) {
			GeoPoint newPoint = new GeoPoint(cons);
			newPoint.setCoords(0, 0, 1); // set defined
			newPoint.setParentAlgorithm(this);
			newPoint.setEuclidianVisible(true);
			newPoint.setAuxiliaryObject(true);
			points[i] = newPoint;
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

}
