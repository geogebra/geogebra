package geogebra3D.kernel3D;

import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.Matrix.Coords;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.kernel.kernelND.GeoPointND;
import geogebra.kernel.kernelND.GeoVectorND;

/**
 * @author ggb3D
 *
 */
public class AlgoQuadricPointVectorNumber extends AlgoQuadricPointNumber {
	
	/**
	 * @param c construction
	 * @param label 
	 * @param origin 
	 * @param direction 
	 * @param r 
	 */
	public AlgoQuadricPointVectorNumber(Construction c, String label, GeoPointND origin, GeoVectorND direction, NumberValue r, AlgoQuadricComputer computer) {
		super(c,label,origin,(GeoElement) direction, r,computer);
	}
	
	protected Coords getDirection(){
		return ((GeoVectorND) getSecondInput()).getCoordsInD(3);
	}
	
    final public String toString() {
    	return app.getPlain(getClassName()+"FromQuadricPointAVectorBNumberC",getOrigin().getLabel(),getSecondInput().getLabel(),getNumber().getLabel());

    }




	


}
