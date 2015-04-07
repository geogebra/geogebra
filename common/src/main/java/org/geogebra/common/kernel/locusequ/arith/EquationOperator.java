/**
 * 
 */
package org.geogebra.common.kernel.locusequ.arith;

/**
 * @author sergio
 *
 */
public abstract class EquationOperator extends EquationExpression {

	/**
	 * Contains all expressions which this operator is applied to.
	 */
	protected EquationExpression[] exprs;
    
    /**
     * Use this constructor if you are implementing a new operator and
     * you need it.
     */
    protected EquationOperator() {} // Empty constructor for some operators.
    
    /**
     * Constructor for general use.
     * @param expressions for this operator.
     */
    public EquationOperator(final EquationExpression... expressions) {
        this.exprs = expressions;
    }
    
    /**
     * @return current expressions
     */
    public EquationExpression[] getExpressions() {
        return this.exprs;
    }
    
    @Override
    public boolean isOperator() {
        return true;
    }
    @Override
	protected boolean containsSymbolicValuesImpl() {
        
    	for(int i = 0; i < this.exprs.length; i++) {
    		if(this.exprs[i].containsSymbolicValuesImpl()) {
    			return true;
    		}
    	}
    	
    	return false;
    }
}
