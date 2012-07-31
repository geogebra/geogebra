/**
 * 
 */
package geogebra.common.kernel.locusequ.arith;

import geogebra.common.kernel.locusequ.EquationTranslator;

/**
 * @author sergio
 * Represents the square root of a given expression.
 */
public class EquationSqrtOperator extends EquationUnaryOperator {

	/**
	 * Represents the square root of expr.
	 * @param expr to be sqrt'd
	 */
	public EquationSqrtOperator(final EquationExpression expr) {
		super(expr);
	}
	
	/* (non-Javadoc)
	 * @see geogebra.common.kernel.locusequ.arith.EquationUnaryOperator#operation(double)
	 */
	@Override
	protected double operation(double a) {
		return (a < 0) ? -1 : Math.sqrt(a);
	}

	/* (non-Javadoc)
	 * @see geogebra.common.kernel.locusequ.arith.EquationExpression#toLong()
	 */
	@Override
	public long toLong() {
		long value = this.getOriginalExpression().toLong();
		return (long) ((value < 0) ? -1 : Math.ceil(Math.sqrt(value)));
	}

	/* (non-Javadoc)
	 * @see geogebra.common.kernel.locusequ.arith.EquationExpression#toString()
	 */
	@Override
	public String toString() {
		return "sqrt("+this.getOriginalExpression().toString()+")";
	}

	@Override
    public boolean isSqrt() {
        return true;
    }
	
	@Override
	protected <T> T translateImpl(EquationTranslator<T> translator) {
        if(this.getOriginalExpression().containsSymbolicValuesImpl()) {
            return translator.sqrt(this.getOriginalExpression().translate(translator));
        }
        return translator.number(Math.sqrt(this.getOriginalExpression().computeValue()));
    }
}
