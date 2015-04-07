/**
 * 
 */
package org.geogebra.common.kernel.locusequ.arith;

import org.geogebra.common.kernel.locusequ.EquationTranslator;

/**
 * @author sergio
 * Represents the inverse of an expression.
 */
public class EquationInverseOperator extends EquationUnaryOperator {

	/**
	 * Represents the inverse of expr.
	 * @param expr to be inversed.
	 */
	public EquationInverseOperator(final EquationExpression expr) {
		super(expr);
	}
	
	@Override
	public EquationExpression getInverse() {
		return getOriginalExpression();
	}
	

	/*
	 * FIXME: implement if translator is kept.
    @Override
    protected <T> T translateImpl(EquationTranslator<T> translator) {
        T orig = this.getOriginalExpression().translate(translator);
        return translator.inverse(orig);
    }
    */
	
	@Override
	protected double operation(double a) {
		if(a == 0) {
			return Double.NaN;
		}
		
		return 1/a;
	}
	
	@Override
	public long toLong() {
		long value = getInverse().toLong();
		if(value == 0) {
			return Long.MAX_VALUE;
		} else if(value == 1) {
			return 1;
		} else {
			return 0;
		}
	}
	
	@Override
	public String toString() {
        return "(1/"+this.getOriginalExpression().toString()+ ")";
	}
	
    @Override
    public boolean isInverse() {
        return true;
    }

	@Override
	protected <T> T translateImpl(EquationTranslator<T> translator) {
		return translator.inverse(this.getOriginalExpression().translate(translator));
	}
}
