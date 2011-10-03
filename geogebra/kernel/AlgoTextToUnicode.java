/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel;


public class AlgoTextToUnicode extends AlgoElement {

	private static final long serialVersionUID = 1L;
	protected GeoText text;  // input
    protected GeoList list;     // output           
        
    protected AlgoTextToUnicode(Construction cons, String label, GeoText text) {       
	  super(cons); 
      this.text = text;

      list = new GeoList(cons); 
      setInputOutput(); // for AlgoElement
      
      compute();     
          
      list.setLabel(label);
    }   
  
    public String getClassName() {
        return "AlgoTextToUnicode";
    }
    
    // for AlgoElement
    protected void setInputOutput() {
        input =  new GeoElement[1];
        input[0] = text;

        
        output = new GeoElement[1];        
        output[0] = list;        
        setDependencies(); // done by AlgoElement
    }    
    
    protected GeoList getResult() { return list; }        

    protected void compute()
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
}
