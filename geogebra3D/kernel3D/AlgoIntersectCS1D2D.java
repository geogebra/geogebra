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
import geogebra.kernel.Matrix.CoordSys;
import geogebra.kernel.Matrix.Coords;
import geogebra.kernel.kernelND.GeoCoordSys;
import geogebra.kernel.kernelND.GeoCoordSys2D;
import geogebra.kernel.kernelND.GeoLineND;
import geogebra.kernel.kernelND.GeoPlaneND;
import geogebra.kernel.kernelND.GeoPointND;




/**
 *
 * @author  ggb3D
 * @version 
 * 
 * Calculate the GeoPoint3D intersection of two coord sys (eg line and plane).
 * 
 */
public class AlgoIntersectCS1D2D extends AlgoIntersectCoordSys {


    /** Creates new AlgoIntersectLinePlane 
     * @param cons the construction
     * @param label name of point
     * @param cs1 first coord sys
     * @param cs2 second coord sys
     */    
    public AlgoIntersectCS1D2D(Construction cons, String label, GeoElement cs1, GeoElement cs2) {

    	super(cons,label, (GeoElement) cs1, (GeoElement) cs2);
 
    }

	// sets the 1D coord sys as cs1
    protected void setCoordSys(GeoElement cs1, GeoElement cs2){
  
    	if (cs1 instanceof GeoLineND)
    		super.setCoordSys((GeoElement) cs1, (GeoElement) cs2);
    	else
    		super.setCoordSys((GeoElement) cs2, (GeoElement) cs1);
    	
    }
    

    ///////////////////////////////////////////////
    // COMPUTE
    
    

    

    protected void compute(){



    	GeoLineND line = (GeoLineND) getCS1();
    	GeoCoordSys2D cs2D = (GeoCoordSys2D) getCS2();
    	
    	Coords o = line.getPointInD(3, 0);
    	Coords d = line.getPointInD(3, 1).sub(o);
    	Coords[] project = 
    		o.projectPlaneThruV(cs2D.getCoordSys().getMatrixOrthonormal(), d);
    	
    	GeoPoint3D p = (GeoPoint3D) getIntersection();
    	
    	//check if the point is in the line (segment or half-line)
    	// and if the point is in the region (polygon, ...)
    	if (
    			-project[1].get(3) > line.getMinParameter()
    			&& -project[1].get(3) < line.getMaxParameter() 	
    			&&
 				cs2D.isInRegion(project[1].get(1),project[1].get(2))
 		){
			p.setCoords(project[0]);
		}else
			p.setUndefined();
    	


    }
    
    public static int RESULTCATEGORY_GENERAL = 1;
    public static int RESULTCATEGORY_PARALLEL = 2;
    public static int RESULTCATEGORY_CONTAINED = 3;
    
    //TODO optimize it
    public static int getConfigLinePlane(GeoLineND line, GeoCoordSys2D plane) {
    	if (Kernel.isZero(line.getDirectionInD3().dotproduct(plane.getDirectionInD3()))) {
    		if (Kernel.isZero(line.getPointInD(3, 0).sub(plane.getCoordSys().getOrigin()).dotproduct(plane.getDirectionInD3()))) {
    			return RESULTCATEGORY_CONTAINED;
    		} else {
    			return RESULTCATEGORY_PARALLEL;
    		}	
    	} else {
    		return RESULTCATEGORY_GENERAL;
    	}
    }
    
    /**
     * almost a clone of compute(), just for debugging
     * @param line
     * @param cs2D
     * @return
     */
    public static Coords getIntersectLinePlane(GeoLineND line, GeoCoordSys2D cs2D) {

    	Coords o = line.getPointInD(3, 0);
    	Coords d = line.getPointInD(3, 1).sub(o);
    	Coords[] project = 
    		o.projectPlaneThruV(cs2D.getCoordSys().getMatrixOrthonormal(), d);
    	
    	//check if the point is in the line (segment or half-line)
    	// and if the point is in the region (polygon, ...)
    	if (
    			-project[1].get(3) > line.getMinParameter()
    			&& -project[1].get(3) < line.getMaxParameter() 	
    			&&
 				cs2D.isInRegion(project[1].get(1),project[1].get(2))
 		){
			return project[0];
		}else
			return null;
    }
	
	protected String getIntersectionTypeString(){
		return "IntersectionPointOfAB";
	}
  
 

}
