/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * 
 *
 *  
 */

package geogebra3D.kernel3D;

import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoPoint;




/**
 *
 * @author  ggb3D
 * @version 
 */
public class AlgoTo2D extends AlgoElement3D {

	private static final long serialVersionUID = 1L;
	private GeoElement3D in;  // input
    private GeoElement out; // output 


    /** Creates new AlgoJoinPoints */
    public AlgoTo2D(Construction cons, String label, GeoElement3D in) { //TODO remove public
        this(cons, in);
        out.setLabel(label);
    }

    AlgoTo2D(Construction cons, GeoElement3D in) {
    	super(cons);
 
    	this.in=in;
          
    	switch(in.getGeoClassType()){
    	case GeoElement3D.GEO_CLASS_SEGMENT3D:
    		
    		GeoPoint P1 = new GeoPoint(cons);
    		GeoPoint P2 = new GeoPoint(cons);
    		P1.setCoords(0,0,1);
    		P2.setCoords(1,0,1);

    		kernel.setSilentMode(true);
    		out = kernel.Segment(null, P1, P2);
    		kernel.setSilentMode(false);
    		
    		break;
    	default:
    		out = null;
    	}     
    	
    	if (out!=null){
    		setInputOutput(); // for AlgoElement
    		compute();
    	}
    }   

    public String getClassName() {
        return "AlgoTo2D";
    }

    // for AlgoElement
    protected void setInputOutput() {
  	
    	input = new GeoElement3D[1];
    	input[0] = in;

    	            	
    	
        output = new GeoElement[1];
        output[0] = out;
 	
    	// set dependencies
        input[0].addAlgorithm(this);
        

        // parent of output
        out.setParentAlgorithm(this);       
        cons.addToAlgorithmList(this); 

    }


    GeoElement3D getIn() {
        return in;
    }
     
    GeoElement getOut() {
        return out;
    }
    
    // recalc 
    protected final void compute() {
    	   
    	
    }

    
  
    final public String toString() {
    
        
        return  null;
    }
}
