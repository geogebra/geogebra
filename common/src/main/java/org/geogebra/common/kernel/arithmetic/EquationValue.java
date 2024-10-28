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
	 * Implicit (expanded) equation
	 */
	public void setToImplicit();

	/**
	 * Set the equation form from a string value coming from XML.
	 * @param equationForm
	 *            equation form (e.g., "implicit", "explicit", "user")
	 * @param parameter
	 *            parameter name
	 * @return whether equation form is valid for this objecttype
	 */
	public boolean setEquationFormFromXML(String equationForm, String parameter);
}
