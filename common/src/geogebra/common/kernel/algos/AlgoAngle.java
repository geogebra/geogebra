package geogebra.common.kernel.algos;

import geogebra.common.euclidian.draw.DrawAngle;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.geos.GeoAngle;
import geogebra.common.kernel.kernelND.GeoPointND;

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
	 * create a new GeoAngle with interval as default angle
	 * @param cons construction
	 * @return new GeoAngle
	 */
	protected GeoAngle newGeoAngle(Construction cons) {
		return GeoAngle.newAngleWithDefaultInterval(cons);
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
	
	/**
	 * @param drawCoords coords (center, v1, v2) for drawing
	 */
	public boolean getCoordsInD3(Coords[] drawCoords){
		drawCoords[0] = Coords.O;
		drawCoords[1] = Coords.VX;
		drawCoords[2] = Coords.VY;
		
		return true;
	}
	
    /**
     * 
     * @param vertex start point
     * @return true if vertex is not correct center for drawing the angle
     */
    static final protected boolean centerIsNotDrawable(GeoPointND vertex){
    	return vertex == null || !vertex.isDefined() || vertex.isInfinite();
    }

}
