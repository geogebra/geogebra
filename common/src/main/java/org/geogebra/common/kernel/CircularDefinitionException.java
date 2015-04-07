package org.geogebra.common.kernel;

/**
 * Exception caused by circular definition
 */
public class CircularDefinitionException extends Exception {

	private static final long serialVersionUID = -1012964850092798314L;

	/**
	 * Creates new circular definition exception
	 */
	public CircularDefinitionException() {
		super("CircularDefinition");
	}

}
