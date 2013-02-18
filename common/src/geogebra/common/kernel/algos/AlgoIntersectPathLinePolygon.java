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
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.geos.GeoPolygon;
import geogebra.common.kernel.geos.GeoRay;
import geogebra.common.kernel.geos.GeoSegment;
import geogebra.common.kernel.kernelND.GeoElementND;
import geogebra.common.kernel.kernelND.GeoLineND;
import geogebra.common.kernel.kernelND.GeoPointND;
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

	protected TreeMap<Double, Coords> newCoords;

	/**
	 * common constructor
	 * 
	 * @param c
	 * @param labels
	 * @param geo line
	 * @param p polygon
	 */
	public AlgoIntersectPathLinePolygon(Construction c, String[] labels,
			GeoElement geo, GeoElement p) {

		super(c);

		outputSegments = createOutputSegments();

		setFirstInput(geo);
		setSecondInput(p);

		newCoords = new TreeMap<Double, Coords>(
				Kernel.DoubleComparator(Kernel.STANDARD_PRECISION));

		setInputOutput(); // for AlgoElement

		setLabels(labels);

		update();

	}
	
	public AlgoIntersectPathLinePolygon(Construction c) {
		super(c);
	}

	/**
	 * @param geo first input
	 */
	protected void setFirstInput(GeoElement geo){
		this.g = (GeoLineND) geo;
	}

	/**
	 * 
	 * @return first input
	 */
	protected GeoElement getFirstInput(){
		return (GeoElement) g;
	}
	
	/**
	 * @param geo first input
	 */
	protected void setSecondInput(GeoElement geo){
		this.p = (GeoPolygon) geo;
	}

	/**
	 * 
	 * @return first input
	 */
	protected GeoElement getSecondInput(){
		return p;
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
				setSegmentVisualProperties(a);
				return a;
			}
		});
	}
	

	/**
	 * set visual style for new segments
	 * @param segment segment
	 */
	public void setSegmentVisualProperties(GeoElement segment){
		if (outputSegments.size()>0){
			GeoElement seg0 = outputSegments.getElement(0);
			segment.setAllVisualProperties(seg0, false);
			segment.setViewFlags(seg0.getViewSet());
		}
	}

	@Override
	public Commands getClassName() {
        return Commands.IntersectPath;
    }

	@Override
	public int getRelatedModeID() {
		return EuclidianConstants.MODE_INTERSECTION_CURVE;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[2];
		input[0] = getFirstInput();
		input[1] = getSecondInput();

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
	protected void intersectionsCoords(GeoPolygon p){

		for(int i=0; i<p.getSegments().length; i++){
			GeoSegmentND seg = p.getSegments()[i];
			
			//check if the segment is defined (e.g. for regular polygons)
			if (seg.isDefined()){
				Coords o2 = seg.getPointInD(3, 0);
				Coords d2 = seg.getPointInD(3, 1).sub(o2);

				Coords[] project = CoordMatrixUtil.nearestPointsFromTwoLines(
						o1,d1,o2,d2
						);

				//check if projection is intersection point
				if (project!=null && project[0].equalsForKernel(project[1], Kernel.STANDARD_PRECISION)){

					double t1 = project[2].get(1); //parameter on line
					double t2 = project[2].get(2); //parameter on segment


					if (checkParameter(t1) && onSegment(t2))//seg.respectLimitedPath(t2))
						addCoords(t1, project[0], seg);

				}
			}
		}

	}
	
	final private static boolean onSegment(double t){
		//t=0 and t=1 can be ignored: vertices will be added by addPolygonPoints()
		return Kernel.isGreater(t, 0) && Kernel.isGreater(1, t);
	}
	
	
	/**
	 * check if midpoint (a,b) is in the polygon
	 * @param p polygon
	 * @param a point
	 * @param b point
	 * @return check
	 */
	protected boolean checkMidpoint(GeoPolygon p, Coords a, Coords b){
		Coords midpoint = a.add(b).mul(0.5);
		//App.debug("\n"+midpoint+"\n"+ p.isInRegion(midpoint.getX(), midpoint.getY()));
		return  p.isInRegion(midpoint.getX(), midpoint.getY());
	}
	

	/**
	 * add start/end points to new coords collection
	 */
	protected void addStartEndPoints(){
		if (g instanceof GeoSegment){
			newCoords.put(0.0,g.getStartPoint().getInhomCoordsInD(2));
			newCoords.put(1.0,g.getEndPoint().getInhomCoordsInD(2));
		}else if (g instanceof GeoRay)
			newCoords.put(0d,g.getStartPoint().getInhomCoordsInD(2));
	}
	
	
	/**
	 * add polygon points that are on the line
	 */
	protected void addPolygonPoints(){

		for(int i=0; i<p.getPoints().length; i++){
			GeoPointND geoPoint = p.getPointsND()[i];
			//check if the point is defined (e.g. for regular polygons)
			if (geoPoint.isDefined()){
				Coords point = geoPoint.getInhomCoordsInD(3);

				Coords[] project = point.projectLine(o1, d1);

				//App.debug("\npoint=\n"+point+"\nproject=\n"+project[0]);

				//check if projection is intersection point
				if (project[0].equalsForKernel(point, Kernel.STANDARD_PRECISION)){

					double t1 = project[1].get(1); 

					if (checkParameter(t1))
						addCoords(t1, project[0], geoPoint);
				}
			}
		}
	}
	
	/**
	 * add coords
	 * @param parameter
	 * @param coords
	 * @param newCoords
	 */
	protected void addCoords(double parameter, Coords coords, GeoElementND parent){
		newCoords.put(parameter, new Coords(coords.getX(), coords.getY()));
	}

	
	
	/**
	 * set all new intersection points coords
	 */
	protected void setNewCoords(){
		
		newCoords.clear();
		
		//line origin and direction
		setIntersectionLine();

		
		//add start/end points for segments/rays
		addStartEndPoints();
		
		//add polygon points
		addPolygonPoints();
		

		// fill a new points map
		intersectionsCoords(p);
	}

	
	@Override
	public void compute() {
		
		
		
		

		// set the point map
		setNewCoords();
		

		// set segments
		if (newCoords.size()<2) { //no segment
			outputSegments.adjustOutputSize(1);
			outputSegments.getElement(0).setUndefined();
		} else {
			//check which bi-points are segments, and save indices
			ArrayList<Coords[]> segmentList = new ArrayList<Coords[]>();
			Coords[] points = new Coords[newCoords.size()];
			newCoords.values().toArray(points);
			Coords b = points[0];
			Coords startSegment = null;
			Coords endSegment = null;
			for (int i=1; i<newCoords.size(); i++) {
				Coords a = b;
				b = points[i];
				if (checkMidpoint(p, a, b)){
					if (startSegment==null)
						startSegment = a; //new start segment
					endSegment = b; //extend segment to b
				}else{
					if (startSegment!=null){//add last correct segment
						segmentList.add(new Coords[] {startSegment,endSegment});
						startSegment=null;
					}
				}
			}
			if (startSegment!=null)//add last correct segment
				segmentList.add(new Coords[] {startSegment,endSegment});
			
			//adjust segments output
			if (segmentList.size()==0){
				outputSegments.adjustOutputSize(1);
				outputSegments.getElement(0).setUndefined();
			}else{
				outputSegments.adjustOutputSize(segmentList.size());
				outputSegments.updateLabels();
				int indexSegment = 0;
				for (Coords[] seg : segmentList){
					GeoSegmentND segment = (GeoSegmentND) outputSegments
							.getElement(indexSegment);
		   			//App.debug("\na=\n"+points[i-1]+"\nb=\n"+points[i]);
					setSegment(segment, seg[0], seg[1]);
					//((GeoElement) segment).update(); // TODO optimize it
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
		return loc.getPlain("IntersectionOfAandB",
				getFirstInput().getLabel(tpl), getSecondInput().getLabel(tpl));
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
