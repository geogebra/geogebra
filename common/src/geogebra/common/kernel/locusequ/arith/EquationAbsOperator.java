/**
 * 
 */
package geogebra.common.kernel.locusequ.arith;

/**
 * @author sergio
 *
 */
public class EquationAbsOperator extends EquationUnaryOperator {

    /**
     *  Constructor.
     * @param expr whose absolute value you're interested in.
     */
    public EquationAbsOperator(final EquationExpression expr) {
        super(expr);
    }

    @Override
    public boolean isAbs() {
        return true;
    }

    /*
     * FIXME: in case translator is kept.
    @Override
    protected <T> T translateImpl(EquationTranslator<T> translator) {
        T orig = this.getOriginalExpression().translate(translator);
        return translator.abs(orig);
    }
    */

    @Override
    public long toLong() {
        return Math.abs(this.getOriginalExpression().toLong());
    }

    @Override
    public String toString() {
        return new StringBuilder("abs(").
                   append(this.getOriginalExpression().toString()).
                   append(")").toString();
    }

	@Override
	protected double operation(double a) {
		return Math.abs(a);
	}
}
