/**
 * 
 */
package org.geogebra.common.kernel.locusequ.arith;

import org.geogebra.common.kernel.locusequ.EquationTranslator;

/**
 * @author sergio
 * Represents the product of two expressions.
 */
public class EquationProductOperator extends EquationBinaryOperator {


    /**
     * Represents the product of expr1 and expr2.
     * @param expr1 one member of the product.
     * @param expr2 the other member of the product.
     */
    public EquationProductOperator(final EquationExpression expr1, final EquationExpression expr2) {
        super(expr1, expr2);
    }

    @Override
    public boolean isProduct() {
        return true;
    }

    @Override
    public long toLong() {
        
        return this.getFirstExpression().toLong() * this.getSecondExpression().toLong();
    }

    @Override
    public String toString() {
        return "("+this.getFirstExpression().toString()+"*"+this.getSecondExpression().toString()+")";
    }

    @Override
    protected double operation(double a, double b) {
        return a*b;
    }

	@Override
	protected <T> T translateImpl(EquationTranslator<T> translator) {
		return translator.product(this.getFirstExpression().translate(translator),
								  this.getSecondExpression().translate(translator));
	}
}
