/**
 * 
 */
package geogebra.common.kernel.locusequ;

import geogebra.common.kernel.algos.AlgoLocus;
import geogebra.common.kernel.geos.GeoPoint2;

/**
 * @author sergio
 *
 * {@link EquationDependentPoint} represents any non-free point that is not
 * the tracer of a {@link AlgoLocus}. 
 */
public class EquationDependentPoint extends EquationSymbolicPoint {

	private GeoPoint2 p;

    /**
     * @param v Starting value for the coordinates.
     * @param p point to be represented.
     */
    public EquationDependentPoint(int v, GeoPoint2 p) {
        super(v);
        this.p = p;
    }
    
    @Override
    protected String getId() {
        return DEPENDENT_POINT_ID;
    }

    @Override
	public GeoPoint2 getPoint() {
        return this.p;
    }
}
