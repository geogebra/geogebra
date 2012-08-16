/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.common.kernel.algos;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoText;
import geogebra.common.kernel.locusequ.EquationElement;
import geogebra.common.kernel.locusequ.EquationScope;


public class AlgoTextToUnicode extends AlgoElement {

	protected GeoText text;  // input
    protected GeoList list;     // output           
        
    public AlgoTextToUnicode(Construction cons, String label, GeoText text) {       
	  super(cons); 
      this.text = text;

      list = new GeoList(cons); 
      setInputOutput(); // for AlgoElement
      
      compute();     
          
      list.setLabel(label);
    }   
  
    @Override
	public Algos getClassName() {
        return Algos.AlgoTextToUnicode;
    }
    
    // for AlgoElement
    @Override
	protected void setInputOutput() {
        input =  new GeoElement[1];
        input[0] = text;
           
        super.setOutputLength(1);
        super.setOutput(0, list);
        setDependencies(); // done by AlgoElement
    }    
    
    public GeoList getResult() { return list; }        

    @Override
	public void compute()
    {
    	String t = text.getTextString();
    	
    	if (t == null) {
    		list.setUndefined();
    		return;
    	}
      
	  	list.setDefined(true);
		list.clear();
		
		int size = t.length();
		
		if (size == 0) return;
		
		for (int i=0 ; i<size ; i++)
		{
			GeoNumeric num = new GeoNumeric(cons);
			num.setValue(t.charAt(i));
			list.add(num); //num.copy());
		}
    }

	// TODO Consider locusequability
}
