package org.geogebra.common.kernel.locusequ;

public interface EquationSolution {

	/**
	 * @return a EquationList with the solution.
	 */
	public EquationList getEquationList();
	
	/**
	 * @return true if all symbolic elements in the construction are algebraic.
	 */
	public boolean isAlgebraic();
	
	/**
	 * @return true if all symbolic elements are locusequable.
	 */
	public boolean isLocuEquable();
}
