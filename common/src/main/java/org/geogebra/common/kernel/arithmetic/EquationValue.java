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

	/**
	 * @param style
	 *            type from XML
	 * @param parameter
	 *            parameter name
	 * @param force
	 *            force equation form
	 * @return whether style is valid for this objecttype
	 */
	public boolean setTypeFromXML(String style, String parameter, boolean force);

	/**
	 * Implicit (expanded) equation
	 */
	public void setToImplicit();
}
