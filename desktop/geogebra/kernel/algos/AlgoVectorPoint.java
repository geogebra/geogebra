/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoVectorPoint.java
 *
 * Created on 24. September 2001, 21:37
 */

package geogebra.kernel.algos;

import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.kernel.AbstractConstruction;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.kernel.geos.GeoPoint2;
import geogebra.kernel.geos.GeoVector;


/**
 * Vector v = P - (0, 0)
 * @author  Markus
 * @version 
 */
public class AlgoVectorPoint extends AlgoElement {

    private GeoPoint2 P;   // input
    private GeoVector  v;     // output                    
    
    public AlgoVectorPoint(AbstractConstruction cons, String label, GeoPoint2 P) {
        super(cons);
        this.P = P;
        
        // create new vector
        v = new GeoVector(cons);                
        setInputOutput();
                        
        compute();        
        v.setLabel(label);
    }           
    
    @Override
	public String getClassName() {
        return "AlgoVectorPoint";
    }
    
    @Override
	public int getRelatedModeID() {
    	return EuclidianConstants.MODE_VECTOR_FROM_POINT;
    }

    
    // for AlgoElement
    @Override
	protected void setInputOutput() {
        input = new GeoElement[1];
        input[0] = P;
              
        super.setOutputLength(1);
        super.setOutput(0, v);
        setDependencies(); // done by AlgoElement
    }           
    
    public GeoVector getVector() { return v; }
    public GeoPoint2 getP() { return P; }    
    
    // calc vector OP   
    @Override
	public final void compute() {                
        if (P.isFinite()) {                    
            v.x = P.inhomX;
            v.y = P.inhomY;        
            v.z = 0.0;
        } else {
            v.setUndefined();
        }
    }       
    
}
