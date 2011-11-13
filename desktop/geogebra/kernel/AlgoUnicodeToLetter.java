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
  
    @Override
	public String getClassName() {
        return "AlgoUnicodeToLetter";
    }
    
    // for AlgoElement
    @Override
	protected void setInputOutput() {
        input =  new GeoElement[1];
        input[0] = a.toGeoElement();
          
        super.setOutputLength(1);
        super.setOutput(0, text);
        setDependencies(); // done by AlgoElement
    }    
    
    protected GeoText getResult() { return text; }        
      
    @Override
	protected final void compute() {
    	
    	char ss = (char)a.getDouble();
    	text.setTextString(ss+"");
    }
}
