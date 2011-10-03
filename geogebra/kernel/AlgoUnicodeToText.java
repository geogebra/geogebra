/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel;

public class AlgoUnicodeToText extends AlgoElement {

	private static final long serialVersionUID = 1L;
	protected GeoList list;  // input
    protected GeoText text;     // output           
        
    protected AlgoUnicodeToText(Construction cons, String label, GeoList list) {       
	  super(cons); 
      this.list = list;

      text = new GeoText(cons); 
		text.setIsTextCommand(true); // stop editing as text
		setInputOutput(); // for AlgoElement
      
      // compute angle
      compute();     
          
      text.setLabel(label);
    }   
  
    public String getClassName() {
        return "AlgoUnicodeToText";
    }
    
    // for AlgoElement
    protected void setInputOutput() {
        input =  new GeoElement[1];
        input[0] = list;
        
        output = new GeoElement[1];        
        output[0] = text;        
        setDependencies(); // done by AlgoElement
    }    
    
    protected GeoText getResult() { return text; }        

      
    protected void compute() {
    	
    	int size = list.size();
    	
    	if (size == 0)
    	{
    		text.setTextString("");
    		return;
    	}
    	
    	String s="";
    	
    	for (int i=0 ; i<size ; i++)
    	{ 	
	    	GeoElement geo = list.get(i);
	    	
	    	if (geo.isGeoNumeric())
	    	{   	
	    		s += (char)((GeoNumeric)geo).getDouble();
	    	}
    	}
    	text.setTextString(s);
    }
}
