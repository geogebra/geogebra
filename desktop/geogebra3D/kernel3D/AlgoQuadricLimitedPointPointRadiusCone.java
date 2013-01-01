package geogebra3D.kernel3D;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.kernel.kernelND.GeoQuadricNDConstants;


/**
 * Algo for cylinder between two end points and given radius.
 * @author mathieu
 *
 */
public class AlgoQuadricLimitedPointPointRadiusCone extends AlgoQuadricLimitedPointPointRadius {


	/**
	 * 
	 * @param c
	 * @param labels
	 * @param origin
	 * @param secondPoint
	 * @param r
	 */
	public AlgoQuadricLimitedPointPointRadiusCone(Construction c, String[] labels, GeoPointND origin, GeoPointND secondPoint, NumberValue r) {
		super(c, labels, origin, secondPoint, r, GeoQuadricNDConstants.QUADRIC_CONE);
		
	}
	
	@Override
	protected void createEnds(){
		AlgoQuadricEnds algo2 = new AlgoQuadricEnds(cons, getQuadric());
		cons.removeFromConstructionList(algo2);
		bottom = algo2.getSection1();
		top = null;

	}
	
	@Override
	protected void setOutput(){
		setOutput(new GeoElement[] {getQuadric(),getQuadric().getBottom(),getQuadric().getSide()});
	}

	@Override
	protected void setQuadric(Coords o1, Coords o2, Coords d, double r, double min, double max){
		getQuadric().setCone(o2,d,r/max, -max, 0);
	}

	

	@Override
	public Commands getClassName() {
        return Commands.Cone;
    }

	// TODO Consider locusequability
	
}
