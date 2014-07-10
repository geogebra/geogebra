package geogebra.common.geogebra3D.kernel3D.algos;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.Matrix.CoordSys;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.GeoConicNDConstants;
import geogebra.common.kernel.kernelND.GeoCoordSys2D;
import geogebra.common.kernel.kernelND.GeoDirectionND;
import geogebra.common.kernel.kernelND.GeoPointND;

public class AlgoEllipseHyperbolaFociPoint3DOriented extends AlgoEllipseHyperbolaFociPoint3D{

	public AlgoEllipseHyperbolaFociPoint3DOriented(Construction cons, String label,
			GeoPointND A, GeoPointND B, GeoPointND C, GeoDirectionND orientation, 
            final int type) {
		super(cons, label, A, B, C, orientation, type);

	}
	
	private GeoDirectionND orientation;
	
    @Override
	protected void setOrientation(GeoDirectionND orientation){
    	this.orientation = orientation;
    }
	
    @Override
	protected void setInput(){
    	input = new GeoElement[4];
        input[0] = (GeoElement) A;
        input[1] = (GeoElement) B;
        input[2] = (GeoElement) C;
        input[3] = (GeoElement) orientation;
    }
    
    
    @Override
	protected boolean setCoordSys(CoordSys cs, Coords Ac, Coords Bc, Coords Cc){
    	
    	Coords vn = orientation.getDirectionInD3();
    	
    	if (vn.isZero()){
    		return false;
    	}
    	
    	Coords d1 = Bc.sub(Ac);
       
    	// check if plane (ABC) and vn are orthogonal
    	if (!Kernel.isZero(d1.dotproduct(vn))){
    		return false;
    	}
 
    	if (!Kernel.isZero(Cc.sub(Ac).dotproduct(vn))){
    		return false;
    	}
    	
    	// set the coord sys
    	cs.addPoint(Ac);
       	cs.addVector(d1);
       	cs.addVector(vn.crossProduct4(d1));
       	
       	return cs.makeOrthoMatrix(false, false);
    }
    
    

    
    
    @Override
	public String toString(StringTemplate tpl) {
 
    	//direction is plane
    	if (orientation instanceof GeoCoordSys2D){
    		if (type == GeoConicNDConstants.CONIC_HYPERBOLA){
    			return loc.getPlain("HyperbolaWithFociABPassingThroughCParallelToD",A.getLabel(tpl),
                		B.getLabel(tpl),C.getLabel(tpl),orientation.getLabel(tpl));
    		}
    		return loc.getPlain("EllipseWithFociABPassingThroughCParallelToD",A.getLabel(tpl),
            		B.getLabel(tpl),C.getLabel(tpl),orientation.getLabel(tpl));
    	}
    	
    	
     	//direction is line
    	if (type == GeoConicNDConstants.CONIC_HYPERBOLA){
    		return loc.getPlain("HyperbolaWithFociABPassingThroughCPerpendicularToD",A.getLabel(tpl),
            		B.getLabel(tpl),C.getLabel(tpl),orientation.getLabel(tpl));
    	}
    	return loc.getPlain("EllipseWithFociABPassingThroughCPerpendicularToD",A.getLabel(tpl),
        		B.getLabel(tpl),C.getLabel(tpl),orientation.getLabel(tpl));
    	
    }
}
