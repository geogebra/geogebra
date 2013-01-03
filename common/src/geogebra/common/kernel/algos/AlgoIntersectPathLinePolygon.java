/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * AlgoIntersectLines.java
 *
 * Created on 30. August 2001, 21:37
 */

package geogebra.common.kernel.algos;

import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.Matrix.CoordMatrixUtil;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.geos.GeoPolygon;
import geogebra.common.kernel.geos.GeoRay;
import geogebra.common.kernel.geos.GeoSegment;
import geogebra.common.kernel.kernelND.GeoLineND;
import geogebra.common.kernel.kernelND.GeoSegmentND;

import java.util.ArrayList;
import java.util.TreeMap;

/**
 * Algo for intersection of a line with the interior of a polygon
 * 
 * @author matthieu
 * @version
 */
public class AlgoIntersectPathLinePolygon extends AlgoElement {

	protected GeoLineND g; // input
	protected GeoPolygon p; // input
	protected OutputHandler<GeoElement> outputSegments; // output
	protected int spaceDim = 2;

	private TreeMap<Double, Coords> newCoords;

	/**
	 * common constructor
	 * 
	 * @param c
	 * @param labels
	 * @param g
	 * @param p
	 */
	public AlgoIntersectPathLinePolygon(Construction c, String[] labels,
			GeoLineND g, GeoPolygon p) {

		super(c);

		outputSegments = createOutputSegments();

		this.g = g;
		this.p = p;

		newCoords = new TreeMap<Double, Coords>(
				Kernel.DoubleComparator(Kernel.STANDARD_PRECISION));

		init();
		setInputOutput(); // for AlgoElement

		compute();

		setLabels(labels);
		// TODO: actually no need to update
		// update();

	}

	 protected void init() {
		// TODO Auto-generated method stub
		spaceDim = 2;
	}



	protected OutputHandler<GeoElement> createOutputSegments() {
		return new OutputHandler<GeoElement>(new elementFactory<GeoElement>() {
			public GeoSegment newElement() {
				GeoSegment a = new GeoSegment(cons);
				GeoPoint aS = new GeoPoint(cons);
				aS.setCoords(0, 0, 1);
				GeoPoint aE = new GeoPoint(cons);
				aE.setCoords(0, 0, 1);
				a.setPoints(aS, aE);
				a.setParentAlgorithm(AlgoIntersectPathLinePolygon.this);
				if (outputSegments.size()>0)
					a.setAllVisualProperties(outputSegments.getElement(0), false);
				return a;
			}
		});
	}

	@Override
	public Algos getClassName() {
		return Algos.AlgoIntersectPathLinePolygon;
	}

	@Override
	public int getRelatedModeID() {
		return EuclidianConstants.MODE_INTERSECTION_CURVE;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[2];
		input[0] = (GeoElement) g;
		input[1] = p;

		setDependencies(); // done by AlgoElement
	}

	protected Coords o1, d1;

	protected void setIntersectionLine(){

		o1 = g.getPointInD(3, 0);
		d1 = g.getPointInD(3, 1).sub(o1);
	}
	
    /**
     * check the first parameter
     * @param t1 parameter
     * @return true if ok
     */
    protected boolean checkParameter(double t1){
    	return g.respectLimitedPath(t1);
    }


    /**
     * calc all intersection points between line and polygon p
     * @param p polygon
     * @param newCoords intersection points
     */
	protected void intersectionsCoords(GeoPolygon p, TreeMap<Double, Coords> newCoords){

		for(int i=0; i<p.getSegments().length; i++){
			GeoSegmentND seg = p.getSegments()[i];

			Coords o2 = seg.getPointInD(3, 0);
			Coords d2 = seg.getPointInD(3, 1).sub(o2);

			Coords[] project = CoordMatrixUtil.nearestPointsFromTwoLines(
					o1,d1,o2,d2
					);

			//check if projection is intersection point
			if (project!=null && project[0].equalsForKernel(project[1], Kernel.STANDARD_PRECISION)){

				double t1 = project[2].get(1); //parameter on line
				double t2 = project[2].get(2); //parameter on segment


				if (checkParameter(t1) && seg.respectLimitedPath(t2))
					addCoords(t1, project[0], newCoords);

			}
		}

	}
	
	
	/**
	 * check if midpoint (a,b) is in the polygon
	 * @param a point
	 * @param b point
	 * @return check
	 */
	protected boolean checkMidpoint(Coords a, Coords b){
		Coords midpoint = a.add(b).mul(0.5);
		return  p.isInRegion(midpoint.getX(), midpoint.getY());
	}
	

	/**
	 * add start/end points to new coords collection
	 * @param newCoords coords collection
	 */
	protected void addStartEndPoints(TreeMap<Double, Coords> newCoords){
		if (g instanceof GeoSegment){
			newCoords.put(0.0,g.getStartPoint().getInhomCoordsInD(2));
			newCoords.put(1.0,g.getEndPoint().getInhomCoordsInD(2));
		}else if (g instanceof GeoRay)
			newCoords.put(0d,g.getStartPoint().getInhomCoordsInD(2));
	}
	
	
	/**
	 * add polygon points that are on the line
	 * @param newCoords coords collection
	 */
	protected void addPolygonPoints(TreeMap<Double, Coords> newCoords){
		
		for(int i=0; i<p.getPoints().length; i++){
			Coords point = p.getPoints()[i].getInhomCoordsInD(3);

			Coords[] project = point.projectLine(o1, d1);

			//check if projection is intersection point
			if (project[0].equalsForKernel(point, Kernel.STANDARD_PRECISION)){

				double t1 = project[1].get(1); 

				if (checkParameter(t1))
					addCoords(t1, project[0], newCoords);
			}
		}
	}
	
	/**
	 * add coords
	 * @param parameter
	 * @param coords
	 * @param newCoords
	 */
	protected void addCoords(double parameter, Coords coords, TreeMap<Double, Coords> newCoords){
		newCoords.put(parameter, new Coords(coords.getX(), coords.getY()));
	}

	@Override
	public void compute() {

		// clear the points map
		newCoords.clear();
		
		//line origin and direction
		setIntersectionLine();

		
		//add start/end points for segments/rays
		addStartEndPoints(newCoords);
		
		//add polygon points
		addPolygonPoints(newCoords);
		

		// fill a new points map
		intersectionsCoords(p, newCoords);



		if (newCoords.size()<2) { //no segment
			outputSegments.adjustOutputSize(1);
			outputSegments.getElement(0).setUndefined();
		} else {
			//check which bi-points are segments, and save indices
			ArrayList<Integer> segIndices = new ArrayList<Integer>();
			Coords[] points = new Coords[newCoords.size()];
			newCoords.values().toArray(points);
			Coords b = points[0];
			for (int i=1; i<newCoords.size(); i++) {
				Coords a = b;
				b = points[i];
    			if (checkMidpoint(a, b))
    				segIndices.add(i);
			}
			
			//adjust segments output
			if (segIndices.size()==0){
				outputSegments.adjustOutputSize(1);
				outputSegments.getElement(0).setUndefined();
			}else{
				outputSegments.adjustOutputSize(segIndices.size());
				outputSegments.updateLabels();
				int indexSegment = 0;
				for (int i : segIndices){
					GeoSegmentND segment = (GeoSegmentND) outputSegments
							.getElement(indexSegment);
					setSegment(segment, points[i-1], points[i]);
		   			//App.debug("\na=\n"+points[i-1]+"\nb=\n"+points[i]);
					((GeoElement) segment).update(); // TODO optimize it
					indexSegment++;
				}
			}
			
		}
	}
	
	/**
	 * set segment start and end points
	 * @param seg segment
	 * @param start point
	 * @param end point
	 */
	protected void setSegment(GeoSegmentND seg, Coords start, Coords end){
		((GeoSegment) seg).setTwoPointsInhomCoords(start, end);
	}


	@Override
	public String toString(StringTemplate tpl) {
		return app.getPlain("IntersectionOfAandB",
				((GeoElement) g).getLabel(tpl), p.getLabel(tpl));
	}


	protected void setLabels(String[] labels) {

		if (labels!=null &&
				labels.length==1 &&
				outputSegments.size() > 1 &&
				labels[0]!=null &&
				!labels[0].equals("")) {
			outputSegments.setIndexLabels(labels[0]);
      	

    	} else {
    		outputSegments.setLabels(labels);
    	}

	}

	// TODO Consider locusequability

}
