/**
 * 
 */
package org.geogebra.common.kernel.locusequ.arith;

/**
 * @author sergio
 * Represents a unary operator.
 */
public abstract class EquationUnaryOperator extends EquationOperator{

	/**
     * General constructor.
     * @param expression
     */
    public EquationUnaryOperator(final EquationExpression expression) {
    	super(expression);
    }

    /**
     * @return the expression under the operator.
     */
    public EquationExpression getOriginalExpression() {
    	return this.exprs[0];
    }

    @Override
	protected boolean containsSymbolicValuesImpl() {
        return this.getOriginalExpression().containsSymbolicValuesImpl();
    }
    
    /**
     * All subclasses must implement it representing the operation for a double.
     * @param a the value of original expression.
     * @return current operator applied to a.
     */
    protected abstract double operation(double a);

    @Override
    protected double computeValueImpl() {
        return this.operation(this.getOriginalExpression().computeValue());
    }
}
