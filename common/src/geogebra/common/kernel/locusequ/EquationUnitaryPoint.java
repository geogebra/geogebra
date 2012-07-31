/**
 * 
 */
package geogebra.common.kernel.locusequ;

import static geogebra.common.kernel.locusequ.arith.EquationArithHelper.div;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.locusequ.arith.EquationExpression;
import geogebra.common.kernel.locusequ.arith.EquationNumericValue;
/**
 * @author sergio
 * Given an {@link EquationPoint} returns another one prepared for a vector.
 * 
 * Given v = a - b, v = (a.x - b.x, a.y - b.y),
 * vu = ((a.x - b.x)/|v|, (a.y - b.y)/|v|)
 */
public class EquationUnitaryPoint extends EquationPoint implements EquationAuxiliaryElement {

    private EquationPoint point;
    private EquationExpression module;

    /**
     * Creates a point and the module of a vector returns a point prepared for
     * creating an unitary vector.
     * @param p a {@link EquationPoint}
     * @param mod the {@link EquationExpression} for the module of the vector.
     */
    public EquationUnitaryPoint(final EquationPoint p, final EquationExpression mod) {
        super();
        this.point = p;
        this.module = mod;
    }
    
    /* (non-Javadoc)
     * @see geogebra.kernel.locusequ.EquationPoint#getXExpression()
     */
    @Override
    public EquationExpression getXExpression() {
        return div(this.point.getX(), this.module);
    }

    /* (non-Javadoc)
     * @see geogebra.kernel.locusequ.EquationPoint#getYExpression()
     */
    @Override
    public EquationExpression getYExpression() {
        return div(this.point.getY(), this.module);
    }

    /* (non-Javadoc)
     * @see geogebra.kernel.locusequ.EquationPoint#getZExpression()
     */
    @Override
    public EquationExpression getZExpression() {
        return EquationNumericValue.from(1);
    }

    /* (non-Javadoc)
     * @see geogebra.kernel.locusequ.EquationPoint#getPoint()
     */
    @Override
    public GeoPoint getPoint() {
        return this.point.getPoint(); // This is an auxiliary element!
    }

    /* (non-Javadoc)
     * @see geogebra.kernel.locusequ.EquationPoint#getIndexesFrom(geogebra.kernel.locusequ.EquationPoint)
     */
    @Override
    public void getIndexesFrom(EquationPoint newPoint) {
        // Nothing to do. Auxiliar element.
    }

    /* (non-Javadoc)
     * @see geogebra.kernel.locusequ.EquationPoint#fixX(double)
     */
    @Override
    public void fixX(double value) {
        // Nothing to do. Auxiliar element.
    }

    /* (non-Javadoc)
     * @see geogebra.kernel.locusequ.EquationPoint#fixY(double)
     */
    @Override
    public void fixY(double value) {
        // Nothing to do. Auxiliar element.
    }
}
