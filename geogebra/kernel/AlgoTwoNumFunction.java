/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * Area of polygon P[0], ..., P[n]
 *
 */

package geogebra.kernel;

import geogebra.kernel.arithmetic.NumberValue;

/**
 * Parent algorithm for commands that are functions of R^2 -> R
 * @author  Markus Hohenwarter
 * @version 
 */
public abstract class AlgoTwoNumFunction extends AlgoElement {

	private static final long serialVersionUID = 1L;
	protected NumberValue a, b;  // input
    protected GeoNumeric num;     // output           
        
    protected AlgoTwoNumFunction(Construction cons, String label, NumberValue a, NumberValue b) {       
  	  	this(cons, a, b);
        num.setLabel(label);
      }   
    
    protected AlgoTwoNumFunction(Construction cons, NumberValue a, NumberValue b) {       
  	  super(cons); 
        this.a = a;
        this.b = b;
        num = new GeoNumeric(cons); 
        setInputOutput(); // for AlgoElement
        
        // compute angle
        compute();     
      }   
    
    public abstract String getClassName();
    
    // for AlgoElement
    protected void setInputOutput() {
        input =  new GeoElement[2];
        input[0] = a.toGeoElement();
        input[1] = b.toGeoElement();
        
        output = new GeoElement[1];        
        output[0] = num;        
        setDependencies(); // done by AlgoElement
    }    
    
    protected GeoNumeric getResult() { return num; }        

    abstract protected void compute();     
}
