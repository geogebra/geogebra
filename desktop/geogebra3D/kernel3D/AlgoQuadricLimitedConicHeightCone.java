package geogebra3D.kernel3D;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.algos.Algos;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoElement;
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
	protected void createTop(){
		AlgoQuadricEndTop algo2 = new AlgoQuadricEndTop(cons, getQuadric());
		cons.removeFromConstructionList(algo2);
		top = algo2.getSection();

	}
	
	@Override
	protected void setOutput(){
		output = new GeoElement[] {getQuadric(),getQuadric().getTop(),getQuadric().getSide()};
	}
	

	@Override
	protected void setQuadric(Coords o1, Coords o2, Coords d, double r, double min, double max){
		//getQuadric().setCone(o1,d,r, min, max);
		getQuadric().setCone(o2,d,r/max, -max, 0);
	}

	

	@Override
	public Algos getClassName() {
		return Algos.AlgoLimitedCone;
	}
	
}
