/**
 * 
 */
package org.geogebra.common.kernel.locusequ.arith;

import org.geogebra.common.kernel.locusequ.EquationTranslator;

/**
 * @author sergio
 * Represent baseExpr ^ expExpr
 */
public class EquationExpOperator extends EquationBinaryOperator {

    /**
     * Represents base ^ exp.
     * @param base base of pow.
     * @param exp base of pow
     */
    public EquationExpOperator(final EquationExpression base,
            final EquationExpression exp) {
        super(base, exp);
    }
    
    /**
     * Alias for getFirstExpression.
     * @return the base.
     */
    public EquationExpression getBase() {
        return super.getFirstExpression();
    }
    
    /**
     * Alias for getSecondExpression.
     * @return the exponent.
     */
    public EquationExpression getExp() {
        return super.getSecondExpression();
    }

    @Override
    public boolean isExp() {
        return true;
    }

    /*
     * FIXME: implement if translator is kept.
    @Override
    protected <T> T translateImpl(EquationTranslator<T> translator) {
        T base = this.getBase().translate(translator);
        long exp = this.getExp().toLong();
        return translator.exp(base,exp);
    }
    */

    @Override
    public long toLong() {
        return (long) Math.pow(this.getBase().toLong(), this.getExp().toLong());
    }

    @Override
    public String toString() {
        return "("+this.getBase().toString()+"^"+this.getExp().toString()+")";
    }

    @Override
    protected double operation(double a, double b) {
        if(a == 0 && b == 0) {
            return Double.NaN;
        }
        return Math.pow(a, b);
    }

	@Override
	protected <T> T translateImpl(EquationTranslator<T> translator) {
		return translator.exp(this.getBase().translate(translator),
				this.getExp().toLong());
	}
}
