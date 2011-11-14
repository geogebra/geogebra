/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra3D.kernel3D;

import geogebra.common.euclidian.EuclidianConstants;
import geogebra.kernel.AlgoPolyLine;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoList;
import geogebra.kernel.Matrix.CoordSys;
import geogebra.kernel.kernelND.GeoPointND;
import geogebra.kernel.kernelND.GeoSegmentND;




/**
 * Creates a PolyLine from a given list of points or point array.
 * 
 * @author  Michael Borcherds
 * @version 
 */
public class AlgoPolyLine3D extends AlgoPolyLine {

	private static final long serialVersionUID = 1L;
    
       
    public AlgoPolyLine3D(Construction cons, String [] labels, GeoList geoList) {
    	this(cons, labels, null, geoList);
    }
    
    public AlgoPolyLine3D(Construction cons, String [] labels, GeoPointND [] points) {
    	this(cons, labels, points, null);
    }
 
    protected AlgoPolyLine3D(Construction cons, String [] labels, GeoPointND [] points, GeoList geoList) {
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
    protected AlgoPolyLine3D(Construction cons, String [] labels, 
    		GeoPointND [] points, GeoList geoList, CoordSys cs2D, 
    		boolean createSegments, GeoElement polyhedron) {
        super(cons, labels, points, geoList, cs2D, createSegments, polyhedron);

        //poly = new GeoPolygon(cons, points);
      //  createPolyLine(createSegments);  
        
        // compute polygon points
      //  compute();  
        
      //  setInputOutput(); // for AlgoElement
        
      //  if (labels != null)
        //	poly.setLabel(labels[0]);
        //else
        //	poly.setLabel(null);
        
    }   
    
    
    /**
     * create the polygon
     * @param createSegments says if the polygon has to creates its edges (3D only)
     */
    protected void createPolyLine(boolean createSegments){
    	poly = new GeoPolyLine3D(this.cons, this.points);
    }
        
    public String getClassName() {
        return "AlgoPolyLine3D";
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
    	points = new GeoPointND[size];
    	for (int i=0; i < size; i++) {    		
    		points[i] = (GeoPointND) pointList.get(i);
    	}
    	poly.setPoints(points);
    	
    }
    
    public void update() {
        // compute output from input
        compute();
        getOutput(0).update();
    }
    
      
    public GeoPointND[] getPointsND() {
    	return points;
    }
    
 
    protected void compute() { 
    	if (geoList != null) {
    		updatePointArray(geoList);
    	}
    	
        // compute area
        poly.calcLength(); 
        
    }   
    
    
    
    // for AlgoElement
    protected void setInputOutput() {
    	
    	//efficient inputs are points or list
    	GeoElement [] efficientInput = createEfficientInput();  	
    	
    	
    		input = new GeoElement[efficientInput.length];
    		for (int i=0; i<efficientInput.length; i++)
    			input[i]=efficientInput[i];
    	
    	
    	
    	setEfficientDependencies(input, efficientInput);
        
    	//set output after, to avoid segments to have this to parent algo
    	//setOutput();
    	
        setOutputLength(1);
        setOutput(0,poly);
        setDependencies();
    	

    }    
    
    protected GeoElement [] createEfficientInput(){

    	GeoElement [] efficientInput;

    	if (geoList != null) {
    		// list as input
    		efficientInput = new GeoElement[1];
    		efficientInput[0] = geoList;
    	} else {    	
    		// points as input
    		efficientInput = new GeoElement[points.length];
    		for(int i = 0; i < points.length; i++)
    			efficientInput[i]=(GeoElement) points[i];
    	}    

    	return efficientInput;
    }
    
    private void setOutput() {
                     
        output[0] = poly;        
        
       
    }
    
    
}
