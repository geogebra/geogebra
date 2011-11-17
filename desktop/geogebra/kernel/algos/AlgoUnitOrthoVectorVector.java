/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoOrthoVectorVector.java
 *
 * Created on 30. August 2001, 21:37
 */

package geogebra.kernel.algos;

import geogebra.kernel.CircularDefinitionException;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.GeoVec2D;
import geogebra.kernel.GeoVector;


/**
 *
 * @author  Markus
 * @version 
 */
public class AlgoUnitOrthoVectorVector extends AlgoElement {
    
    private GeoVector v; // input
    private GeoVector  n;     // output       
    
    private double length;
        
    /** Creates new AlgoUnitOrthoVectorVector */
    AlgoUnitOrthoVectorVector(Construction cons, String label,GeoVector v) {  
        super(cons);
        this.v = v;                
        n = new GeoVector(cons);
        
        GeoPoint possStartPoint = v.getStartPoint();
        if (possStartPoint != null && possStartPoint.isLabelSet()) {
	        try{
	            n.setStartPoint(possStartPoint);
	        } catch (CircularDefinitionException e) {}
        }
         
        setInputOutput(); // for AlgoElement        
        
        // compute line through P, Q
        n.z = 0.0d;
        compute();      
        n.setLabel(label);
    }   
    
    @Override
	public String getClassName() {
        return "AlgoUnitOrthoVectorVector";
    }
    
    // for AlgoElement
    @Override
	protected void setInputOutput() {
        input = new GeoElement[1];        
        input[0] = v;
        
        super.setOutputLength(1);
        super.setOutput(0, n);
        setDependencies(); // done by AlgoElement
    }    
    
    GeoVector getVector() { return n; }    
    GeoVector getv() { return v; }
    
    // line through P normal to v
    @Override
	protected final void compute() {        
        length = GeoVec2D.length(v.x, v.y);
        n.x = -v.y / length;
        n.y = v.x / length;        
    }   
    
    @Override
	final public String toString() {
        // Michael Borcherds 2008-03-30
        // simplified to allow better Chinese translation
        return app.getPlain("UnitVectorPerpendicularToA",v.getLabel());

    }
}
