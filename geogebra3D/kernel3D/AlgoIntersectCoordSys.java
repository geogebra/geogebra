/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 */

package geogebra3D.kernel3D;

import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;




/**
 *
 * @author  ggb3D
 * @version 
 * 
 * Calculate the GeoPoint3D intersection of two coord sys (eg line and plane).
 * 
 */
public abstract class AlgoIntersectCoordSys extends AlgoElement3D {

	
	//inputs
	/** first coord sys */
	private GeoElement cs1;
	/** second coord sys */
	private GeoElement cs2;
	
	//output
	/** intersection */
	private GeoElement3D intersection;


    /** Creates new AlgoIntersectLinePlane 
     * @param cons the construction
     * @param label name of point
     * @param cs1 first coord sys
     * @param cs2 second coord sys
     */    
    AlgoIntersectCoordSys(Construction cons, String label, GeoElement cs1, GeoElement cs2) {

    	this(cons, cs1, cs2);
    	intersection.setLabel(label);
 
    }
    
    AlgoIntersectCoordSys(Construction cons, GeoElement cs1, GeoElement cs2) {

    	super(cons);


    	setCoordSys(cs1, cs2);
    	
    	intersection = createIntersection(cons);//new GeoPoint3D(cons);
  
    	setInputOutput(new GeoElement[] {(GeoElement) cs1,(GeoElement) cs2}, new GeoElement[] {intersection});

 
    }
 
    
    /**
     * set cs1 and cs2 as the 2 coord sys on inputs
     * if one is 1D and the second 2D, 1D must be taken for cs1
     * @param cs1
     * @param cs2
     */
    protected void setCoordSys(GeoElement cs1, GeoElement cs2){
    	
    	this.cs1 = cs1;
    	this.cs2 = cs2;
    }




    /**
     * return new intersection (default is 3D point)
     * @param cons 
     * @return new intersection
     */
    protected GeoElement3D createIntersection(Construction cons){
    	
    	return new GeoPoint3D(cons);
    	
    }
    
    
    
    
    /**
     * return the first coord sys
     * @return the first coord sys
     */
    GeoElement getCS1() {
        return cs1;
    }
    
    /**
     * return the second coord sys
     * @return the second coord sys
     */   
    GeoElement getCS2() {
        return cs2;
    }
    
    
    /**
     * return the intersection
     * @return the intersection
     */   
    public GeoElement3D getIntersection() {
        return intersection;
    }
   
    
    
    

    ///////////////////////////////////////////////
    // COMPUTE
    
    
    /**
     * sets the output to "undefined" if inputs are not defined
     * @return if the output is defined
     */
    protected boolean outputIsDefined() {
	    
    	if (!((GeoElement) cs1).isDefined() || !((GeoElement) cs2).isDefined()){
    		intersection.setUndefined();
    		return false;
    	}
    	
    	return true;
    }

    
    

	public String getClassName() {
    	
    	return "AlgoIntersectCoordSys";
	}

	
	
	
    final public String toString() {

    	return app.getPlain(getIntersectionTypeString(),getCS1().getLabel(),getCS2().getLabel());
       
    } 
    
    abstract protected String getIntersectionTypeString();
  
 

}
