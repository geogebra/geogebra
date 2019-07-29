package org.geogebra.common.main;

/**
 * Unbalanced brackets error
 */
public class BracketsError extends MyError {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @param loc
	 *            localization
	 * @param input
	 *            parsed string
	 */
	public BracketsError(Localization loc, String input) {
		super(loc, Errors.UnbalancedBrackets, input);
	}

}
