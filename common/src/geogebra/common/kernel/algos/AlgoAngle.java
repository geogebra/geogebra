package geogebra.common.kernel.algos;

import geogebra.common.euclidian.draw.DrawAngle;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Matrix.Coords;

/**
 * Abstract class for all angle algos
 * 
 * @author mathieu
 *
 */
public abstract class AlgoAngle extends AlgoElement{

	/**
	 * default constructor
	 * @param c construction
	 */
	public AlgoAngle(Construction c) {
		super(c);
	}
	
	/**
	 * Creates new algorithm
	 * @param c construction
	 * @param addToConstructionList true to add this to construction list
	 */
	protected AlgoAngle(Construction c, boolean addToConstructionList) {
		super(c, addToConstructionList);
	}
	
	/**
	 * 
	 * @return normal vector
	 */
	public Coords getVn() {
		return Coords.VZ;
	}
	
	
	
	/**
	 * update draw info for 2D drawable
	 * @param m angle apex
	 * @param firstVec base line direction
	 * @param drawable 2D drawable
	 * @return true if visible
	 */
	public abstract boolean updateDrawInfo(double[]m,double[] firstVec, DrawAngle drawable);

}
