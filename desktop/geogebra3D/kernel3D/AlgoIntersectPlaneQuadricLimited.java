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
import geogebra.common.kernel.PathNormalizer;
import geogebra.common.kernel.PathParameter;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.GeoConicND;
import geogebra.common.kernel.kernelND.GeoConicNDConstants;
import geogebra.common.kernel.kernelND.GeoQuadricND;




/**
 *
 * @author  mathieu
 * 
 * 
 */
public class AlgoIntersectPlaneQuadricLimited extends AlgoIntersectPlaneQuadric {

	
	private AlgoIntersectPlaneConic algoBottom, algoTop;

	private GeoPoint3D[] bottomP, topP;

    /** Creates new AlgoIntersectLinePlane 
     * @param cons the construction
     * @param label name of point
     * @param plane plane
     * @param quadric quadric
     */    
    AlgoIntersectPlaneQuadricLimited(Construction cons, String[] labels, GeoPlane3D plane, GeoQuadricND quadric) {

    	super(cons, plane, quadric);
    	
    	
    	//labels 	
    	String conicLabel = null;
    	String[] bottomLabels = null;
    	String[] topLabels = null;
    	if (labels!=null){
    		conicLabel = labels[0];
    		if (labels.length>2){
    			bottomLabels = new String[2];
    			bottomLabels[0] = labels[1];
    			bottomLabels[1] = labels[2];
    	   		if (labels.length>4){
    	   			topLabels = new String[2];
    	   			topLabels[0] = labels[3];
    	   			topLabels[1] = labels[4];
        		}
     
    		}
    	}

		conic.setLabel(conicLabel);
		/*
		GeoElement.setLabels(bottomLabels, bottomP);
		GeoElement.setLabels(topLabels, topP);
		*/
		    	
 
 
    }
    
    @Override
	protected GeoConic3D newConic(Construction cons){
    	return new GeoConic3DPart(cons);
    }
    

    
	@Override
	protected void end(){
		
		
		//algo for intersect points with bottom and top
		boolean oldSilentMode = kernel.isSilentMode();
		kernel.setSilentMode(true);
		algoBottom = new AlgoIntersectPlaneConic(cons);
		algoTop = new AlgoIntersectPlaneConic(cons);
		kernel.setSilentMode(oldSilentMode);
		
		
		//output
		GeoElement[] output = new GeoElement[5];
		output[0] = conic;
		
		bottomP  = new GeoPoint3D[2];
		for(int i = 0 ; i < 2 ; i++){
			bottomP[i] = new GeoPoint3D(cons);
			output[1+i] = bottomP[i];
		}

		topP  = new GeoPoint3D[2];
		for(int i = 0 ; i < 2 ; i++){
			topP[i] = new GeoPoint3D(cons);
			output[3+i] = topP[i];
		}
		
    	//setInputOutput(new GeoElement[] {plane,quadric}, output);
		super.end();
    }
    
   
    
    
    

    ///////////////////////////////////////////////
    // COMPUTE
    
    
    
    
    @Override
	public void compute(){
    	
    	super.compute();

    	
    	GeoQuadric3DLimited ql = (GeoQuadric3DLimited) quadric;

    	 
    	// set part points
    	double[] bottomParameters = setPartPoints(algoBottom, ql.getBottom(), bottomP);
    	double[] topParameters = setPartPoints(algoTop, ql.getTop(), topP);

    	switch (conic.getType()) {
    	case GeoConicNDConstants.CONIC_CIRCLE:
    	case GeoConicNDConstants.CONIC_ELLIPSE:
    		//if some parameters are NaN, force to be in topParameters
    		if (Double.isNaN(bottomParameters[0])){
    			bottomParameters[0] = topParameters[0];
    			bottomParameters[1] = topParameters[1];
    			topParameters[0] = Double.NaN;
    		}

    		//if topParameters are NaN, and not bottomParameters,
    		//set twice the "middle" parameter for topParameters to check the order
    		//App.debug(topParameters[0]+","+bottomParameters[0]);
    		if (Double.isNaN(topParameters[0])){
    			if(!Double.isNaN(bottomParameters[0])){
    				//calc "midpoint" on conic
    				double midParameter = (bottomParameters[0] + bottomParameters[1])/2;
    				PathParameter pp = new PathParameter(midParameter);
    				Coords P = new Coords(3);
    				conic.pathChangedWithoutCheck(P, pp);
    				P = conic.getPoint(P.getX(), P.getY());
    				//check if "midpoint" is on quadric side
    				//App.debug("\n"+P+"\n"+ql.getSide().isInRegion(P));
    				if(ql.getSide().isInRegion(P)){
    					//set "midpoint"
    					topParameters[0] = midParameter;
    				}else{
    					//set symetric "midpoint"
    					topParameters[0] = midParameter+Math.PI;
    					if (midParameter<0){
    						topParameters[0] = midParameter+Math.PI;
    					}else{
    						topParameters[0] = midParameter-Math.PI;
    					}

    				}
    				topParameters[1]=topParameters[0];
    				
    			}else{ //no intersection : check if the plane is not totally outside the quadric
    				//App.debug("\nmidpoint=\n"+ql.getMidpoint3D()+"\nev3=\n"+ql.getEigenvec3D(2));
    				//calc parameter (on quadric axis) of the intersection point between plane and quadrix axis
    				double parameter = -(ql.getMidpoint3D().projectPlaneThruV(plane.getCoordSys().getMatrixOrthonormal(), ql.getEigenvec3D(2))[1]).getZ();
    				//App.debug("parameter="+parameter);
    				//check if paramter is between quadric min and max
    				if (Kernel.isGreater(ql.getMin(), parameter) || Kernel.isGreater(parameter, ql.getMax())){
    					conic.setUndefined();
    					return;
    				}
    			}
    		}
    		break;
    		
    	case GeoConicNDConstants.CONIC_HYPERBOLA:

    		
    		if (Double.isNaN(bottomParameters[0])){
    			if (Double.isNaN(topParameters[0])){ //no intersection with ends of the quadric : hyperbola is completely outside
    				conic.setUndefined();
    			}else if (Kernel.isEqual(topParameters[0], topParameters[1])){ // single point
     				conic.setSinglePoint(topP[0]);
    			}
    		}else if (Kernel.isEqual(bottomParameters[0], bottomParameters[1])){ // single point
 				conic.setSinglePoint(bottomP[0]);
			}
    			
    		break;
    	}

    	
    	// set parameters to conic
    	GeoConic3DPart cp = (GeoConic3DPart) conic;
    	
    	/*
      	App.error(bottomParameters[0]+","+
      			bottomParameters[1]+","+
      			topParameters[0]+","+
      			topParameters[1]);
      			*/
      			
      			
      	//App.debug("\n"+bottomP[0]+"\n"+bottomP[1]+"\n"+topP[0]+"\n"+topP[1]);
      			
    	 

    	/*
    	App.debug(PathNormalizer.infFunction(bottomParameters[0])+","+
    			PathNormalizer.infFunction(topParameters[0])+","+
    			PathNormalizer.infFunction(bottomParameters[1]-2)+","+
    			PathNormalizer.infFunction(topParameters[1]-2));
    			*/

        
      	cp.setParameters(
      			bottomParameters[0], 
      			bottomParameters[1],
      			topParameters[0], 
      			topParameters[1]);
    	
    	
    	
    }


    private double[] setPartPoints(AlgoIntersectPlaneConic algo, GeoConicND c, GeoPoint3D[] points){

    	//check if c is point or undefined
    	if (//c==null
    			//|| 
    			!c.isDefined()
    			|| c.getType()==GeoConicNDConstants.CONIC_EMPTY 
    			//|| c.getType()==GeoConicNDConstants.CONIC_SINGLE_POINT 
    			){
    		return new double[] {Double.NaN, Double.NaN};
    	}
    	
    	//calc points
    	algo.intersect(plane, c, points);
    	
    	//App.debug(points[0].isDefined());
    	
    	if(!points[0].isDefined()){
    		return new double[] {Double.NaN, Double.NaN};
    	}
    	
    	
    	Coords c0 = points[0].getCoordsInD2(conic.getCoordSys());
    	Coords c1 = points[1].getCoordsInD2(conic.getCoordSys());
    	
    	double[] ret = new double[2];

    	if (c0.equalsForKernel(c1) && conic.getType() == GeoConicNDConstants.CONIC_INTERSECTING_LINES){
    		//force compute parameter for the two liness
    		PathParameter pp = new PathParameter();   		
    		conic.lines[0].doPointChanged(c0,pp);
    		ret[0] = PathNormalizer.inverseInfFunction(pp.getT());
    		conic.lines[1].doPointChanged(c1,pp);
    		ret[1] = PathNormalizer.inverseInfFunction(pp.getT())+2;
    	}else{
    		//get parameters to limit the conic
    		PathParameter pp = new PathParameter();
    		conic.pointChanged(c0,pp);
    		ret[0] = pp.getT();
    		conic.pointChanged(c1,pp);
    		ret[1] = pp.getT();
    	}
    	
    	return ret;
    	
    }


    

 

}
