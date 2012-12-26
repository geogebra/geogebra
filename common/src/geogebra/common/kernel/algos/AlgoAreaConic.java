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

package geogebra.common.kernel.algos;

import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoConic;
import geogebra.common.kernel.geos.GeoConicPart;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.kernelND.GeoConicND;
import geogebra.common.kernel.kernelND.GeoConicNDConstants;


/**
 * Computes the area of a conic section
 * @author  Markus Hohenwarter
 * @version 
 */
public class AlgoAreaConic extends AlgoElement {

	private GeoConicND  conic;  // input
    private GeoNumeric area;     // output           
        
    public AlgoAreaConic(Construction cons, String label, GeoConicND c) {       
	  super(cons); 
      this.conic = c;
      area = new GeoNumeric(cons); 
      setInputOutput(); // for AlgoElement
      
      // compute angle
      compute();     
          
      area.setLabel(label);
    }   
  
    @Override
	public Commands getClassName() {
        return Commands.Area;
    }
    
    @Override
	public int getRelatedModeID() {
    	return EuclidianConstants.MODE_AREA;
    }
    
    // for AlgoElement
    @Override
	protected void setInputOutput() {
        input =  new GeoElement[1];
        input[0] = conic;
        
        setOutputLength(1);        
        setOutput(0, area);        
        setDependencies(); // done by AlgoElement
    }    
    
    public GeoNumeric getArea() { return area; }        
    
    // calc area of conic c 
    @Override
	public final void compute() {  
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
			case GeoConicNDConstants.CONIC_CIRCLE:
				// r is length of one of the half axes
				double r = conic.halfAxes[0];
				area.setValue(r * r * Math.PI);
				break;
				
			case GeoConicNDConstants.CONIC_ELLIPSE:
				// lengths of the half axes
				double a = conic.halfAxes[0];
				double b = conic.halfAxes[1];
				area.setValue(a * b * Math.PI);
				break;
					
			default:			
				area.setUndefined();			
		}    	
    }

	// TODO Consider locusequability   
    
    
}
