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

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.algos.Algos;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPoint;

/**
 *
 * @author  ggb3D
 * @version 
 */
public class AlgoTo2D extends AlgoElement3D {

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
    	case SEGMENT3D:
    		GeoPoint P1 = new GeoPoint(cons);
    		GeoPoint P2 = new GeoPoint(cons);
    		P1.setCoords(0,0,1);
    		P2.setCoords(1,0,1);

    		kernel.setSilentMode(true);
    		out = kernel.getAlgoDispatcher().Segment(null, P1, P2);
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

    @Override
	public Algos getClassName() {
        return Algos.AlgoTo2D;
    }

    // for AlgoElement
    @Override
	protected void setInputOutput() {
  	
    	input = new GeoElement3D[1];
    	input[0] = in;      	
    	
        setOutputLength(1);
        setOutput(0, out);
 	
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
    @Override
	public final void compute() {
    	// TODO ?
    	// otherwise comment empty statement
    } 
  
    @Override
	final public String toString(StringTemplate tpl) {     
        return  null;
    }

	// TODO Consider locusequability
}
