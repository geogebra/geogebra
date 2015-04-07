/**
 * 
 */
package org.geogebra.common.kernel.locusequ.arith;

import org.geogebra.common.kernel.locusequ.EquationTranslator;

/**
 * @author sergio
 * Represents the subtraction of two expressions.
 */
public class EquationDiffOperator extends EquationBinaryOperator {

    /**
     * Represents expr1 - expr2.
     * @param expr1 first expression.
     * @param expr2 second expression.
     */
    public EquationDiffOperator(final EquationExpression expr1, final EquationExpression expr2) {
        super(expr1, expr2);
    }

    @Override
    public boolean isDiff() {
        return true;
    }

    /*
     * FIXME: implement if translator is kept.
    @Override
    protected <T> T translateImpl(EquationTranslator<T> translator) {
        T a, b;
        
        a = this.getFirstExpression().translate(translator);
        b = this.getSecondExpression().translate(translator);
        
        return translator.diff(a, b);
    }
    */

    @Override
    public long toLong() {
        return this.getFirstExpression().toLong()-this.getSecondExpression().toLong();
    }

    @Override
    public String toString() {
        return "("+this.getFirstExpression().toString()+"-"+this.getSecondExpression().toString()+")";
    }

    @Override
    protected double operation(double a, double b) {
        return a-b;
    }

	@Override
	protected <T> T translateImpl(EquationTranslator<T> translator) {
		return translator.diff(this.getFirstExpression().translate(translator),
							   this.getSecondExpression().translate(translator));
	}

}
