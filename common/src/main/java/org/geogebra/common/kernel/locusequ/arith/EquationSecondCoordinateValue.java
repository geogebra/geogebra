/**
 * 
 */
package org.geogebra.common.kernel.locusequ.arith;

import org.geogebra.common.kernel.locusequ.EquationPoint;

/**
 * @author sergio
 * Represents the second coordinate of a point.
 */
public class EquationSecondCoordinateValue extends EquationCoordinateValue {

    /**
     * @param point
     */
    public EquationSecondCoordinateValue(final EquationPoint point) {
        super(point);
    }

    @Override
    protected EquationExpression getOriginalExpression() {
        return this.getPoint().getYExpression();
    }
}
