package org.geogebra.common.kernel.arithmetic;

/**
 * 
 * Interface for objects determined by single equation, such as line, conic or
 * implicit curve
 *
 */
public interface EquationValue {
	/**
	 * @return equation
	 */
	public Equation getEquation();

	/**
	 * @return array of variables with nonzero cofficients
	 */
	public String[] getEquationVariables();

	/**
	 * Force output in user input form
	 */
	public void setToUser();

	public boolean setTypeFromXML(String style, String parameter);

	public void setToImplicit();
}
