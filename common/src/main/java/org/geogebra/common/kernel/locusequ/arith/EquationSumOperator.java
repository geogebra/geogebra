/**
 * 
 */
package org.geogebra.common.kernel.locusequ.arith;

import org.geogebra.common.kernel.locusequ.EquationTranslator;

/**
 * @author sergio
 * Represents the sum of two expressions.
 */
public class EquationSumOperator extends EquationBinaryOperator {

    public EquationSumOperator(EquationExpression expr1, EquationExpression expr2) {
        super(expr1, expr2);
    }

    @Override
    public boolean isSum() {
        return true;
    }

    /*
     * FIXME: in case translator is kept.
    @Override
    protected <T> T translateImpl(EquationTranslator<T> translator) {
        T a,b;
        a = this.getFirstExpression().translate(translator);
        b = this.getSecondExpression().translate(translator);
        return translator.sum(a,b);
    }
    */

    @Override
    public long toLong() {
        return this.getFirstExpression().toLong() + this.getSecondExpression().toLong();
    }

    @Override
    public String toString() {
        return "("+this.getFirstExpression().toString()+"+"+this.getSecondExpression().toString()+")";
    }

    @Override
    protected double operation(double a, double b) {
        return a+b;
    }

	@Override
	protected <T> T translateImpl(EquationTranslator<T> translator) {
		return translator.sum(this.getFirstExpression().translate(translator),
				              this.getSecondExpression().translate(translator));
	}
}
