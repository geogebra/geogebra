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

import geogebra.euclidian.EuclidianConstants;




/**
 * Computes the area of a conic section
 * @author  Markus Hohenwarter
 * @version 
 */
public class AlgoAreaConic extends AlgoElement {

	private static final long serialVersionUID = 1L;
	private GeoConic  conic;  // input
    private GeoNumeric area;     // output           
        
    AlgoAreaConic(Construction cons, String label, GeoConic c) {       
	  super(cons); 
      this.conic = c;
      area = new GeoNumeric(cons); 
      setInputOutput(); // for AlgoElement
      
      // compute angle
      compute();     
          
      area.setLabel(label);
    }   
  
    public String getClassName() {
        return "AlgoAreaConic";
    }
    
    public int getRelatedModeID() {
    	return EuclidianConstants.MODE_AREA;
    }
    
    // for AlgoElement
    protected void setInputOutput() {
        input =  new GeoElement[1];
        input[0] = conic;
        
        setOutputLength(1);        
        setOutput(0, area);        
        setDependencies(); // done by AlgoElement
    }    
    
    GeoNumeric getArea() { return area; }        
    
    // calc area of conic c 
    protected final void compute() {  
    	if (!conic.isDefined()) {
    		area.setUndefined();
    		return;
    	}
    
    	// area of sector
    	if (conic.isGeoConicPart()) {
    		GeoConicPart conicPart = (GeoConicPart) conic;
    		
    		
    		// added area for arcs, so just call getArea
    		area.setValue(conicPart.getArea());
    		/*
    		 * int partType = conicPart.getConicPartType();
    		
			if (partType == GeoConicPart.CONIC_PART_SECTOR) {				
				// circumference of sector
				area.setValue(conicPart.getValue());					
			}
			else {
				// area of arc is undefined	    	
				area.setUndefined();
			}			*/
    		
			return;
    	}
    	
    	// standard case: area of conic
    	int type = conic.getType();		
		switch (type) {
			case GeoConic.CONIC_CIRCLE:
				// r is length of one of the half axes
				double r = conic.halfAxes[0];
				area.setValue(r * r * Math.PI);
				break;
				
			case GeoConic.CONIC_ELLIPSE:
				// lengths of the half axes
				double a = conic.halfAxes[0];
				double b = conic.halfAxes[1];
				area.setValue(a * b * Math.PI);
				break;
					
			default:			
				area.setUndefined();			
		}    	
    }   
    
    
}
