/**
 * 
 */
package geogebra.common.kernel.locusequ.arith;

import geogebra.common.kernel.locusequ.EquationTranslator;

/**
 * @author sergio
 * Represent expr1/expr2
 */
public class EquationDivOperator extends EquationBinaryOperator {


    /**
     * Express numerator / denominator.
     * @param numerator of fraction
     * @param denominator of fraction
     */
    public EquationDivOperator(final EquationExpression numerator, final EquationExpression denominator) {
        super(numerator, denominator);
    }
    
    /**
     * Alias for getFirstExpression()
     * @return the numerator.
     */
    public EquationExpression getNumerator() {
        return super.getFirstExpression();
    }
    
    /**
     * Alias for getSecondExpression();
     * @return the denominator.
     */
    public EquationExpression getDenominator() {
        return super.getSecondExpression();
    }

    @Override
    public boolean isDiv() {
        return true;
    }

    /*
     * FIXME: implement if translator is kept.
    @Override
    protected <T> T translateImpl(EquationTranslator<T> translator) {
        T num, denom;
        num = this.getNumerator().translate(translator);
        denom = this.getDenominator().translate(translator);
        
        return translator.div(num, denom);
    }
    */

    @Override
    public long toLong() {
        return (this.getNumerator().toLong() / this.getDenominator().toLong());
    }

    @Override
    public String toString() {
        return "("+this.getNumerator().toString()+"/"+this.getDenominator().toString()+")";
    }

    @Override
    protected double operation(double a, double b) {
        if(b == 0) {
            return Double.NaN;
        }
        return a/b;
    }

	@Override
	protected <T> T translateImpl(EquationTranslator<T> translator) {
		return translator.div(this.getFirstExpression().translate(translator),
							  this.getSecondExpression().translate(translator));
	}
}
