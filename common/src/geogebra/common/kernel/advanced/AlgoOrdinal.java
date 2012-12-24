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
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoText;

public class AlgoOrdinal extends AlgoElement {

	protected GeoNumeric n;  // input
    protected GeoText text;     // output           
        
    public AlgoOrdinal(Construction cons, String label, GeoNumeric list) {       
	  super(cons); 
      this.n = list;

      text = new GeoText(cons); 
		text.setIsTextCommand(true); // stop editing as text
		setInputOutput(); // for AlgoElement
      
      // compute angle
      compute();     
          
      text.setLabel(label);
    }   
  
    @Override
	public Commands getClassName() {
        return Commands.Ordinal;
    }
    
    // for AlgoElement
    @Override
	protected void setInputOutput() {
        input =  new GeoElement[1];
        input[0] = n;
          
        super.setOutputLength(1);
        super.setOutput(0, text);
        setDependencies(); // done by AlgoElement
    }    
    
    public GeoText getResult() { 
    	return text; 
    }        
      
    @Override
	public void compute() {
    	
    	if (!n.isDefined()) {
    		text.setTextString("");
    		text.setUndefined();
    		return;
    	}
    	
    	double num = n.getDouble();
    	
    	if (num < 0 || Double.isNaN(num) || Double.isInfinite(num)){
    		text.setTextString("");
    		text.setUndefined();
    		return;   		
    	}
    	
    	text.setTextString(app.getOrdinalNumber((int)num));
    	
    }

	// TODO Consider locusequability
}
