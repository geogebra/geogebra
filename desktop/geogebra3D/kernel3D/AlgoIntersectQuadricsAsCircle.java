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
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.Matrix.CoordSys;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.GeoQuadricND;
import geogebra.common.kernel.kernelND.GeoQuadricNDConstants;




/**
 *
 * @author  ggb3D
 * 
 */
public class AlgoIntersectQuadricsAsCircle extends AlgoElement3D {

	
	//inputs
	private GeoQuadricND quadric1, quadric2;
	
	//output
	/** intersection */
	protected GeoConic3D circle;
	


    /** Creates new AlgoIntersectLinePlane 
     * @param cons the construction
     * @param labels names
     * @param quadric1 first quadric
     * @param quadric2 second quadric
     */    
    AlgoIntersectQuadricsAsCircle(Construction cons, String[] labels, GeoQuadricND quadric1, GeoQuadricND quadric2) {

    	this(cons, quadric1, quadric2);
    	circle.setLabel(null);
    	
 
 
    }
    
    /** Creates new AlgoIntersectLinePlane 
     * @param cons the construction
     * @param quadric1 first quadric
     * @param quadric2 second quadric
     */
    AlgoIntersectQuadricsAsCircle(Construction cons, GeoQuadricND quadric1, GeoQuadricND quadric2) {

    	super(cons);


    	this.quadric1 = quadric1;
    	this.quadric2 = quadric2;

    	circle = new GeoConic3D(cons);
    	circle.setCoordSys(new CoordSys(2));

    	circle.setIsIntersection(true); //should be called before setDependencies (in setInputOutput)
  
    	setInputOutput(new GeoElement[] {quadric1,quadric2}, new GeoElement[] {circle});
    	
    	compute();
 
    }
    
    
    
    
    


    
    
    
    
    /**
     * return the intersection
     * @return the intersection
     */   
    public GeoConic3D getConic() {
        return circle;
    }
   
    
    
    

    ///////////////////////////////////////////////
    // COMPUTE
    
    
    
    
    @Override
	public void compute(){

    	if (!quadric1.isDefined() || !quadric2.isDefined()){
    		circle.setUndefined();
    		return;
    	}
    	
    	circle.setDefined();
    	
    	if (quadric1.getType() == GeoQuadricNDConstants.QUADRIC_SPHERE){
    		if (quadric2.getType() == GeoQuadricNDConstants.QUADRIC_SPHERE){
    			// intersect sphere / sphere
        		Coords o1 = quadric1.getMidpoint3D();
        		double r1 = quadric1.getHalfAxis(0);
        		Coords o2 = quadric2.getMidpoint3D();
        		double r2 = quadric2.getHalfAxis(0);
        		
        		
        		//same center
        		if (o1.equalsForKernel(o2)){
        			if (Kernel.isZero(r1) && Kernel.isZero(r2)){
        				//single point
        				circle.setSinglePoint(o1);
        				return;
        			}
        			
        			if (Kernel.isEqual(r1, r2)){
        				//undefined
        				circle.setUndefined();
        				return;
        			}
        			
        			//empty conic
					circle.empty();
					return;
        		}
        		
        		//different centers
        		Coords v = o2.sub(o1);
        		v.calcNorm();
        		double d = v.getNorm();
        		
        		
        		if (Kernel.isGreater(d, r1+r2)){
        			//no intersection : empty
        			circle.empty();
					return;
        		}
        		
        		v = v.mul(1/d);
        		double x = (d+(r1*r1-r2*r2)/d)/2;
        		Coords o = o1.add(v.mul(x));
        		
        		Coords[] vs = v.completeOrthonormal();  	
        		CoordSys coordSys = circle.getCoordSys();
        		coordSys.resetCoordSys();
        		coordSys.addPoint(o);
        		coordSys.addVector(vs[0]);
        		coordSys.addVector(vs[1]);
        		coordSys.makeOrthoMatrix(false, false);
        		circle.setSphereND(new Coords(0,0), Math.sqrt(r1*r1-x*x));
        		return;
        		
        	}
    	}
    	
    	
    	
    	// other cases
    	circle.setUndefined();
    	
    	
    
    }
    
    
    @Override
	public Commands getClassName() {
		return Commands.IntersectCircle;
	}
	
    @Override
	final public String toString(StringTemplate tpl) {
        StringBuilder sb = new StringBuilder();

        sb.append(loc.getPlain("IntersectionCircleOfAB",quadric1.getLabel(tpl),quadric2.getLabel(tpl)));
        
        return sb.toString();
    }   
    


 

}
