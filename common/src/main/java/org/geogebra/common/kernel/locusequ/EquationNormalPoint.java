/**
 * 
 */
package org.geogebra.common.kernel.locusequ;

import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.locusequ.arith.EquationExpression;

/**
 * @author sergio
 * Auxiliary point for defining a normal vector.
 * 
 * Given v = a-b = (a.x-b.x,a.y-b.y) a normal vector n would be
 * n=(a.y-b.y, b.x-a.x)=(a.y-b.y, (-a.x)-(-b.x)) so a "normal" point for
 * a=(a.x,a.y) would be (a.y,-a.x).
 */
public class EquationNormalPoint extends EquationPoint implements
		EquationAuxiliaryElement {

    /**
     * Original point.
     */
    EquationPoint point;
    
    /**
     * General Constructor.
     * @param point original point.
     */
    public EquationNormalPoint(final EquationPoint point) {
        super();
        this.point = point;
    }
    
    @Override
	public boolean isIndependent() {
        return this.point.isIndependent();
    }

    @Override
	public EquationExpression getXExpression() {
        return this.point.getYExpression();
    }

    @Override
	public EquationExpression getYExpression() {
        return this.point.getXExpression().getOpposite();
    }

    @Override
	public EquationExpression getZExpression() {
        return this.point.getZExpression();
    }

    @Override
	public GeoPoint getPoint() {
        return this.point.getPoint();
    }

    @Override
    public void getIndexesFrom(EquationPoint newPoint) {
        // Do nothing. These points are auxiliar.
    }

    @Override
    public void fixX(double value) {
        this.point.fixY(value);
    }

    @Override
    public void fixY(double value) {
        this.point.fixX(-value);
    }
}
