/**
 * 
 */
package org.geogebra.common.kernel.locusequ;

import static org.geogebra.common.kernel.locusequ.arith.EquationArithHelper.div;
import static org.geogebra.common.kernel.locusequ.arith.EquationArithHelper.sum;

import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.locusequ.arith.EquationExpression;
import org.geogebra.common.kernel.locusequ.arith.EquationNumericValue;

/**
 * @author sergio
 * Represents the midpoint of other two.
 */
public class EquationMidpoint extends EquationSymbolicPoint implements EquationAuxiliaryElement {

    private EquationPoint a, b;
    
    /**
     * @param a first point.
     * @param b second point.
     */
    public EquationMidpoint(final EquationPoint a, final EquationPoint b) {
        super();
        this.a = a;
        this.b = b;
    }

    @Override
    public GeoPoint getPoint() {
        return a.getPoint(); // non-sense at all, but hey, it is an EquationAuxiliarElement.
    }

    @Override
    protected String getId() {
        return null; // non-sense at all, but hey, it is an EquationAuxiliarElement.
    }

    @Override
    public EquationExpression getXExpression() {
        if(this.x == null) {
            this.x = div(sum(this.a.getXExpression(), this.b.getXExpression()), EquationNumericValue.from(2));
        }
        return this.x;
    }

    @Override
    public EquationExpression getYExpression() {
        if(this.y == null) {
            this.y = div(sum(this.a.getYExpression(), this.b.getYExpression()), EquationNumericValue.from(2));
        }
        return this.y;
    }

    @Override
    public EquationExpression getZExpression() {
        if(this.z == null) {
            this.z = div(sum(this.a.getZExpression(), this.b.getZExpression()), EquationNumericValue.from(2));
        }
        return this.z;
    }

    @Override
    public boolean isIndependent() {
        return this.a.isIndependent() && this.b.isIndependent();
    }
}
