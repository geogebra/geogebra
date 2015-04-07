/**
 * 
 */
package org.geogebra.common.kernel.locusequ.arith;

import org.geogebra.common.kernel.locusequ.EquationTranslator;

/**
 * @author sergio
 * Represents the opposite of a given expression.
 */
public class EquationOppositeOperator extends EquationUnaryOperator {

	/**
	 * Creates the opposite of expr.
	 * @param expr opposite.
	 */
	public EquationOppositeOperator(final EquationExpression expr) {
		super(expr);
	}

	/**
     * Alias for getOriginalExpression.
     * @returns the opposite.
     */
    @Override
	public EquationExpression getOpposite() {
        return getOriginalExpression();
    }


    /*
     * FIXME: in case Translator is kept.
    @Override
    protected <T> T translateImpl(EquationTranslator<T> translator) {
        T orig = this.getOriginalExpression().translate(translator);
        return translator.opposite(orig);
    }
    */
    
    @Override
    public boolean isOpposite() {
        return true;
    }

	@Override
	protected double operation(double a) {
		return -a;
	}

	@Override
	public long toLong() {
		return -this.getOriginalExpression().toLong();
	}

	@Override
	public String toString() {
		return "-"+this.getOriginalExpression().toString();
	}

	@Override
	protected <T> T translateImpl(EquationTranslator<T> translator) {
		return translator.opposite(this.getOriginalExpression().translate(translator));
	}
}
