package org.geogebra.common.kernel.locusequ.arith;

/**
 * Represents a Term in the equation.
 * It is only in the middle so it can set isTerm to true.
 * @author sergio
 *
 */
public abstract class EquationValue extends EquationExpression {

	@Override
    public boolean isTerm() {
        return true;
    }
}
