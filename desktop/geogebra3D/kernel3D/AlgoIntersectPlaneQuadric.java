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

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.Matrix.CoordMatrix;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.GeoSegmentND;




/**
 *
 * @author  ggb3D
 * @version 
 * 
 * Calculate the GeoPoint3D intersection of two coord sys (eg line and plane).
 * 
 */
public class AlgoIntersectPlaneQuadric extends AlgoElement3D {

	
	//inputs
	/** plane */
	private GeoPlane3D plane;
	/** second coord sys */
	private GeoQuadric3D quadric;
	
	//output
	/** intersection */
	private GeoConic3D conic;


    /** Creates new AlgoIntersectLinePlane 
     * @param cons the construction
     * @param label name of point
     * @param cs1 first coord sys
     * @param cs2 second coord sys
     */    
    AlgoIntersectPlaneQuadric(Construction cons, String label, GeoPlane3D plane, GeoQuadric3D quadric) {

    	this(cons, plane, quadric);
    	conic.setLabel(label);
    	
 
 
    }
    
 
    AlgoIntersectPlaneQuadric(Construction cons, GeoPlane3D plane, GeoQuadric3D quadric) {

    	super(cons);


    	this.plane = plane;
    	this.quadric = quadric;
    	
    	conic = new GeoConic3D(cons);
    	conic.setIsIntersection(true); //should be called before setDependencies (in setInputOutput)
  
    	setInputOutput(new GeoElement[] {plane,quadric}, new GeoElement[] {conic});
 
    }


    
    
    
    
    /**
     * return the intersection
     * @return the intersection
     */   
    public GeoConic3D getConic() {
        return conic;
    }
   
    
    
    

    ///////////////////////////////////////////////
    // COMPUTE
    
    
    
    
    @Override
	public void compute(){

    	if (!quadric.isDefined() || !plane.isDefined()){
    		conic.setUndefined();
    		return;
    	}
    	
    	CoordMatrix qm = quadric.getSymetricMatrix();
    	CoordMatrix pm = plane.getParametricMatrix();
    	CoordMatrix pmt = pm.transposeCopy();
    	
    	//sets the conic matrix from plane and quadric matrix
    	CoordMatrix cm = pmt.mul(qm).mul(pm);
    	
    	//Application.debug("pm=\n"+pm+"\nqm=\n"+qm+"\ncm=\n"+cm);
    	
    	conic.setCoordSys(plane.getCoordSys());
    	conic.setMatrix(cm);
    
    }
    
    public static void intersectPlaneQuadric(GeoPlane3D inputPlane, GeoQuadric3D inputQuad, GeoConic3D outputConic) {
    	 
    	CoordMatrix qm = inputQuad.getSymetricMatrix();
    	CoordMatrix pm = inputPlane.getParametricMatrix();
    	CoordMatrix pmt = pm.transposeCopy();
    	
    	//sets the conic matrix from plane and quadric matrix
    	CoordMatrix cm = pmt.mul(qm).mul(pm);
    	
    	//Application.debug("pm=\n"+pm+"\nqm=\n"+qm+"\ncm=\n"+cm);
    	
    	outputConic.setCoordSys(inputPlane.getCoordSys());
    	outputConic.setMatrix(cm);
    }
    
    @Override
	public Commands getClassName() {
		return Commands.IntersectionPaths;
	}
	
    @Override
	final public String toString(StringTemplate tpl) {
        StringBuilder sb = new StringBuilder();

        sb.append(app.getPlain("IntersectionCurveOfAB",plane.getLabel(tpl),quadric.getLabel(tpl)));
        
        return sb.toString();
    }   
    

	protected void setStyle(GeoSegmentND segment) {
		//TODO:  set styles in somewhere else
		
		//segment.setObjColor(Color.orange);
	}

	// TODO Consider locusequability

 

}
