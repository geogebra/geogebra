package geogebra.common.kernel.arithmetic;

/**
 * Sets the assignment type of an expression or equation (or also NO assignment)
 * 
 * @author Zbynek, comments by Johannes
 */
public enum AssignmentType {
	/**
	 * no assignment in the expression/equation
	 */
	NONE,

	/**
	 * the default assignment (normally ":=" for functions and ":" for
	 * equations)
	 */
	DEFAULT,

	/**
	 * the assignment which delays the evaluation (":==")
	 */
	DELAYED
}
