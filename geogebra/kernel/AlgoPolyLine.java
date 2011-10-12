/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel;

import geogebra.euclidian.EuclidianConstants;
import geogebra.kernel.Matrix.CoordSys;
import geogebra.kernel.kernelND.GeoPointND;




/**
 * Creates a PolyLine from a given list of points or point array.
 * 
 * @author  Michael Borcherds
 * @version 
 */
public class AlgoPolyLine extends AlgoElement {

	private static final long serialVersionUID = 1L;
	protected GeoPointND [] points;  // input
	protected GeoList geoList;  // alternative input
    protected GeoPolyLine poly;     // output
       
    public AlgoPolyLine(Construction cons, String [] labels, GeoList geoList) {
    	this(cons, labels, null, geoList);
    }
    
    public AlgoPolyLine(Construction cons, GeoList geoList) {
    	this(cons, null, geoList, null, 
        		true, null);
    }
    
    public AlgoPolyLine(Construction cons, String [] labels, GeoPointND [] points) {
    	this(cons, labels, points, null);
    }
 
    protected AlgoPolyLine(Construction cons, String [] labels, GeoPointND [] points, GeoList geoList) {
    	this(cons,labels,points,geoList,null,true,null);
    }
    
    /**
     * @param cons the construction
     * @param labels names of the polygon and the segments
     * @param points vertices of the polygon
     * @param geoList list of vertices of the polygon (alternative to points)
     * @param cs2D for 3D stuff : GeoCoordSys2D
     * @param createSegments  says if the polygon has to creates its edges (3D only) 
     * @param polyhedron polyhedron (when segment is part of), used for 3D
     */
    protected AlgoPolyLine(Construction cons, String [] labels, 
    		GeoPointND [] points, GeoList geoList, CoordSys cs2D, 
    		boolean createSegments, GeoElement polyhedron) {

    	this(cons, points, geoList, cs2D, createSegments, polyhedron);
        
        if (labels != null)
        	poly.setLabel(labels[0]);
        else
        	poly.setLabel(null);
        
    }   
    
    protected AlgoPolyLine(Construction cons,  
    		GeoPointND [] points, GeoList geoList, CoordSys cs2D, 
    		boolean createSegments, GeoElement polyhedron) {
        super(cons);
        this.points = points;           
        this.geoList = geoList;
          
        //poly = new GeoPolygon(cons, points);
        createPolyLine(createSegments);  
        
        // compute polygon points
        compute();  
        
        setInputOutput(); // for AlgoElement
        
    }   
    
    /**
     * create the polygon
     * @param createSegments says if the polygon has to creates its edges (3D only)
     */
    protected void createPolyLine(boolean createSegments){
    	poly = new GeoPolyLine(this.cons, this.points);
    }
        
    public String getClassName() {
        return "AlgoPolyLine";
    }
    
    public int getRelatedModeID() {
    	return EuclidianConstants.MODE_POLYLINE;
    }
    
    
    /**
     * Update point array of polygon using the given array list
     * @param pointList
     */
    private void updatePointArray(GeoList pointList) {
    	// check if we have a point list
    	if (pointList.getElementType() != GeoElement.GEO_CLASS_POINT) {
    		poly.setUndefined();
    		return;
    	}
    	
    	// create new points array
    	int size = pointList.size();
    	points = new GeoPoint[size];
    	for (int i=0; i < size; i++) {    		
    		points[i] = (GeoPoint) pointList.get(i);
    	}
    	poly.setPoints(points);
    	
    }
    
    
    // for AlgoElement
    protected void setInputOutput() {
    	if (geoList != null) {
    		// list as input
			input = new GeoElement[1];
			input[0] = geoList;

    	} else {    	
   			input = (GeoElement[]) points;
    	}    	
    	// set dependencies
        for (int i = 0; i < input.length; i++) {
            input[i].addAlgorithm(this);
        }
        
        // set output
        setOutputLength(1);
        setOutput(0,poly);
        setDependencies();
    }    
    
    public void update() {
        // compute output from input
        compute();
        getOutput(0).update();
    }
    
    
    public GeoPolyLine getPoly() { return poly; }    
    public GeoPoint [] getPoints() {
    	return (GeoPoint[]) points;
    }
    
 
    protected void compute() { 
    	if (geoList != null) {
    		updatePointArray(geoList);
    	}
    	
        // compute area
        poly.calcLength(); 
        
    }   
    
    StringBuilder sb;
    final public String toString() {

        if (sb == null) sb = new StringBuilder();
        else sb.setLength(0);
  
        sb.append(app.getPlain("PolyLine"));
        sb.append(' ');
        
        //G.Sturr: get label from geoList  (2010-3-15)
		if (geoList != null) {
			sb.append(geoList.getLabel());
			
		} else {
		// use point labels
			 
			int last = points.length - 1;
			for (int i = 0; i < last; i++) {
				sb.append(points[i].getLabel());
				sb.append(", ");
			}
			sb.append(points[last].getLabel());
		}
        
        
        return  sb.toString();
    }
}
