/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.common.kernel.advanced;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.algos.Algos;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoText;


public class AlgoUnicodeToLetter extends AlgoElement {

	protected NumberValue a;  // input
    protected GeoText text;     // output           
        
    public AlgoUnicodeToLetter(Construction cons, String label, NumberValue a) {       
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
	public Algos getClassName() {
        return Algos.AlgoUnicodeToLetter;
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
    
    public GeoText getResult() { return text; }        
      
    @Override
	public final void compute() {
    	
    	char ss = (char)a.getDouble();
    	text.setTextString(ss+"");
    }

	// TODO Consider locusequability
}
