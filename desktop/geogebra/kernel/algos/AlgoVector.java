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

package geogebra.kernel.algos;

import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.kernel.CircularDefinitionException;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.kernel.AbstractConstruction;
import geogebra.kernel.geos.GeoPoint2;
import geogebra.kernel.geos.GeoVector;
import geogebra.kernel.kernelND.GeoVectorND;


/**
 * Vector between two points P and Q.
 * 
 * @author  Markus
 * @version 
 */
public class AlgoVector extends AlgoElement {

	private GeoPointND P, Q;   // input
    private GeoVectorND  v;     // output     
        
    /** Creates new AlgoVector */  
    public AlgoVector(AbstractConstruction cons, String label, GeoPointND P, GeoPointND Q) {
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
    	
    	return new GeoPoint2((GeoPoint2) P);
    }
    
    @Override
	public String getClassName() {
        return "AlgoVector";
    }
    
    @Override
	public int getRelatedModeID() {
    	return EuclidianConstants.MODE_VECTOR;
    }
    
    // for AlgoElement
    @Override
	public void setInputOutput() {
        input = new GeoElement[2];
        input[0] = (GeoElement) P;
        input[1] = (GeoElement) Q;
        
        super.setOutputLength(1);
        super.setOutput(0, (GeoElement) v);
        setDependencies(); // done by AlgoElement
    }           
    
    public GeoVectorND getVector() { return v; }
    public GeoPointND getP() { return P; }
    public GeoPointND getQ() { return Q; }
    
    // calc the vector between P and Q    
    @Override
	public final void compute() {
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
