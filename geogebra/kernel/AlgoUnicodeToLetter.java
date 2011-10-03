/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel;

import geogebra.kernel.arithmetic.NumberValue;


public class AlgoUnicodeToLetter extends AlgoElement {

	private static final long serialVersionUID = 1L;
	protected NumberValue a;  // input
    protected GeoText text;     // output           
        
    protected AlgoUnicodeToLetter(Construction cons, String label, NumberValue a) {       
	  super(cons); 
      this.a = a;

      text = new GeoText(cons); 
		text.setIsTextCommand(true); // stop editing as text
		setInputOutput(); // for AlgoElement
      
      // compute angle
      compute();     
          
      text.setLabel(label);
    }   
  
    public String getClassName() {
        return "AlgoUnicodeToLetter";
    }
    
    // for AlgoElement
    protected void setInputOutput() {
        input =  new GeoElement[1];
        input[0] = a.toGeoElement();
        
        output = new GeoElement[1];        
        output[0] = text;        
        setDependencies(); // done by AlgoElement
    }    
    
    protected GeoText getResult() { return text; }        

      
    protected final void compute() {
    	
    	char ss = (char)a.getDouble();
    	text.setTextString(ss+"");
    }
}
