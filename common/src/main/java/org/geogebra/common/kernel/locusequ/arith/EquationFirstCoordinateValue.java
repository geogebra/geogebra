/**
 * 
 */
package org.geogebra.common.kernel.locusequ.arith;

import org.geogebra.common.kernel.locusequ.EquationPoint;

/**
 * @author sergio
 * Represents the first coordinate of a point.
 */
public class EquationFirstCoordinateValue extends EquationCoordinateValue {

    /**
     * @param point
     */
    public EquationFirstCoordinateValue(final EquationPoint point) {
        super(point);
    }

    @Override
    protected EquationExpression getOriginalExpression() {
        return this.getPoint().getXExpression();
    }
}
