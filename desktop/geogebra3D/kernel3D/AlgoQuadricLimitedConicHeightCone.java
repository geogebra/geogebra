package geogebra3D.kernel3D;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.algos.Algos;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.kernelND.GeoConicND;
import geogebra.common.kernel.kernelND.GeoQuadricNDConstants;


/**
 * Algo for cone from a conic and a height
 * @author mathieu
 *
 */
public class AlgoQuadricLimitedConicHeightCone extends AlgoQuadricLimitedConicHeight {


	/**
	 * 
	 * @param c construction
	 * @param labels labels
	 * @param bottom bottom side
	 * @param height height
	 */
	public AlgoQuadricLimitedConicHeightCone(Construction c, String[] labels, GeoConicND bottom, NumberValue height) {
		super(c, labels, bottom, height, GeoQuadricNDConstants.QUADRIC_CONE);
	}


	@Override
	protected void setQuadric(Coords o1, Coords o2, Coords d, double r, double min, double max){
		//getQuadric().setCone(o1,d,r, min, max);
		getQuadric().setCone(o2,d,r/max, -max, 0);
	}

	

	@Override
	public Commands getClassName() {
        return Commands.Cone;
    }

	// TODO Consider locusequability
	
}
