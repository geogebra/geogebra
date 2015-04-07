/**
 * 
 */
package org.geogebra.common.kernel.locusequ;

import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.locusequ.arith.EquationExpression;
import org.geogebra.common.kernel.locusequ.arith.EquationNumericValue;

/**
 * @author sergio
 * 
 * {@link EquationFreePoint} represents an independent point.
 */
public class EquationFreePoint extends EquationPoint {

    /**
     * x coordinate.
     */
    protected EquationNumericValue x;
    
    /**
     * y coordinate.
     */
    protected EquationNumericValue y;
    
    /**
     * z coordinate (only because GeoPoint already has three coordinates).
     */
    protected EquationNumericValue z;

    private GeoPoint p;
    
    /**
     * @param p Free {@link GeoPoint} to take the coordinates from.
     */
    public EquationFreePoint(GeoPoint p) {
        super();
        this.p = p;
        this.x = EquationNumericValue.from(this.p.getX());
        this.y = EquationNumericValue.from(this.p.getY());
        this.z = EquationNumericValue.from(this.p.getZ());
    }

    @Override
	public GeoPoint getPoint() {
        return this.p;
    }

    @Override
	public EquationExpression getXExpression() {
        return x;
    }

    @Override
    public EquationExpression getYExpression() {
        return y;
    }

    @Override
    public EquationExpression getZExpression() {
        return z;
    }
    
    @Override
	public boolean isIndependent() {
        return true;
    }

    @Override
    public void getIndexesFrom(EquationPoint newPoint) {
        // Do nothing, this is suppose to be for symbolic.
    }

    @Override
    public void fixX(double value) {
        this.x = EquationNumericValue.from(value);
    }

    @Override
    public void fixY(double value) {
        this.y = EquationNumericValue.from(value);
    }
}
