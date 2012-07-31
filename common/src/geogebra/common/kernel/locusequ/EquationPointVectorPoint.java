/**
 * 
 */
package geogebra.common.kernel.locusequ;

import static geogebra.common.kernel.locusequ.arith.EquationArithHelper.sum;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.locusequ.arith.EquationExpression;

/**
 * @author sergio
 * Represents the point resulted from applying a vector to another point.
 */
public class EquationPointVectorPoint extends EquationPoint implements
		EquationAuxiliaryElement {

    private SymbolicVector vector;
    private EquationPoint point;
    private boolean independent;

    /**
     * General constructor.
     * @param vector vector
     * @param point point
     */
    public EquationPointVectorPoint(SymbolicVector vector, EquationPoint point) {
        super();
        this.vector = vector; // Just in case;
        this.point  = point; // Just in case;
        this.independent = this.vector.isIndependent() && this.point.isIndependent();
        this.xExpr = sum(point.getXExpression(), vector.getX());
        this.yExpr = sum(point.getYExpression(), vector.getY());
        this.zExpr = sum(point.getZExpression(), vector.getZ());
    }
    

    
    @Override
	public EquationExpression getXExpression() {
        return xExpr;
    }

    @Override
	public EquationExpression getYExpression() {
        return yExpr;
    }

    @Override
	public EquationExpression getZExpression() {
        return zExpr;
    }

    @Override
	public boolean isIndependent() {
        return this.independent;
    }

    // This is the only EquationPoint that returns a point that it's not true.
    // It is an EquationAuxiliarElement!
    @Override
	public GeoPoint getPoint() {
        return this.point.getPoint();
    }

    @Override
    public void getIndexesFrom(EquationPoint newPoint) {
        // These points are auxiliary.
    }

    @Override
    public void fixX(double value) {
        // Not needed, since it is an auxiliary element
    }

    @Override
    public void fixY(double value) {
        // Not needed, since it is an auxiliary element
    }
}
