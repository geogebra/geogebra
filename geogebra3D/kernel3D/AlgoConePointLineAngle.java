package geogebra3D.kernel3D;

import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.Matrix.Coords;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.kernel.kernelND.GeoLineND;
import geogebra.kernel.kernelND.GeoPointND;

/**
 * @author ggb3D
 *
 */
public class AlgoConePointLineAngle extends AlgoQuadricPointNumber {
	
	/**
	 * @param c construction
	 * @param label 
	 * @param origin 
	 * @param axis 
	 * @param angle 
	 */
	public AlgoConePointLineAngle(Construction c, String label, GeoPointND origin, GeoLineND axis, NumberValue angle) {
		super(c,label,origin,(GeoElement) axis,angle,new AlgoQuadricComputerCone());
	}
	
	protected Coords getDirection(){
		GeoLineND axis = (GeoLineND) getSecondInput();
		return axis.getPointInD(3, 1).sub(axis.getPointInD(3, 0));
	}
	
    final public String toString() {
    	return app.getPlain("ConeWithCenterAAxisParallelToBAngleC",getOrigin().getLabel(),getSecondInput().getLabel(),getNumber().getLabel());

    }
    
    
	

}
