/**
 * 
 */
package geogebra.common.kernel.locusequ;

import geogebra.common.kernel.advanced.AlgoLocus;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.locusequ.arith.EquationSpecialSymbolicValue;

/**
 * @author sergio
 * EquationSpecialSymbolicPoint represents a special symbolic point,
 * defined as the locusPoint in a {@link AlgoLocus}.
 */
public class EquationSpecialSymbolicPoint extends EquationSymbolicPoint {

	private GeoPoint p;
    /**
     * @param v Starting value for the coordinates.
     * @param p The point to represent.
     */
    public EquationSpecialSymbolicPoint(int v, final GeoPoint p) {
        super(v);
        this.x = new EquationSpecialSymbolicValue(v+0);
        this.y = new EquationSpecialSymbolicValue(v+1);
        this.z = new EquationSpecialSymbolicValue(v+2);
        this.p = p;
    }

    @Override
    protected String getId() {
        return SPECIAL_SYMBOLIC_ID;
    }

    @Override
	public GeoPoint getPoint() {
        return this.p;
    }

    @Override
    public void fixX(double value) {
        // Do nothing
    }

    @Override
    public void fixY(double value) {
        // Do nothing
    }

    @Override
    public boolean isFixable() {
        return false;
    }

}
