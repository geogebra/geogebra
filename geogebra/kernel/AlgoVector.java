/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoVector.java
 *
 * Created on 24. September 2001, 21:37
 */

package geogebra.kernel;

import geogebra.euclidian.EuclidianConstants;
import geogebra.kernel.kernelND.GeoPointND;
import geogebra.kernel.kernelND.GeoVectorND;



/**
 * Vector between two points P and Q.
 * 
 * @author  Markus
 * @version 
 */
public class AlgoVector extends AlgoElement {

	private static final long serialVersionUID = 1L;
	private GeoPointND P, Q;   // input
    private GeoVectorND  v;     // output     
        
    /** Creates new AlgoVector */  
    protected AlgoVector(Construction cons, String label, GeoPointND P, GeoPointND Q) {
        super(cons);
        this.P = P;
        this.Q = Q;         
        
        // create new vector
        v=createNewVector();      
        //v = new GeoVector(cons);   
        try {     
        	if (P.isLabelSet())
        		v.setStartPoint(P);
            else {
            	GeoPointND startPoint = newStartPoint();
            	//GeoPoint startPoint = new GeoPoint(P);
            	startPoint.set(P);
            	v.setStartPoint(startPoint);
            }        		
        } catch (CircularDefinitionException e) {}
        
        
                 
        setInputOutput();
        
        // compute vector PQ        
        compute();                          
        v.setLabel(label);
    }        
    
    
    
    protected GeoVectorND createNewVector(){
    	
    	return new GeoVector(cons);
    	
    }
    
    
    protected GeoPointND newStartPoint(){
    	
    	return new GeoPoint((GeoPoint) P);
 
    }
    
    
    
    
    
    
    
    public String getClassName() {
        return "AlgoVector";
    }
    
    public int getRelatedModeID() {
    	return EuclidianConstants.MODE_VECTOR;
    }

    
    // for AlgoElement
    public void setInputOutput() {
        input = new GeoElement[2];
        input[0] = (GeoElement) P;
        input[1] = (GeoElement) Q;
        
        output = new GeoElement[1];        
        output[0] = (GeoElement) v;        
        setDependencies(); // done by AlgoElement
    }           
    
    public GeoVectorND getVector() { return v; }
    public GeoPointND getP() { return P; }
    public GeoPointND getQ() { return Q; }
    
    // calc the vector between P and Q    
    protected final void compute() {
        if (P.isFinite() && Q.isFinite()) {     
        	     	
           	setCoords();
                       
            // update position of unlabeled startpoint
            GeoPointND startPoint = v.getStartPoint();
            
            if (startPoint!=null)
            	if (!startPoint.isLabelSet())
            		startPoint.set(P);       
            		  
        } else {
            v.setUndefined();
        }
    }
    
    protected void setCoords(){
    	v.setCoords(P.vectorTo(Q));
    }
    
}
