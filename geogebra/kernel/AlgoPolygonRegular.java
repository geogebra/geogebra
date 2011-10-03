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
import geogebra.kernel.arithmetic.MyDouble;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.kernel.kernelND.GeoPointND;
import geogebra.kernel.kernelND.GeoSegmentND;

import java.util.ArrayList;


/**
 * Creates a regular Polygon for two points and the number of vertices.
 * 
 * @author  Markus Hohenwarter
 */
public class AlgoPolygonRegular extends AlgoElement {

	private static final long serialVersionUID = 1L;
	private GeoPoint A, B;  // input
	private NumberValue num; // input
    private GeoPolygon poly;     // output
    
    private GeoPoint [] points;
    private GeoPoint centerPoint;	
    private MyDouble rotAngle;
    
    private boolean labelPointsAndSegments;
    private boolean labelsNeedIniting;
    
    /**
     * Creates a new regular polygon algorithm
     * @param cons
     * @param labels: labels[0] for polygon, then labels for segments and then for points
     * @param A
     * @param B
     * @param num
     */
    AlgoPolygonRegular(Construction cons, String [] labels, GeoPoint A, GeoPoint B, NumberValue num) {
        super(cons);
        labelsNeedIniting = true;
        
        this.A = A;
        this.B = B;
        this.num = num;  
                       
        // labels given by user or loaded from file 
        int labelsLength = labels == null ? 0 : labels.length;

        // set labels for segments only when points have labels
        labelPointsAndSegments = A.isLabelSet() || B.isLabelSet() || labelsLength > 1;              
	
        // temp center point of regular polygon
        centerPoint = new GeoPoint(cons);
        rotAngle = new MyDouble(kernel);   
               
        // output
        points = new GeoPoint[0];
        poly = new GeoPolygon(cons, points);
                     
        // for AlgoElement
        setInputOutput(); 
        
        // compute poly
        compute();      
        
        if (labelPointsAndSegments) {      
			poly.initLabels(labels);
        } else if (labelsLength == 1) {
			poly.setLabel(labels[0]);
        } else {
			poly.setLabel(null);
        }
        
        labelsNeedIniting = false;
        
        // make sure that we set all point and segment labels when needed
        updateSegmentsAndPointsLabels(points.length);
    }   
        
    public String getClassName() {
        return "AlgoPolygonRegular";
    }        
    
    public int getRelatedModeID() {
    	return EuclidianConstants.MODE_REGULAR_POLYGON;
    }
    
    
    // for AlgoElement
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
        poly.setParentAlgorithm(this);       
        cons.addToAlgorithmList(this); 
    }        

    private void setOutput() {    
    	if (points == null) return;
    	
    	// if init points have no labels, all the points and segments
    	// of the polygon don't get labels either: in this case we only
    	// have the polygon itself as output object
    	if (!labelPointsAndSegments) {
    		output = new GeoElement[1];  
    		output[0] = poly;    		
    	}
    	// otherwise: points and segments are also output objects
    	else {
	    	// size = poly + points (without A, B) + segments
	    	GeoSegmentND[] segments = poly.getSegments();
	    	GeoPointND [] points = poly.getPoints();
	        int size = 1 + segments.length + points.length - 2; 
	       
	        output = new GeoElement[size];   
	        int k = 0;
	        output[k] = poly;                                  
	              
	        for (int i=0; i < segments.length; i++) {
	            output[++k] = (GeoElement) segments[i];
	        }    
	        
	        for (int i=2; i < points.length; i++) {
	            output[++k] = (GeoElement) points[i];            
	        }         
    	}
        
        
//    	Application.debug("*** OUTPUT ****************");
//        for (int i=0; i < output.length; i++) {
//			Application.debug(" " + i + ": " + output[i].getLongDescription());		     	        	     	
//		} 
//    	Application.debug("*****************");
        
    }
    
    GeoPolygon getPoly() { return poly; }    
     
    /**
     * Computes points of regular polygon
     */
    protected final void compute() {      
    	// check points and number
    	double nd = num.getDouble();
    	if (Double.isNaN(nd)) nd = 2;
    	
    	// get integer number of vertices n
    	int n = Math.max(2, (int) Math.round( nd ));
    	
    	// if number of points changed, we need to update the
    	// points array and the output array
    	int oldPointNumber = points.length;
    	if (n != oldPointNumber) {
    		updatePointsArray(n);
    		poly.setPoints(points);
    		setOutput();
    	}
    	
    	// check if regular polygon is defined
    	if (n < 3 || !A.isDefined() || !B.isDefined()) {
     		poly.setUndefined();
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
    	centerPoint.setCoords(mx + tanBetaHalf * nx,
    						  my + tanBetaHalf * ny,
    						  1.0);
    	
    	// now we have the center point of the polygon and
    	// the center angle alpha between two neighbouring points
    	// let's create the points by rotating A around the center point
    	for (int k=2; k < n; k++) {    		
    		// rotate point around center point
    		points[k].set((GeoElement) A); 
    		rotAngle.set(k * alpha);
    		points[k].rotate(rotAngle, centerPoint);      		
    	}
    	
    	// compute area of poly
    	poly.calcArea();  
    	//update region coordinate system (Zbynek Konecny, 2010-05-17)
    	poly.updateRegionCS();
    	// update new points and segments 
    	if (n != oldPointNumber) {
    		updateSegmentsAndPointsLabels(oldPointNumber);
    	}    	    	
    }         
    
    private void updateSegmentsAndPointsLabels(int oldPointNumber) {
    	if (labelsNeedIniting)
    		return;
    	
    	// set labels only when points have labels
		labelPointsAndSegments = labelPointsAndSegments || A.isLabelSet() || B.isLabelSet();
		
		GeoSegmentND[] segments = poly.getSegments();    	           

		boolean pointsSegmentsShowLabel = labelPointsAndSegments && 
				(A.isEuclidianVisible() && A.isLabelVisible() || 
				 B.isEuclidianVisible() && B.isLabelVisible());
		
		// set labels for points only if the original points had labels
		if (labelPointsAndSegments) {
			for (int i=2; i < points.length; i++) {            	
				if (!points[i].isLabelSet()) {
					points[i].setLabel(null); 
					points[i].setLabelVisible(pointsSegmentsShowLabel);
				}
			}
		}
		
		// update all segments and set labels for new segments
		for (int i=0; i < segments.length; i++) {   
			GeoElement seg = (GeoElement) segments[i];
			if (labelPointsAndSegments) {				
            	if (!seg.isLabelSet()) {
            		seg.setLabel(null);
            		seg.setAuxiliaryObject(true);
            		// show segment label only if label showing for first segment
            		seg.setLabelVisible(pointsSegmentsShowLabel && segments[0].isLabelVisible());
            	} 
            	else {
            		pointsSegmentsShowLabel = pointsSegmentsShowLabel || seg.isLabelVisible();
            	}
			}    			
        	
			seg.getParentAlgorithm().update(); 
        }
    }
    
    /**
     * Ensures that the pointList holds n points.
     * @param n
     */
    private void updatePointsArray(int n) {
    	GeoPoint [] oldPoints = points;	
    	int oldPointsLength = oldPoints == null ? 0 : oldPoints.length;    	    	
		if (oldPointsLength < 2) {
			// init old points array with first two points A and B
			oldPoints = new GeoPoint[2];
			oldPoints[0] = A;
			oldPoints[1] = B;
			oldPointsLength = 2;
		}
		
		// new points
		points = new GeoPoint[n];
        
		// reuse old points
        for (int i=0; i < oldPointsLength; i++) {
        	if (i < points.length) {
        		// reuse old point
        		points[i] = oldPoints[i];	
        	} else {
        		removePoint(oldPoints[i]);  
        	}        		        	
		}
        
        // create new points if needed
        for (int i=oldPointsLength; i < points.length; i++) {
			GeoPoint newPoint = new GeoPoint(cons);			
			newPoint.setCoords(0,0,1); // set defined
			newPoint.setParentAlgorithm(this);
			newPoint.setPointSize(A.pointSize);
			newPoint.setEuclidianVisible(A.isEuclidianVisible() || B.isEuclidianVisible());
			newPoint.setAuxiliaryObject(true);
			GeoBoolean conditionToShow = A.getShowObjectCondition();
			if (conditionToShow == null) conditionToShow = B.getShowObjectCondition();
			if (conditionToShow != null) {
				try { ((GeoElement)newPoint).setShowObjectCondition(conditionToShow);}
				catch (Exception e) {}
			}

			points[i] = newPoint;						 	        	     
		}    
    }
    
    private void removePoint(GeoPoint oldPoint) {    	
    	// remove dependent algorithms (e.g. segments) from update sets	of
    	// objects further up (e.g. polygon) the tree
		ArrayList algoList = oldPoint.getAlgorithmList();
		for (int k=0; k < algoList.size(); k++) {        			
			AlgoElement algo = (AlgoElement) algoList.get(k);	
			for (int j=0; j < input.length; j++)
				input[j].removeFromUpdateSets(algo);			
		}
    	   
    	// remove old point                     	
		oldPoint.setParentAlgorithm(null);
		
		// remove dependent segment algorithm that are part of this polygon
		// to make sure we don't remove the polygon as well		
		for (int k=0; k < algoList.size(); k++) {        			
			AlgoElement algo = (AlgoElement) algoList.get(k);	
			// make sure we don't remove the polygon as well
			if (algo instanceof AlgoJoinPointsSegment &&
				((AlgoJoinPointsSegment) algo).getPoly() == poly) 
			{        				
			} else {
				algo.remove();
			}
		}
		
		algoList.clear();
		// remove point
		oldPoint.doRemove(); 
    }
    
    
    /**
     * Calls doRemove() for all output objects of this
     * algorithm except for keepGeo.
     */
    void removeOutputExcept(GeoElement keepGeo) {
    	for (int i=0; i < output.length; i++) {
            GeoElement geo = output[i];
            if (geo != keepGeo) {
            	if (geo.isGeoPoint())
            		removePoint((GeoPoint) geo);
            	else 
            		geo.doRemove();
            }            	
        }
    }
       
}
