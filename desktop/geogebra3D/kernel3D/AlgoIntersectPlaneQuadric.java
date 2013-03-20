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
import geogebra.common.kernel.kernelND.GeoQuadricND;
import geogebra.common.kernel.kernelND.GeoSegmentND;




/**
 *
 * @author  ggb3D
 * 
 */
public class AlgoIntersectPlaneQuadric extends AlgoElement3D {

	
	//inputs
	/** plane */
	protected GeoPlane3D plane;
	/** second coord sys */
	protected GeoQuadricND quadric;
	
	//output
	/** intersection */
	protected GeoConic3D conic;


    /** Creates new AlgoIntersectLinePlane 
     * @param cons the construction
     * @param label name of point
     * @param plane plane
     * @param quadric quadric
     */    
    AlgoIntersectPlaneQuadric(Construction cons, String label, GeoPlane3D plane, GeoQuadricND quadric) {

    	this(cons, plane, quadric);
    	conic.setLabel(label);
    	
 
 
    }
    
 
    AlgoIntersectPlaneQuadric(Construction cons, GeoPlane3D plane, GeoQuadricND quadric) {

    	super(cons);


    	this.plane = plane;
    	this.quadric = quadric;

    	conic = newConic(cons);

    	conic.setIsIntersection(true); //should be called before setDependencies (in setInputOutput)
  
    	//end
    	end();
    	
    	//compute();
 
    }
    
    
    /**
     * end of contructor for this algo
     */
	protected void end(){
    	setInputOutput(new GeoElement[] {plane,quadric}, new GeoElement[] {conic});
    }
    
    

    /**
     * 
     * @param cons construction
     * @return new conic for intersection
     */
    protected GeoConic3D newConic(Construction cons){
    	return new GeoConic3D(cons);
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
    	
    	intersectPlaneQuadric(plane, quadric, conic);
    
    }
    
    public static void intersectPlaneQuadric(GeoPlane3D inputPlane, GeoQuadricND inputQuad, GeoConic3D outputConic) {
    	 
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
		return Commands.IntersectPath;
	}
	
    @Override
	final public String toString(StringTemplate tpl) {
        StringBuilder sb = new StringBuilder();

        sb.append(loc.getPlain("IntersectionCurveOfAB",plane.getLabel(tpl),quadric.getLabel(tpl)));
        
        return sb.toString();
    }   
    

	protected void setStyle(GeoSegmentND segment) {
		//TODO:  set styles in somewhere else
		
		//segment.setObjColor(Color.orange);
	}

	// TODO Consider locusequability

 

}
