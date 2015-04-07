package org.geogebra.common.kernel.locusequ.arith;

import org.geogebra.common.kernel.locusequ.EquationList;

/**
 * Represents an equations. It differs from {@link org.geogebra.common.kernel.arithmetic.Equation}
 * since its polynomials' terms' variables are just strings whose
 * variables' names can only be one-character long.
 * @author sergio
 *
 */
public class Equation {

	private EquationExpression equ;
	private boolean algebraic;

	/**
	 * Simple constructor.
	 * @param equ expression to be "equated".
	 */
	public Equation(final EquationExpression equ) {
		this.equ = equ;
	}
	
	/**
	 * Checks if the element producing this equation is or not algebraic.
	 * It is important to distinguish between the equation algebraicity
	 * (they are always algebraic, otherwise they cannot be computed),
	 * and the algebraicity of the element producing the equation.
	 * 
	 * Example: a line and a segment of that line would produce
	 * the same equation, but the former is algebraic and the latter
	 * is not.
	 * @return algebraicity of current element.
	 */
	public boolean isAlgebraic() {
        return this.algebraic;
    }
    
	/**
	 * Sets algebraicity of current element.
	 * @param algebraic indicates algebraicity.
	 */
    protected void setAlgebraic(boolean algebraic) { this.algebraic = algebraic; }
    
    /**
     * Returns an {@link EquationList} containing only this {@link Equation}.
     * @return a list with only one equation.
     */
    public EquationList toList() {
    	EquationList list = new EquationList(1);
    	list.add(this);
    	return list;
    }
    
    /**
     * Accesses undelying expression.
     * @return an expression.
     */
    public EquationExpression getExpression() {
    	return this.equ;
    }
    
    @Override
    public String toString() {
    	return this.getExpression().toString();
    }
}
