/**
 * 
 */
package org.geogebra.common.kernel.locusequ.arith;

import org.geogebra.common.kernel.locusequ.EquationPoint;

/**
 * @author sergio
 * Represents the third coordinate of a point.
 */
public class EquationThirdCoordinateValue extends EquationCoordinateValue {

    /**
     * @param point
     */
    public EquationThirdCoordinateValue(final EquationPoint point) {
        super(point);
    }

    @Override
    protected EquationExpression getOriginalExpression() {
        return this.getPoint().getZExpression();
    }
}
