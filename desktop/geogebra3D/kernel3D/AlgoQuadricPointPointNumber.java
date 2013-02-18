package geogebra3D.kernel3D;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.GeoPointND;

/**
 * @author ggb3D
 *
 */
public abstract class AlgoQuadricPointPointNumber extends AlgoQuadricPointNumber {
	
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
	
	
	@Override
	protected Coords getDirection(){
		return ((GeoPointND) getSecondInput()).getInhomCoordsInD(3).sub(getOrigin().getInhomCoordsInD(3));
	}
	
    @Override
	final public String toString(StringTemplate tpl) {
    	return loc.getPlain(getPlainName(),getOrigin().getLabel(tpl),getSecondInput().getLabel(tpl),
    			getNumber().getLabel(tpl));

    }
	

}
