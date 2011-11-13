/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoJoinPointsSegment
 *
 * Created on 21. August 2003
 */

package geogebra3D.kernel3D;

import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;
import geogebra.kernel.Matrix.CoordMatrixUtil;
import geogebra.kernel.Matrix.Coords;
import geogebra.kernel.kernelND.GeoLineND;




/**
 *
 * @author  ggb3D
 * @version 
 * 
 * Calculate the GeoPoint3D intersection of two coord sys (eg line and plane).
 * 
 */
public class AlgoIntersectCS1D1D extends AlgoIntersectCoordSys {

	


    /** Creates new AlgoIntersectLinePlane 
     * @param cons the construction
     * @param label name of point
     * @param cs1 first coord sys
     * @param cs2 second coord sys
     */    
    public AlgoIntersectCS1D1D(Construction cons, String label, GeoLineND cs1, GeoLineND cs2) {

    	super(cons,label, (GeoElement) cs1, (GeoElement) cs2);
 
    }
    
 

    
    
    
    
    ///////////////////////////////////////////////
    // COMPUTE
    

    protected void compute(){
    	
    	if (!outputIsDefined())
    		return;
    	
    	GeoLineND line1 = (GeoLineND) getCS1();
    	GeoLineND line2 = (GeoLineND) getCS2();
    	
    	Coords o1 = line1.getPointInD(3, 0);
    	Coords d1 = line1.getPointInD(3, 1).sub(o1);
    	Coords o2 = line2.getPointInD(3, 0);
       	Coords d2 = line2.getPointInD(3, 1).sub(o2);
           	

    	Coords[] project = CoordMatrixUtil.nearestPointsFromTwoLines(
    			o1,d1,o2,d2
    	);
    	
    	GeoPoint3D p = (GeoPoint3D) getIntersection();
    	
    	if (project==null)
    		p.setUndefined(); //TODO infinite point
    	else if (project[0].equalsForKernel(project[1], Kernel.STANDARD_PRECISION)){
    		
    		double t1 = project[2].get(1); //parameter on line 1
       		double t2 = project[2].get(2); //parameter on line 2
       	    		
    		if (t1 > line1.getMinParameter()
    				&& t1 < line1.getMaxParameter() 				
    				&& t2 > line2.getMinParameter()
    				&& t2 < line2.getMaxParameter()
    				)   		
    			p.setCoords(project[0]);
    		else
    			p.setUndefined();
    	}
    	else
    		p.setUndefined();
    	
    }
  
    
    
    
    


	

	protected String getIntersectionTypeString(){
		return "IntersectionPointOfAB";
	}
  
 

}
