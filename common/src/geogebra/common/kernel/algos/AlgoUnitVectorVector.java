/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoUnitVectorVector.java
 *
 * Created on 30. August 2001, 21:37
 */

package geogebra.common.kernel.algos;

import geogebra.common.kernel.CircularDefinitionException;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.geos.GeoVector;
import geogebra.common.util.MyMath;


/**
 *
 * @author  Markus
 * @version 
 */
public class AlgoUnitVectorVector extends AlgoElement {
    
    private GeoVector v; // input
    private GeoVector  u;     // output       
    
    private double length;
        
    /** Creates new AlgoOrthoVectorVector */
    public AlgoUnitVectorVector(Construction cons, String label,GeoVector v) {        
        super(cons);
        this.v = v;                
        u = new GeoVector(cons); 
        
        GeoPoint possStartPoint = v.getStartPoint();
        if (possStartPoint != null && possStartPoint.isLabelSet()) {
	        try{
	            u.setStartPoint(possStartPoint);
	        } catch (CircularDefinitionException e) {}
        }
        
        setInputOutput(); // for AlgoElement
        
        u.z = 0.0d;
        compute();      
        u.setLabel(label);
    }   
    
    // for AlgoElement
    @Override
	protected void setInputOutput() {
        input = new GeoElement[1];        
        input[0] = v;
        
        super.setOutputLength(1);
        super.setOutput(0, u);
        setDependencies(); // done by AlgoElement
    }    
    
    @Override
	public Commands getClassName() {
        return Commands.UnitVector;
    }
    
    public GeoVector getVector() { return u; }    
    GeoVector getv() { return v; }
    
    // unit vector of v
    @Override
	public final void compute() {
        length = MyMath.length(v.x, v.y);        
        u.x = v.x / length;
        u.y = v.y / length;
    }   
    
    @Override
	final public String toString(StringTemplate tpl) {
        // Michael Borcherds 2008-03-31
        // simplified to allow better translation
    	return app.getPlain("UnitVectorOfA",v.getLabel(tpl));
    }

	// TODO Consider locusequability
}
