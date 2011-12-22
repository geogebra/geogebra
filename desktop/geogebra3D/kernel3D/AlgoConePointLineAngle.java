package geogebra3D.kernel3D;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.algos.Algos;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.GeoLineND;
import geogebra.common.kernel.kernelND.GeoPointND;

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
    	return app.getPlain(getPlainName(),getOrigin().getLabel(),getSecondInput().getLabel(),getNumber().getLabel());

    }

	@Override
	final protected String getPlainName() {
		return "ConeWithCenterAAxisParallelToBAngleC";
	}
    
    

	@Override
	public Algos getClassName(){
		return Algos.AlgoConeInfinitePointLineAngle;
	}


}
