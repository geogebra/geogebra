/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoRadius.java
 *
 * Created on 30. August 2001, 21:37
 */

package geogebra.kernel;



/**
 *
 * @author  Markus
 * @version 
 */
public class AlgoRadius extends AlgoElement {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GeoConic c;  // input
    private GeoNumeric num;     // output                  
    
    AlgoRadius(Construction cons, GeoConic c) {        
        super(cons);
        this.c = c;                                                              
        num = new GeoNumeric(cons);                
        setInputOutput(); // for AlgoElement                
        compute();              
        
    }   
    
    AlgoRadius(Construction cons, String label,GeoConic c) {        
        this(cons,c);    
        num.setLabel(label);            
    }   
    
    public String getClassName() {
        return "AlgoRadius";
    }
    
    // for AlgoElement
    protected void setInputOutput() {
        input = new GeoElement[1];
        input[0] = c;        
        
        output = new GeoElement[1];
        output[0] = num;
        setDependencies(); // done by AlgoElement
    }       
    
    GeoNumeric getRadius() { return num; }    
    GeoConic getConic() { return c; }        
    
    // set parameter of parabola
    protected final void compute() {        
        if (c.type == GeoConic.CONIC_CIRCLE)
            num.setValue(c.halfAxes[0]);
        else 
            num.setUndefined();
    }
    
    final public String toString() {
        // Michael Borcherds 2008-03-30
        // simplified to allow better Chinese translation
        return app.getPlain("RadiusOfA",c.getLabel());

    }
}
