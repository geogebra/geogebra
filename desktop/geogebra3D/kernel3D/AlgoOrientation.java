/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra3D.kernel3D;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.GeoDirectionND;




/**
 *
 * @author  mathieu
 * @version 
 * 
 * Calculate the orientation vector of a plane (or polygon, ...) or a line
 * 
 */
public class AlgoOrientation extends AlgoElement3D {

	
	//input
	/** direction */
	private GeoDirectionND direction;
	
	//output
	/** vector */
	private GeoVector3D vector;


    /** Creates new AlgoIntersectLinePlane 
     * @param cons the construction
     * @param label name of point
     * @param direction geo that has a direction (orientation)
     */    
    public AlgoOrientation(Construction cons, String label, GeoDirectionND direction) {

    	super(cons);


    	this.direction = direction;
    	
    	vector = new GeoVector3D(cons);
  
    	setInputOutput(new GeoElement[] {(GeoElement) direction}, new GeoElement[] {vector});

    	vector.setLabel(label);
 
    }
    
 



    
    
    
    
    /**
     * return the vector
     * @return the vector
     */   
    public GeoVector3D getVector() {
        return vector;
    }
   
    
    
    

    ///////////////////////////////////////////////
    // COMPUTE
    
    
    
    
    @Override
	public void compute(){
    	
    	if (!((GeoElement) direction).isDefined()){
    		vector.setUndefined();
    		return;
    	}
    	
    	vector.setCoords(direction.getDirectionInD3());
    	
    }
    
    
    
    

	@Override
	public Commands getClassName() {
    	
    	return Commands.Orientation;
	}

	
	
	
    @Override
	final public String toString(StringTemplate tpl) {
        return loc.getPlain("OrientationOfA", ((GeoElement) direction).getLabel(tpl));

    }  

  
 

}
