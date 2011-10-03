/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

//

package geogebra.kernel;

import geogebra.euclidian.EuclidianView;
import geogebra.euclidian.GeneralPathClipped;
import geogebra.kernel.kernelND.GeoPointND;
import geogebra.kernel.kernelND.GeoSegmentND;

import java.awt.geom.Area;
import java.awt.geom.PathIterator;
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
public class AlgoPolygonOperation extends AlgoElement {

	private static final long serialVersionUID = 1L;
	private GeoPolygon inPoly0; //input
	private GeoPolygon inPoly1; //input
	private GeoPolygon poly; //output	

	private GeoPoint [] points;
	private int operationType;
	private EuclidianView ev;

	private boolean labelPointsAndSegments;
	private boolean labelsNeedIniting;

	public static final int TYPE_INTERSECTION = 0;
	public static final int TYPE_UNION = 1;
	public static final int TYPE_DIFFERENCE = 2;

	public AlgoPolygonOperation(Construction cons, String[] labels,
			GeoPolygon inPoly0, GeoPolygon inPoly1, int operationType) {

		super(cons);

		ev = cons.getApplication().getEuclidianView();
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


	public String getClassName() {
		return "AlgoPolygonOperation";
	}


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
			output = new GeoElement[1];
			output[0] = poly;
		}
		// otherwise: points and segments are also output objects
		else {
			// size = poly + points + segments
			GeoSegmentND[] segments = poly.getSegments();
			GeoPointND[] points = poly.getPoints();
			int size = 1 + segments.length + points.length;

			output = new GeoElement[size];
			int k = 0;
			output[k] = poly;

			for (int i = 0; i < segments.length; i++) {
				output[++k] = (GeoElement) segments[i];
			}

			for (int i = 0; i < points.length; i++) {
				output[++k] = (GeoElement) points[i];
			}
		}
	}


	GeoPolygon getPoly() {
		return poly;
	}



	/**
	 * Convert array of polygon GeoPoints to an Area object
	 */
	private Area getArea(GeoPointND[] points) {

		double [] coords = new double[2]; 
		GeneralPathClipped gp = new GeneralPathClipped(ev);

		// first point
		points[0].getInhomCoords(coords);		
		gp.moveTo(coords[0], coords[1]);   

		for (int i=1; i < points.length; i++) {
			points[i].getInhomCoords(coords);			
			gp.lineTo(coords[0], coords[1]);
		}
		gp.closePath();

		return new Area(gp);	
	}



	protected final void compute() {

		ArrayList<Double> xcoord = new ArrayList<Double>();
		ArrayList<Double> ycoord = new ArrayList<Double>();
		double[] coords = new double[6];
				
		// Convert input polygons to Area objects
		Area a1 = getArea(inPoly0.getPoints());
		Area a2 = getArea(inPoly1.getPoints());

		// test for empty intersection
		Area testArea = getArea(inPoly0.getPoints());
		testArea.intersect(a2);
		if(testArea.isEmpty()) {
			poly.setUndefined();
		}
		// if intersection is non-empty perform operation
		else
		{
			switch (operationType) {
			case TYPE_INTERSECTION:
				a1.intersect(a2);
				break;
			case TYPE_UNION:
				a1.add(a2);
				break;
			case TYPE_DIFFERENCE:
				a1.subtract(a2);
				break;
			}

			// Iterate through the path of the result 
			// and recover the polygon vertices.
			
			PathIterator it = a1.getPathIterator(null);

			int type = it.currentSegment(coords);
			it.next();
			double[] oldCoords = coords.clone();
			double epsilon = 1E-10;

			while (!it.isDone()) {
				type = it.currentSegment(coords);
				if (type == PathIterator.SEG_CLOSE) {
					break;
				}
				// Sometimes the Path iterator gives two almost identical points and 
				// we only want one of them. 
				// TODO: Why does this happen???
				if ((double) Math.abs(oldCoords[0] - coords[0]) > epsilon
						|| (double) Math.abs(oldCoords[1] - coords[1]) > epsilon) {
					xcoord.add(coords[0]);
					ycoord.add(coords[1]);
				}
				oldCoords = coords.clone();

				it.next();

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
			updateSegmentsAndPointsLabels(oldPointNumber);
		}    	    	
	}         


	private void updateSegmentsAndPointsLabels(int oldPointNumber) {
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
		|| (app.getLabelingStyle() == ConstructionDefaults.LABEL_VISIBLE_USE_DEFAULTS && cons.getConstructionDefaults().getDefaultGeo(ConstructionDefaults.DEFAULT_SEGMENT).isLabelVisible());

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
		ArrayList algoList = oldPoint.getAlgorithmList();
		for (int k = 0; k < algoList.size(); k++) {
			AlgoElement algo = (AlgoElement) algoList.get(k);
			for (int j = 0; j < input.length; j++)
				input[j].removeFromUpdateSets(algo);
		}

		// remove old point
		oldPoint.setParentAlgorithm(null);

		// remove dependent segment algorithm that are part of this polygon
		// to make sure we don't remove the polygon as well
		for (int k = 0; k < algoList.size(); k++) {
			AlgoElement algo = (AlgoElement) algoList.get(k);
			// make sure we don't remove the polygon as well
			if (algo instanceof AlgoJoinPointsSegment
					&& ((AlgoJoinPointsSegment) algo).getPoly() == poly) {
			} else {
				algo.remove();
			}
		}

		algoList.clear();
		// remove point
		oldPoint.doRemove(); 

	}


}
