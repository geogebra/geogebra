/**
 * 
 */
package org.geogebra.common.kernel.locusequ.arith;

/**
 * @author sergio
 *
 */
public abstract class EquationBinaryOperator extends EquationOperator {

    /**
     * Creates a new binary operator given two parameters.
     * @param expr1 for the first parameter.
     * @param expr2 for the secon parameter.
     */
    public EquationBinaryOperator(final EquationExpression expr1, final EquationExpression expr2) {
    	super(expr1, expr2);
    }
    
    /**
     * Returns first expression.
     * @return expression
     */
    public EquationExpression getFirstExpression() {
        return this.exprs[0];
    }
    
    /**
     * Returns second expression.
     * @return expression
     */
    public EquationExpression getSecondExpression(){
        return this.exprs[1];
    }
    
    /**
     * Subclasses must implement this method representing current operator for doubles.
     * @param a value for first expression
     * @param b value for second expression
     * @return value for the operation (a `op` b).
     */
    protected abstract double operation(double a, double b);

    @Override
    protected double computeValueImpl() {
        return this.operation(this.getFirstExpression().computeValueImpl(),
                              this.getSecondExpression().computeValueImpl());
    }

}
