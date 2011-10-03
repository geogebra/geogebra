/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoAxisSecondLength.java
 *
 * Created on 30. August 2001, 21:37
 */

package geogebra.kernel;



/**
 *
 * @author  Markus
 * @version 
 */
public class AlgoAxisSecondLength extends AlgoElement {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GeoConic c;  // input
    private GeoNumeric num;     // output                  
    
    AlgoAxisSecondLength(Construction cons, String label,GeoConic c) {
        super(cons);        
        this.c = c;                                                              
        num = new GeoNumeric(cons);                
        setInputOutput(); // for AlgoElement                
        compute();              
        num.setLabel(label);            
    }   
    
    public String getClassName() {
        return "AlgoAxisSecondLength";
    }
    
    // for AlgoElement
    protected void setInputOutput() {
        input = new GeoElement[1];
        input[0] = c;        
        
        setOutputLength(1);
        setOutput(0,num);
        setDependencies(); // done by AlgoElement
    }    
    
    GeoNumeric getLength() { return num; }    
    GeoConic getConic() { return c; }        
    
    // set excentricity
    protected final void compute() {  
        switch (c.type) {
            case GeoConic.CONIC_CIRCLE:                               
            case GeoConic.CONIC_HYPERBOLA:
            case GeoConic.CONIC_ELLIPSE:
                num.setValue(c.halfAxes[1]);
                break;
                            
            default:
                num.setUndefined();            
        }        
    }
    
    final public String toString() {
        // Michael Borcherds 2008-03-30
        // simplified to allow better Chinese translation
        return app.getPlain("SecondAxisLengthOfA",c.getLabel());
    }
}
