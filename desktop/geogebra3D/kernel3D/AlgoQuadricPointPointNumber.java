package geogebra3D.kernel3D;

import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.Matrix.Coords;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.kernel.kernelND.GeoPointND;

/**
 * @author ggb3D
 *
 */
public class AlgoQuadricPointPointNumber extends AlgoQuadricPointNumber {
	
	/**
	 * @param c construction
	 * @param label 
	 * @param origin 
	 * @param secondPoint 
	 * @param r 
	 * @param computer 
	 */
	public AlgoQuadricPointPointNumber(Construction c, String label, GeoPointND origin, GeoPointND secondPoint, NumberValue r, AlgoQuadricComputer computer) {
		super(c,label,origin,(GeoElement) secondPoint,r,computer);
	}
	
	
	protected Coords getDirection(){
		return ((GeoPointND) getSecondInput()).getCoordsInD(3).sub(getOrigin().getCoordsInD(3));
	}
	
    final public String toString() {
    	return app.getPlain(getClassName()+"FromQuadricPointsABNumberC",getOrigin().getLabel(),getSecondInput().getLabel(),getNumber().getLabel());

    }
	

}
