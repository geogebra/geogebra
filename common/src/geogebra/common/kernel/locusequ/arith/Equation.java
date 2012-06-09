package geogebra.common.kernel.locusequ.arith;

import geogebra.common.kernel.locusequ.EquationList;

/**
 * Represents an equations. It differs from {@link geogebra.common.kernel.arithmetic.Equation}
 * since its polynomials' terms' variables are just strings whose
 * variables' names can only be one-character long.
 * @author sergio
 *
 */
public class Equation {

	private EquationExpression equ;
	private boolean algebraic;

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
	 * @param algebraic
	 */
    protected void setAlgebraic(boolean algebraic) { this.algebraic = algebraic; }
    
    /**
     * Returns an {@link EquationList} containing only this {@link Equation}.
     * @return
     */
    public EquationList toList() {
    	EquationList list = new EquationList(1);
    	list.add(this);
    	return list;
    }
}
